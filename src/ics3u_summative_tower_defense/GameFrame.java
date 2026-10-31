/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ics3u_summative_tower_defense;

/** GameFrame class
 *
 * @author Logesh Mahendran
 */

// Imports
import javax.swing.*;

public class GameFrame extends JFrame {
    
    // Instance variables
    MainPanel mainPanel;
    
    public GameFrame() {
        
        // Set up defaults
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("Space Tower Defense");
        this.setIconImage(ExtraMethods.loadImage(getClass().getResource("/img/shooter_sprite_0.png")));
        
        // Make mainPanel
        mainPanel = new MainPanel();
        this.add(mainPanel);
        
        // Make sure size and position of JFrame is set
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
    }
    
}
