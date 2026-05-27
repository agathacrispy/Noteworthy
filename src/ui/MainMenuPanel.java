package ui;

import audio.AudioInputManager;
import loader.SongLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class MainMenuPanel extends JPanel {
    private static JButton recordButton;
    private static JButton playButton;
    private AudioInputManager AIM;
    private int mouseX = 600;
    private int mouseY = 400;

    public MainMenuPanel(){
        recordButton = new JButton("Start");
        recordButton.setForeground(new Color(0, 150, 255));
        recordButton.setOpaque(false);
        recordButton.setBorderPainted(false);
        recordButton.setContentAreaFilled(false);
        recordButton.setBounds((int) (575-50*Math.cos(Math.PI/1200.0*mouseX)), (int) (375-50*Math.cos(Math.PI/800.0*mouseY)), 50, 50);
        playButton = new JButton("Play");
        playButton.setForeground(new Color(191, 64, 191));
        playButton.setOpaque(false);
        playButton.setBorderPainted(false);
        playButton.setContentAreaFilled(false);
        playButton.setBounds((int) (575-50*Math.cos(Math.PI/1200.0*mouseX)), (int) (375-50*Math.cos(Math.PI/800.0*mouseY)), 50, 50);
        recordButton.addActionListener(e ->{
            if(!AIM.isRecording()){
                AIM.startRecording();
            } else {
                AIM.stopRecording();
            }
        });

        playButton.addActionListener(e ->{
            if(AIM.isPlayReady()) {
                AIM.playBack();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                mouseX = e.getX();
                mouseY = e.getY();
                recordButton.setBounds((int) (575-50*Math.cos(Math.PI/1200.0*mouseX)), (int) (375-50*Math.cos(Math.PI/800.0*mouseY)), 50, 50);
                playButton.setBounds((int) (575-50*Math.cos(Math.PI/1200.0*mouseX)), (int) (175-50*Math.cos(Math.PI/800.0*(mouseY+250))), 50, 50);
            }
        });
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        /*
        g2d.setColor(new Color(0, 150, 255));
        g2d.fillOval((int) (550-50*Math.cos(Math.PI/1200.0*mouseX)), (int) (350-50*Math.cos(Math.PI/800.0*mouseY)), 100, 100);
        g2d.setColor(new Color(0, 150, 255, 75));
        g2d.fillOval((int) (540-40*Math.cos(Math.PI/1200.0*mouseX)), (int) (340-40*Math.cos(Math.PI/800.0*mouseY)), 120, 120);
        g2d.setColor(new Color(0, 150, 255, 50));
        g2d.fillOval((int) (535-30*Math.cos(Math.PI/1200.0*mouseX)), (int) (335-30*Math.cos(Math.PI/800.0*mouseY)), 130, 130);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Start", (int) (585-50*Math.cos(Math.PI/1200.0*mouseX)), (int) (400-50*Math.cos(Math.PI/800.0*mouseY)));

        g2d.setColor(new Color(191, 64, 191));
        g2d.fillOval((int) (550-50*Math.cos(Math.PI/1200.0*mouseX)), (int) (150-50*Math.cos(Math.PI/800.0*(mouseY+250))), 100, 100);
        g2d.setColor(new Color(191, 64, 191, 75));
        g2d.fillOval((int) (540-40*Math.cos(Math.PI/1200.0*mouseX)), (int) (140-40*Math.cos(Math.PI/800.0*(mouseY+260))), 120, 120);
        g2d.setColor(new Color(191, 64, 191, 50));
        g2d.fillOval((int) (535-30*Math.cos(Math.PI/1200.0*mouseX)), (int) (135-30*Math.cos(Math.PI/800.0*(mouseY+265))), 130, 130);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Play", (int) (585-50*Math.cos(Math.PI/1200.0*mouseX)), (int) (200-50*Math.cos(Math.PI/800.0*(mouseY+250))));
         */
    }
}
