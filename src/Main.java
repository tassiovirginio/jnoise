import org.newdawn.easyogg.OggClip;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


public class Main {

    private static final String APPNAME = "jNoise";

    private static int noiseAtual = 0;

    private static File afile[] = null;

    private static OggClip ogg = null;

    private static TrayIcon trayIcon = null;

    private static PopupMenu popup = null;


    public static void main(String[] args) {
        carregarLista();

        if (SystemTray.isSupported()) {

            SystemTray tray = SystemTray.getSystemTray();

            ActionListener noiseslistener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    play(e.getActionCommand());
                }
            };

            ActionListener aboutlistener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, new MessageWithLink("Tássio Virgínio <br><br> <a href=\"https://github.com/tassiovirginio/jnoise\">https://github.com/tassiovirginio/jnoise</a>"),"About",JOptionPane.INFORMATION_MESSAGE);
                }
            };


            ActionListener exitListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            };

            ActionListener playlistener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    play();
                }
            };

            ActionListener nextlistener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    next();
                }
            };

            ActionListener backlistener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    back();
                }
            };

            ActionListener stoplistener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Image image = null;
                    try {
                        image = ImageIO.read(new File("sounds/jnoise.png"));
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }

                    trayIcon.setImage(image);
                    ogg.stop();
                }
            };

            ActionListener pauselistener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!ogg.isPaused())
                        ogg.pause();
                }
            };

            popup = new PopupMenu(APPNAME);

            PopupMenu popupNoises = new PopupMenu("Noises");
            for(File noise:afile){
                MenuItem menuItem = new MenuItem(noise.getName());
                menuItem.addActionListener(noiseslistener);
                popupNoises.add(menuItem);
            }

            popup.add(popupNoises);
            popup.addSeparator();

            MenuItem play = new MenuItem("Play");
            MenuItem stop = new MenuItem("Stop");
            MenuItem pause = new MenuItem("Pause");
            MenuItem next = new MenuItem("Next");
            MenuItem back = new MenuItem("Back");
            MenuItem sair = new MenuItem("Exit");
            MenuItem about = new MenuItem("About");

            sair.addActionListener(exitListener);
            play.addActionListener(playlistener);
            back.addActionListener(backlistener);
            next.addActionListener(nextlistener);
            stop.addActionListener(stoplistener);
            pause.addActionListener(pauselistener);
            about.addActionListener(aboutlistener);
            popup.add(play);
            popup.add(stop);
            popup.add(pause);
            popup.add(next);
            popup.add(back);
            popup.addSeparator();
            popup.add(about);
            popup.addSeparator();
            popup.add(sair);


            File input = new File("sounds/jnoise.png");
            Image image = null;
            try {
                image = ImageIO.read(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            trayIcon = new TrayIcon(image, "jNoise", popup);
            trayIcon.setImageAutoSize(true);


            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("Erro, TrayIcon não sera adicionado.");
            }

            carregarArquivo(null,false);

        } else {
            JOptionPane.showMessageDialog(null, "recurso ainda não esta disponível pra o seu sistema");

        }

    }

    public static void carregarLista() {
        File file = new File("sounds");
        afile = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toUpperCase().endsWith(".OGG");
            }
        });
    }

    private static void carregarArquivo(String fileName, boolean mudarIcone) {
        if(fileName==null) fileName = afile[noiseAtual].getName();
        try {
            File fileInput = new File("sounds/" + fileName);
            ogg = new OggClip(new FileInputStream(fileInput));
            if(mudarIcone)mudarIcone(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void mudarIcone(String fileName) {
        File imageInput = new File("sounds/" + fileName.replace(".ogg", ".png"));
        Image image = null;
        try {
            image = ImageIO.read(imageInput);
        } catch (IOException e) {
            e.printStackTrace();
        }

        popup.setLabel("jNoise - " + fileName);
        trayIcon.setToolTip("jNoise - " + fileName);
        trayIcon.setImage(image);

    }

    private static void play(String fileName) {
        ogg.stop();
        ogg.close();
        carregarArquivo(fileName,true);
        ogg.loop();
    }

    private static void play() {
        play(afile[noiseAtual].getName());
    }

    private static void next() {
        if(noiseAtual == afile.length-1)return;
        noiseAtual += 1;
        play();
    }

    private static void back() {
        if(noiseAtual == 0)return;
        noiseAtual -= 1;
        play();
    }

}


class MessageWithLink extends JEditorPane {
    private static final long serialVersionUID = 1L;

    public MessageWithLink(String htmlBody) {
        super("text/html", "<html><body style=\"" + getStyle() + "\">" + htmlBody + "</body></html>");
        addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    try {
                        java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        setEditable(false);
        setBorder(null);
    }

    static StringBuffer getStyle() {
        // for copying style
        JLabel label = new JLabel();
        Font font = label.getFont();
        Color color = label.getBackground();

        // create some css from the label's font
        StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
        style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
        style.append("font-size:" + font.getSize() + "pt;");
        style.append("background-color: rgb("+color.getRed()+","+color.getGreen()+","+color.getBlue()+");");
        return style;
    }
}