/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ics3u_summative_tower_defense;

/** Title panel class
 *
 * @author Logesh Mahendran
 * @coauthor Colson Puchala
 */

// Imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import info.clearthought.layout.TableLayout;

public class TitlePanel extends JLayeredPane implements ActionListener {

    // Instance variables
    
    // Objects
    private AudioInputStream audioStream;
    private Clip menuClip;
    private FloatControl volumeControl;
    
    // Swing objects
    JButton playButton,towerButton,howToPlayButton,creditsButton, removeCredits,
            removeHowToPlay;
    JLabel howToPlayTitle,howToPlayContent,creditsTitle,creditsContent;
    JPanel buttonPanel, creditsPanel,howToPlayPanel;
    
    
    public TitlePanel(ActionListener l) {
        
        // Title Panel configuration
        this.setPreferredSize(new Dimension(900, 600));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // Sets offscreen buffering to be true (draws components off screen)
        this.setLayout(null);
        
        // Menu music
        // Background music
        try {
            audioStream = AudioSystem.getAudioInputStream(getClass().getResource("/sound/galactic_rap.wav"));
            menuClip = AudioSystem.getClip();
            menuClip.open(audioStream);
            volumeControl = (FloatControl)menuClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-15f);
            menuClip.start();
            menuClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {}
        
        // Buttons
        double[][] buttonPanelConstraints = {
            {0.25,0.125,0.25,0.125,0.25},
            {0.45,0.1,0.45}
        };
        buttonPanel = new JPanel(new TableLayout(buttonPanelConstraints));
        buttonPanel.setBounds(150,350,600,200);
        buttonPanel.setBackground(new Color(0,0,0,0));
        this.add(buttonPanel,JLayeredPane.MODAL_LAYER);
        
        // Play button (300x90)
        playButton = new JButton();
        playButton.setIcon(new ImageIcon(getClass().getResource("/img/play_button_icon.png")));
        playButton.setFocusPainted(false);
        playButton.addActionListener(l);
        buttonPanel.add(playButton,"1,0,3,0");
        
        // Towers button (150x90)
        towerButton = new JButton();
        towerButton.setIcon(new ImageIcon(getClass().getResource("/img/tower_button_icon.png")));
        towerButton.setFocusPainted(false);
        towerButton.addActionListener(l);
        buttonPanel.add(towerButton,"0,2");
        
        // How to play button (150x90)
        howToPlayButton = new JButton();
        howToPlayButton.setIcon(new ImageIcon(getClass().getResource("/img/how_to_play_icon.png")));
        howToPlayButton.setFocusPainted(false);
        howToPlayButton.addActionListener(this);
        buttonPanel.add(howToPlayButton,"2,2");
        
        // Credits button (150x90)
        creditsButton = new JButton();
        creditsButton.setIcon(new ImageIcon(getClass().getResource("/img/credits_button_icon.png")));
        creditsButton.setFocusPainted(false);
        creditsButton.addActionListener(this);
        buttonPanel.add(creditsButton,"4,2");
        
//        buttonPanel.setVisible(true);
        
        
        // Credits panel
        double[][] creditsConstraints = {
            {10,260,40,10},
            {10,40,230,10}
        };
        creditsPanel = new JPanel(new TableLayout(creditsConstraints));
        creditsPanel.setBounds(290,155,320,290);
        creditsPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN,2));
        creditsPanel.setBackground(new Color(200,255,200));
        
        removeCredits = new JButton();
        removeCredits.setBackground(Color.RED);
        removeCredits.setFocusPainted(false);
        removeCredits.setIcon(new ImageIcon(getClass().getResource("/img/x_icon.png")));
        removeCredits.addActionListener(this);
        creditsPanel.add(removeCredits,"2,1");
        
        creditsTitle = new JLabel("Credits");
        creditsTitle.setFont(new Font("Helvetica",Font.BOLD,30));
        creditsPanel.add(creditsTitle,"1,1");
        
        creditsContent = new JLabel(ExtraMethods.readLine(getClass().getResource("/txt/game_info_file.txt"), 4));
        creditsContent.setFont(new Font("Monospaced",Font.PLAIN,12));
        creditsContent.setVerticalAlignment(JLabel.TOP);
        creditsPanel.add(creditsContent,"1,2,2,2");
        
        creditsPanel.setVisible(false);
        this.add(creditsPanel,JLayeredPane.DRAG_LAYER);
        
        // How to play panel
        double[][] howToPlayConstraints = {
            {10,260,40,10},
            {10,40,230,10}
        };
        howToPlayPanel = new JPanel(new TableLayout(howToPlayConstraints));
        howToPlayPanel.setBounds(290,155,320,290);
        howToPlayPanel.setBorder(BorderFactory.createLineBorder(Color.MAGENTA,2));
        howToPlayPanel.setBackground(new Color(255,200,255));
        
        removeHowToPlay = new JButton();
        removeHowToPlay.setBackground(Color.RED);
        removeHowToPlay.setFocusPainted(false);
        removeHowToPlay.setIcon(new ImageIcon(getClass().getResource("/img/x_icon.png")));
        removeHowToPlay.addActionListener(this);
        howToPlayPanel.add(removeHowToPlay,"2,1");
        
        howToPlayTitle = new JLabel("How to Play");
        howToPlayTitle.setFont(new Font("Helvetica",Font.BOLD,30));
        howToPlayPanel.add(howToPlayTitle,"1,1");
        
        howToPlayContent = new JLabel(ExtraMethods.readLine(getClass().getResource("/txt/game_info_file.txt"), 3));
        howToPlayContent.setFont(new Font("Monospaced",Font.PLAIN,12));
        howToPlayContent.setVerticalAlignment(JLabel.TOP);
        howToPlayPanel.add(howToPlayContent,"1,2,2,2");
        
        howToPlayPanel.setVisible(false);
        this.add(howToPlayPanel,JLayeredPane.DRAG_LAYER);
        
    }
    
    // Stop music
    public void stopMenuMusic() {
        menuClip.stop();
    }
    
    // Override paintComponent to draw background
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.drawImage(ExtraMethods.loadImage(getClass()
                .getResource("/img/title_background.png")),0,0,null);
    }
    
    // ActionListener Interface method
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Credits
        if (e.getSource() == creditsButton) {
            buttonPanel.setVisible(false);
            creditsPanel.setVisible(true);
        }
        
        // Remove credits
        if (e.getSource() == removeCredits) {
            buttonPanel.setVisible(true);
            creditsPanel.setVisible(false);
        }
        
        // How to play
        if (e.getSource() == howToPlayButton) {
            buttonPanel.setVisible(false);
            howToPlayPanel.setVisible(true);
        }
        
        // Remove how to play
        if (e.getSource() == removeHowToPlay) {
            buttonPanel.setVisible(true);
            howToPlayPanel.setVisible(false);
        }
        
        
    }
    
}
