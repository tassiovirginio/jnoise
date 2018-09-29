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
import javax.swing.*;


public class Main {

    private static final String APPNAME = "jNoise";

    private static int noiseAtual = 0;

    private static File afile[] = null;

    private static OggClip ogg = null;

    private static TrayIcon trayIcon = null;

    private static PopupMenu popup = null;

    public static void carregarLista() {
        File file = new File("sounds");
        afile = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().toUpperCase().endsWith(".OGG");
            }
        });
    }

    private static void play() {
        ogg.stop();
        ogg.close();
        try {
            String fileName = afile[noiseAtual].getName();
            popup.setLabel(APPNAME + " - " + fileName.replace(".ogg", ""));
            File fileInput = new File("sounds/" + fileName);
            File imageInput = new File("sounds/" + fileName.replace(".ogg", ".png"));
            ogg = new OggClip(new FileInputStream(fileInput));
            Image image = null;
            try {
                image = ImageIO.read(imageInput);
            } catch (IOException e) {
                e.printStackTrace();
            }

            trayIcon.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ogg.loop();
    }

    private static void next() {
        ogg.stop();
        ogg.close();
        try {
            noiseAtual += 1;
            String fileName = afile[noiseAtual].getName();
            popup.setLabel(APPNAME + " - " + fileName.replace(".ogg", ""));
            File fileInput = new File("sounds/" + fileName);
            File imageInput = new File("sounds/" + fileName.replace(".ogg", ".png"));
            ogg = new OggClip(new FileInputStream(fileInput));
            Image image = null;
            try {
                image = ImageIO.read(imageInput);
            } catch (IOException e) {
                e.printStackTrace();
            }

            trayIcon.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ogg.loop();
    }

    private static void back() {
        ogg.stop();
        ogg.close();
        try {
            noiseAtual -= 1;
            String fileName = afile[noiseAtual].getName();
            popup.setLabel(APPNAME + " - " + fileName.replace(".ogg", ""));
            File fileInput = new File("sounds/" + fileName);
            File imageInput = new File("sounds/" + fileName.replace(".ogg", ".png"));
            ogg = new OggClip(new FileInputStream(fileInput));
            Image image = null;
            try {
                image = ImageIO.read(imageInput);
            } catch (IOException e) {
                e.printStackTrace();
            }

            trayIcon.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ogg.loop();
    }


    public static void main(String[] args) {
        carregarLista();

        if (SystemTray.isSupported()) {

            SystemTray tray = SystemTray.getSystemTray();


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

            MenuItem play = new MenuItem("Play");
            MenuItem stop = new MenuItem("Stop");
            MenuItem pause = new MenuItem("Pause");
            MenuItem next = new MenuItem("Next");
            MenuItem back = new MenuItem("Back");
            MenuItem sair = new MenuItem("Sair");

            sair.addActionListener(exitListener);
            play.addActionListener(playlistener);
            back.addActionListener(backlistener);
            next.addActionListener(nextlistener);
            stop.addActionListener(stoplistener);
            pause.addActionListener(pauselistener);

            popup.add(play);
            popup.add(stop);
            popup.add(pause);
            popup.add(next);
            popup.add(back);
            popup.addSeparator();
            popup.add(sair);

            PopupMenu popupNoises = new PopupMenu("Noises");
            for(File noise:afile){
                popupNoises.add(new MenuItem(noise.getName().replace(".ogg","")));
            }

            popup.add(popupNoises);


            File input = new File("sounds/jnoise.png");
            Image image = null;
            try {
                image = ImageIO.read(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            trayIcon = new TrayIcon(image, "jNoise", popup);

            ActionListener actionListener = new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    trayIcon.displayMessage("jNoise - About",
                            "https://github.com/tassiovirginio/jnoise.",
                            TrayIcon.MessageType.INFO);
                }

            };

            trayIcon.setImageAutoSize(true);

            trayIcon.addActionListener(actionListener);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("Erro, TrayIcon não sera adicionado.");
            }

            carregarArquivo();

        } else {
            JOptionPane.showMessageDialog(null, "recurso ainda não esta disponível pra o seu sistema");

        }

    }

    private static void carregarArquivo() {
        try {
            String fileName = afile[noiseAtual].getName();
            File fileInput = new File("sounds/" + fileName);
            ogg = new OggClip(new FileInputStream(fileInput));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}