/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ics3u_summative_tower_defense;

/** Lose screen Panel
 *
 * @author Logesh Mahendran
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class LosePanel extends JPanel {
    
    // Instance variables
    
    // Swing objects
    JLabel waveLabel;
    JButton returnToMenuButton;
    
    // Constructor
    public LosePanel(ActionListener l,int wave,int difficulty,int gameCode) {
        
        // Initializations
        this.setPreferredSize(new Dimension(900, 600));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // Sets offscreen buffering to be true (draws components off screen)
        this.setLayout(null);
        
        // Wave label
        waveLabel = new JLabel("You reached wave " + wave + ".");
        waveLabel.setBounds(0,300,900,40);
        waveLabel.setForeground(Color.WHITE);
        waveLabel.setFont(new Font("Helvetica",Font.BOLD,35));
        waveLabel.setHorizontalAlignment(JLabel.CENTER);
        this.add(waveLabel);
        
        // Return to menu button
        returnToMenuButton = new JButton();
        returnToMenuButton.setBounds(350,450,200,100);
        returnToMenuButton.setIcon(new ImageIcon(getClass().getResource("/img/return_to_menu_icon.png")));
        returnToMenuButton.setFocusPainted(false);
        returnToMenuButton.addActionListener(l);
        this.add(returnToMenuButton);
        
        // Delete save file of lost game
        if ((difficulty < 2 && wave > 0) || (difficulty == 2 && wave > 2)) {
            File gameFile = ExtraMethods.getSaveFile(String.valueOf(gameCode));
            if (gameFile.exists() && !gameFile.delete()) {
                System.out.println("Deletion of file \"" + gameCode + ".txt\" unsuccessful.");
            }
        }
        
    }
    
    // Override paintcomponent for background
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;
        
        g2.drawImage(ExtraMethods.loadImage(getClass().getResource("/img/lose_screen_background.png")),0,0,null);
        
    }
    
    
}
