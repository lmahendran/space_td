/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ics3u_summative_tower_defense;

/** Pause panel class
 *
 * @author Logesh Mahendran
 */

// Imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PausePanel extends JPanel {
    
    // Instance variables
    
    // Swing objects
    JLabel codeLabel;
    JButton resumeButton, returnToMenuButton, musicToggleButton,effectsToggleButton;
    
    // Primitives
    private int musicIconState, effectsIconState;
    
    // Initializations
    public PausePanel(ActionListener l, int gameCode, boolean musicPlaying, boolean soundsPlaying) {
        this.setPreferredSize(new Dimension(900, 600));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // Sets offscreen buffering to be true (draws components off screen)
        this.setLayout(null);

        // Game code label
        codeLabel = new JLabel("Game Code: " + gameCode + ".");
        codeLabel.setBounds(0,260,900,40);
        codeLabel.setForeground(Color.WHITE);
        codeLabel.setFont(new Font("Helvetica",Font.BOLD,35));
        codeLabel.setHorizontalAlignment(JLabel.CENTER);
        this.add(codeLabel);

        // Resume button
        resumeButton = new JButton();
        resumeButton.setBounds(350,310,200,100);
        resumeButton.setIcon(new ImageIcon(getClass().getResource("/img/resume_icon.png")));
        resumeButton.setFocusPainted(false);
        resumeButton.addActionListener(l);
        this.add(resumeButton);
        
        // Return to menu button
        returnToMenuButton = new JButton();
        returnToMenuButton.setBounds(350,435,200,100);
        returnToMenuButton.setIcon(new ImageIcon(getClass().getResource("/img/return_to_menu_icon.png")));
        returnToMenuButton.setFocusPainted(false);
        returnToMenuButton.addActionListener(l);
        this.add(returnToMenuButton);
        
        // Toggle icons
        if (musicPlaying) {
            musicIconState = 0;
        } else {
            musicIconState = 1;
        }
        if (soundsPlaying) {
            effectsIconState = 0;
        } else {
            effectsIconState = 1;
        }
        
        // Music toggle button
        musicToggleButton = new JButton();
        musicToggleButton.setBounds(10,510,80,80);
        musicToggleButton.setFocusPainted(false);
        musicToggleButton.addActionListener(l);
        musicToggleButton.setIcon(new ImageIcon(getClass().getResource("/img/music_toggle_icon_" + musicIconState + ".png")));
        this.add(musicToggleButton);
        
        // Sound effects toggle button
        effectsToggleButton = new JButton();
        effectsToggleButton.setBounds(100,510,80,80);
        effectsToggleButton.setFocusPainted(false);
        effectsToggleButton.addActionListener(l);
        effectsToggleButton.setIcon(new ImageIcon(getClass().getResource("/img/effects_toggle_icon_" + effectsIconState + ".png")));
        this.add(effectsToggleButton);
        
    }
    
    // Override paintComponent for background
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;
        
        g2.drawImage(ExtraMethods.loadImage(getClass().getResource("/img/pause_background.png")),0,0,null);
    }
    
    // Toggle icon methods
    public void toggleMusicIcon() {
        musicIconState = 1-musicIconState;
        musicToggleButton.setIcon(new ImageIcon(getClass().getResource("/img/music_toggle_icon_" + musicIconState + ".png")));
    }
    
    public void toggleEffectsIcon() {
        effectsIconState = 1-effectsIconState;
        effectsToggleButton.setIcon(new ImageIcon(getClass().getResource("/img/effects_toggle_icon_" + effectsIconState + ".png")));
    }
    
}
