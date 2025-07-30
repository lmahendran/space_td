/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ics3u_summative_tower_defense;

/** Tower Panel class
 *
 * @author Logesh Mahendran
 */

// Imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import layout.TableLayout;
import tower.Tower;

public class TowerPanel extends JLayeredPane implements ActionListener {
    
    // Instance variables
    
    // Swing objects
    JPanel upgradePanel;
    JButton backToTitleButton;
    JButton[] arrowButtons,upgradeButtons;
    JLabel screenTitle,towerTitle,towerCostLabel,upgradesTitle,upgradeDescriptionLabel;
    JLabel[] upgradeArrows;
    
    // Primitives
    private int viewedTower,selectedUpgrade;
    private String viewedTowerName;
    private Color viewedTowerBackground;
    private String[] towerNames = {
        "Shooter","Caster","Booster","Deflator","Generator","Flyer","Minigunner"
    };
    private Color[] towerBackgroundColors = {
        new Color(144,238,144),new Color(231,181,255),new Color(190,255,255),
        new Color(255,200,255),new Color(255,190,147),new Color(255,170,170),
        new Color(255,246,201)
    };
    
    public TowerPanel(ActionListener l) {
        
        // Tower Panel configuration
        this.setPreferredSize(new Dimension(900, 600));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // Sets offscreen buffering to be true (draws components off screen)
        this.setLayout(null);
        
        // Back to title button
        backToTitleButton = new JButton();
        backToTitleButton.setBounds(3,3,80,40);
        backToTitleButton.setIcon(new ImageIcon(getClass().getResource("/img/back_button_icon.png")));
        backToTitleButton.setFocusPainted(false);
        backToTitleButton.addActionListener(l);
        this.add(backToTitleButton);
        
        // Screen title
        screenTitle = new JLabel("TOWERS");
        screenTitle.setForeground(Color.WHITE);
        screenTitle.setBounds(10,50,200,40);
        screenTitle.setFont(new Font("Helvetica",Font.BOLD,40));
        this.add(screenTitle);
        
        // Arrow buttons
        viewedTower = 0;
        viewedTowerName = "shooter";
        viewedTowerBackground = towerBackgroundColors[0];
        arrowButtons = new JButton[2];
        for (int i = 0; i < 2; i++) {
            arrowButtons[i] = new JButton();
            arrowButtons[i].setFocusPainted(false);
            arrowButtons[i].addActionListener(this);
            this.add(arrowButtons[i]);
        }
        arrowButtons[0].setBounds(50,290,100,100);
        arrowButtons[1].setBounds(750,290,100,100);
        
        // Upgrade panel
        double[][] upgradePanelConstraints = {
            {50,50,50,50,50,50,50,50,50},
            {40,5,20,5,25,5,50,5,325}
        };
        // 0 - Tower name
        // 2 - Tower cost
        // 4 - Upgrades title
        // 6 - Upgrade buttons and arrows
        // 8 - Upgrade name
        // 10 - Upgrade cost
        // 12 - Upgrade description
        upgradePanel = new JPanel(new TableLayout(upgradePanelConstraints));
        upgradePanel.setBounds(225,60,450,480);
        upgradePanel.setOpaque(false);
        
        // Tower Title
        towerTitle = new JLabel();
        towerTitle.setFont(new Font("Helvetica",Font.BOLD,35));
        towerTitle.setForeground(Color.WHITE);
        towerTitle.setHorizontalAlignment(JLabel.CENTER);
        upgradePanel.add(towerTitle,"0,0,8,0");
        
        // Tower cost
        towerCostLabel = new JLabel();
        towerCostLabel.setFont(new Font("Helvetica",Font.BOLD,15));
        towerCostLabel.setForeground(Color.WHITE);
        towerCostLabel.setHorizontalAlignment(JLabel.CENTER);
        upgradePanel.add(towerCostLabel,"0,2,8,2");
        
        // Upgrades title
        upgradesTitle = new JLabel("<html><u>Upgrades</u></html>");
        upgradesTitle.setFont(new Font("Helvetica",Font.BOLD,20));
        upgradesTitle.setForeground(Color.WHITE);
        upgradesTitle.setHorizontalAlignment(JLabel.CENTER);
        upgradePanel.add(upgradesTitle,"0,4,8,4");
        
        // Upgrade buttons
        selectedUpgrade = 0;
        upgradeButtons = new JButton[5];
        for (int i = 0; i < 5; i++) {
            upgradeButtons[i] = new JButton();
            upgradeButtons[i].setBackground(viewedTowerBackground);
            upgradeButtons[i].setIcon(new ImageIcon(getClass()
                    .getResource("/img/" + viewedTowerName.toLowerCase() + "_sprite_" + i + ".png")));
            upgradeButtons[i].addActionListener(this);
            upgradeButtons[i].setFocusPainted(false);
            upgradePanel.add(upgradeButtons[i],(2*i)+",6");
        }
        
        // Upgrade arrows
        upgradeArrows = new JLabel[4];
        for (int i = 0; i < 4; i++) {
            upgradeArrows[i] = new JLabel("→");
            upgradeArrows[i].setHorizontalAlignment(JLabel.CENTER);
            upgradeArrows[i].setForeground(Color.WHITE);
            upgradeArrows[i].setFont(new Font("Helvetica",Font.BOLD,40));
            upgradePanel.add(upgradeArrows[i],(2*i+1)+",6");
        }
        
        // Upgrade description
        upgradeDescriptionLabel = new JLabel();
        upgradeDescriptionLabel.setFont(new Font("Helvetica",Font.PLAIN,15));
        upgradeDescriptionLabel.setForeground(Color.WHITE);
        upgradeDescriptionLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE,3,true));
        upgradeDescriptionLabel.setVerticalAlignment(JLabel.TOP);
        upgradePanel.add(upgradeDescriptionLabel,"0,8,8,8");
        
        this.add(upgradePanel,JLayeredPane.PALETTE_LAYER);
        
        // Fire action on first button to update everything
        upgradeButtons[0].doClick();
        
    }
    
    // Override paintComponent for background
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;
        
        g2.drawImage(ExtraMethods.loadImage(getClass()
                .getResource("/img/new_game_background.png")),0,0,null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        boolean fireUpdate = false;
        
        // Switching viewed tower
        if (e.getSource() == arrowButtons[0]) {
            viewedTower--;
            selectedUpgrade = 0;
            fireUpdate = true;
        }
        
        if (e.getSource() == arrowButtons[1]) {
            viewedTower++;
            selectedUpgrade = 0;
            fireUpdate = true;
        }
        
        // Upgrade button pressed
        for (int i = 0; i < 5; i++) {
            if (e.getSource() == upgradeButtons[i]) {
                selectedUpgrade = i;
                fireUpdate = true;
            }
        }
        
        // Update all labels and buttons
        if (fireUpdate) {
            // Update variables based on currently viewed tower
            if (viewedTower < 0) {
                viewedTower += 7;
            } else if (viewedTower > 6) {
                viewedTower -= 7;
            }
            viewedTowerName = towerNames[viewedTower];
            viewedTowerBackground = towerBackgroundColors[viewedTower];
            
            // Updating tower title and cost
            towerTitle.setText("<html><u>" + viewedTowerName + "</u></html>");
            towerTitle.setForeground(viewedTowerBackground);
            towerCostLabel.setText("Cost: $" + Tower.getCOSTS()[viewedTower]);
            towerCostLabel.setForeground(viewedTowerBackground);
            
            // Updating upgrades title
            upgradesTitle.setForeground(viewedTowerBackground);
            
            // Updating upgrade images and arrows
            for (int i = 0; i < 5; i++) {
                upgradeButtons[i].setBackground(viewedTowerBackground);
                upgradeButtons[i].setIcon(new ImageIcon(getClass()
                        .getResource("/img/" + viewedTowerName.toLowerCase() + "_sprite_" + i + ".png")));
                if (i < 4) {
                    upgradeArrows[i].setForeground(viewedTowerBackground);
                }
            }
            upgradeDescriptionLabel.setBorder(BorderFactory.createLineBorder(viewedTowerBackground,3,true));
            
            // Update arrow buttons
            int beforeTower = viewedTower-1;
            if (beforeTower < 0) {
                beforeTower += 7;
            }
            int afterTower = viewedTower+1;
            if (afterTower > 6) {
                afterTower -= 7;
            }
            
            arrowButtons[0].setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/img/" + towerNames[beforeTower]
                    .toLowerCase()+"_sprite_0.png")).getImage().getScaledInstance(80,80,0)));
            arrowButtons[0].setBackground(towerBackgroundColors[beforeTower]);
            arrowButtons[1].setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/img/" + towerNames[afterTower]
                    .toLowerCase()+"_sprite_0.png")).getImage().getScaledInstance(80,80,0)));
            arrowButtons[1].setBackground(towerBackgroundColors[afterTower]);
            
            // Updating tower description
            String viewedTowerUpgradeName,viewedTowerUpgradeCost;
            if (selectedUpgrade > 0) {
                viewedTowerUpgradeName = ExtraMethods.readLine(getClass().getResource("/txt/upgrade_labels.txt"),viewedTower+1)
                        .split(",")[selectedUpgrade-1].split(":")[0] +
                        " (" + selectedUpgrade + "/4)";
                viewedTowerUpgradeCost = "$" + ExtraMethods.readLine(getClass().getResource("/txt/tower_upgrades.txt"),viewedTower+1)
                        .split(",")[selectedUpgrade-1].split("/")[0];
            } else {
                viewedTowerUpgradeName = "N/A (0/4)";
                viewedTowerUpgradeCost = "N/A";
            }
            String statsTitle = "New stats";
            if (selectedUpgrade == 0) {
                statsTitle = "Base stats";
            }
            upgradeDescriptionLabel.setForeground(viewedTowerBackground);
            upgradeDescriptionLabel.setText(
                    "<html><b><u>Upgrade name:</b></u> " + viewedTowerUpgradeName + 
                    "<br><b><u>Upgrade cost:</b></u> " + viewedTowerUpgradeCost +
                    "<br><br><b><u>" + statsTitle + "</b></u><br>" +
                    ExtraMethods.readLine(getClass().getResource("/tower_info/" + viewedTowerName.toLowerCase()+"_desc.txt"),selectedUpgrade+1)
            );
        }
        
    }
    
}
