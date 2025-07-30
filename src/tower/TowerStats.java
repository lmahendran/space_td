/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tower;

/** Tower stats class
 *
 * @author Logesh Mahendran
 * @courtesy FlatIcon for the arrow pictures for targeting panel
 */

// Imports
import ics3u_summative_tower_defense.ExtraMethods;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import layout.TableLayout;

public class TowerStats extends JPanel implements ActionListener {
    
    // Instance variables
    private int towerType, userCash, currentUpgrade, 
            nextUpgradeCost, targeting, sellPrice;
    private String[][] towerUpgrades, upgradeLabels;
    private String damageLabel;
    private boolean maxUpgrade, sold;
    
    // Static variables
    private static boolean[] maxUpgrades;
    private static int userDebt;
    
    // Swing objects
    JLabel towerNameLabel, damageDealtLabel, nextUpgradeLabel, nextUpgradeDesc, targetingLabel;
    JLabel[] statsLabels;
    JButton nextUpgradeButton,deleteButton;
    JButton[] targetingButtons;
    JPanel upgradeButtonPanel, modifierPanel, modifierBar,targetingPanel;
    JPanel[] modifierBarSections;
    
    // Plane-specific
    JButton changeDirectionButton, changePathButton;
    private int planePath,planeDirection;
    
    // Minigunner sepcifioc
    JButton changeMiniTargetButton;
    private boolean changeMiniTarget;
    
    // Constructor
    public TowerStats(int type) {
        super();
        
        // Getting number of stats based on tower type, then casting it to gridlayout
        int numStats;
        if (type == 0 || type == 3) {
            numStats = 3;
        } else if (type == 2 || type == 5 || type == 6) {
            numStats = 5;
        } else if (type == 4) {
            numStats = 6;
        } else {
            numStats = 4;
        }
        this.setLayout(new GridLayout(8+numStats,1));
        
        // Setting background
        Color background;
        switch (type) {
            case 0 -> background = new Color(144,238,144);
            case 1 -> background = new Color(231,181,255);
            case 2 -> background = new Color(190,255,255);
            case 3 -> background = new Color(255,200,255);
            case 4 -> background = new Color(255,190,147);
            case 5 -> background = new Color(255,170,170);
            default -> background = new Color(255,246,201);
        }
        this.setBackground(background);
        
        // Primitives initialization
        towerType = type;
        targeting = 0;
        maxUpgrade = false;
        maxUpgrades = new boolean[7];
        sold = false;
        sellPrice = (int)ExtraMethods.round(Tower.getCOSTS()[towerType]*0.25,0);
        for (int i = 0; i < 7; i++) {
            maxUpgrades[i] = false;
        }
        
        // Getting upgrades from file
        String[] upgradesTemp = ExtraMethods.readLine(getClass().getResource("/txt/tower_upgrades.txt"),towerType+1).split(",");
        towerUpgrades = new String[upgradesTemp.length][];
        for (int i = 0; i < 4; i++) {
            towerUpgrades[i] = upgradesTemp[i].split("/");
        }
        String[] upgradeLabelsTemp = ExtraMethods.readLine(getClass().getResource("/txt/upgrade_labels.txt"),towerType+1).split(",");
        upgradeLabels = new String[upgradeLabelsTemp.length][2];
        for (int i = 0; i < 4; i++) {
            upgradeLabels[i] = upgradeLabelsTemp[i].split(":");
        }
        
        // Tower Label
        String towerLabelText;
        switch (towerType) {
            case 0 -> towerLabelText = "Shooter";
            case 1 -> towerLabelText = "Caster";
            case 2 -> towerLabelText = "Booster";
            case 3 -> towerLabelText = "Deflator";
            case 4 -> towerLabelText = "Generator";
            case 5 -> towerLabelText = "Flyer";
            default -> towerLabelText = "Minigunner";
        }
        towerNameLabel = new JLabel(towerLabelText, SwingConstants.CENTER);
        towerNameLabel.setFont(new Font("Agency FB",Font.BOLD,20));
        towerNameLabel.setPreferredSize(new Dimension(130,20));
        this.add(towerNameLabel);
        
        // Damage dealt/cash made label
        if (towerType == 4) {
            damageLabel = "Cash made: $";
        } else {
            damageLabel = "Damage: ";
        }
        damageDealtLabel = new JLabel(damageLabel + "0", SwingConstants.CENTER);
        damageDealtLabel.setFont(new Font("Monospaced",Font.PLAIN,15));
        this.add(damageDealtLabel);
        
        // Plane-specific button
        if (towerType == 5) {
            planePath = 0;
            planeDirection = 1;
            changeDirectionButton = new JButton("Change Direction");
            changePathButton = new JButton("Path: Circle");
            changeDirectionButton.addActionListener(this);
            changePathButton.addActionListener(this);
            changeDirectionButton.setFocusPainted(false);
            changePathButton.setFocusPainted(false);
            this.add(changeDirectionButton);
            this.add(changePathButton);
        }
        
        // Miniguner-specific button
        if (towerType == 6) {
            changeMiniTarget = false;
            changeMiniTargetButton = new JButton("Change Target");
            changeMiniTargetButton.addActionListener(this);
            changeMiniTargetButton.setFocusPainted(false);
            this.add(changeMiniTargetButton);
        }
        
        // Stats labels
        if (towerType == 5) {
            statsLabels = new JLabel[numStats-2];
        } else if (towerType == 6) {
            statsLabels = new JLabel[numStats-1];
        } else {
            statsLabels = new JLabel[numStats];
        }
        for (int i = 0; i < statsLabels.length; i++) {
            statsLabels[i] = new JLabel("",SwingConstants.CENTER);
            statsLabels[i].setFont(new Font("Monospaced",Font.ITALIC,12));
            this.add(statsLabels[i]);
        }
        
        // Modifier bars
        double[][] modifierPanelConstraints = {
            {0.1,0.8,0.1},
            {0.25,0.5,0.25}
        };
        modifierPanel = new JPanel(new TableLayout(modifierPanelConstraints));
        modifierPanel.setBackground(background);
        modifierBar = new JPanel(new GridLayout(1,5));
        modifierBarSections = new JPanel[5];
        for (int i = 0; i < 5; i++) {
            modifierBarSections[i] = new JPanel();
            modifierBarSections[i].setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
            modifierBar.add(modifierBarSections[i]);
        }
        modifierPanel.add(modifierBar,"1,1");
        this.add(modifierPanel);
        
        // Upgrade labels
        nextUpgradeLabel = new JLabel();
        this.add(nextUpgradeLabel);
        nextUpgradeDesc = new JLabel();
        this.add(nextUpgradeDesc);
        
        double[][] upgradeButtonPanelConstraints = {
            {0.1,0.8,0.1},
            {0.1,0.8,0.1}
        };
        
        // Upgrade Button
        upgradeButtonPanel = new JPanel(new TableLayout(upgradeButtonPanelConstraints));
        upgradeButtonPanel.setBackground(background);
        nextUpgradeButton = new JButton();
        nextUpgradeButton.addActionListener(this);
        nextUpgradeButton.setFocusPainted(false);
        upgradeButtonPanel.add(nextUpgradeButton,"1,1");
        this.add(upgradeButtonPanel);
        
        // Targeting panel
        double[][] targetingPanelConstraints = {
            {0.25,0.5,0.25},
            {TableLayout.FILL}
        };
        targetingPanel = new JPanel(new TableLayout(targetingPanelConstraints));
        targetingPanel.setBackground(background);
        targetingLabel = new JLabel("First", SwingConstants.CENTER);
        targetingButtons = new JButton[2];
        targetingButtons[0] = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("/img/left_arrow.png"))
                    .getImage().getScaledInstance(10,10,0))); // courtesy Flaticon
        targetingButtons[1] = new JButton(new ImageIcon(new ImageIcon(getClass().getResource("/img/right_arrow.png"))
                    .getImage().getScaledInstance(10,10,0))); // courtesy Flaticon
        for (int i = 0; i < 2; i++) {
            targetingButtons[i].setFocusPainted(false);
            targetingButtons[i].addActionListener(this);
        }
        targetingPanel.add(targetingButtons[0],"0,0");
        targetingPanel.add(targetingLabel,"1,0");
        targetingPanel.add(targetingButtons[1],"2,0");
        this.add(targetingPanel);
        
        // Delete button
        deleteButton = new JButton("Sell: $" + sellPrice);
        deleteButton.setForeground(Color.RED);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(this);
        this.add(deleteButton);
        
        this.setVisible(false);
        
    }
    
    // Getter methods
    public String[][] getTowerUpgrades() {
        return towerUpgrades;
    }
    
    public int getCurrentUpgrade() {
        return currentUpgrade;
    }
    
    public int getNextUpgradeCost() {
        return nextUpgradeCost;
    }
    
    public int getTargeting() {
        return targeting;
    }
    
    public int getSellPrice() {
        return sellPrice;
    }
    
    public boolean getSold() {
        return sold;
    }
    
    public int getPlanePath() {
        return planePath;
    }
    
    public int getPlaneDirection() {
        return planeDirection;
    }
    
    public boolean getChangeMiniTarget() {
        return changeMiniTarget;
    }

    
    // Setter methods
    public void setChangeMiniTarget(boolean c) {
        changeMiniTarget = c;
    }
    
    public void setCurrentUpgrade(int c) {
        currentUpgrade = c;
    }
    
    public void setPlanePath(int p) {
        planePath = p;
    }
    
    public void setSellPrice(int s) {
        sellPrice = s;
    }
    
    // Static methods
    public static int getUserDebt() {
        return userDebt;
    }
    
    public static void setUserDebt(int u) {
        userDebt = u;
    }
    
    // ActionListener Interface methods
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (!changeMiniTarget) {
            // Upgrade button
            if (e.getSource() == nextUpgradeButton) {
                if (userCash >= nextUpgradeCost && !maxUpgrade) {
                    currentUpgrade++;
                    sellPrice += (int)ExtraMethods.round(nextUpgradeCost*0.25,0);
                    userDebt += nextUpgradeCost;
                }
            }

            // Left targeting button
            if (e.getSource() == targetingButtons[0] && towerType != 2) {
                targeting--;
                if (targeting < 0) {
                    targeting = 6;
                }
            }

            // Right targeting button
            if (e.getSource() == targetingButtons[1] && towerType != 2) {
                targeting++;
                if (targeting > 6) {
                    targeting = 0;
                }
            }
            
            // Delete button
            if (e.getSource() == deleteButton) {
                sold = true;
            }

            // Plane buttons
            if (e.getSource() == changePathButton) {
                planePath = 1-planePath;
            }

            if (e.getSource() == changeDirectionButton) {
                planeDirection *= -1;
            }
        }
        
        // Minigunner buttons
        if (e.getSource() == changeMiniTargetButton) {
            if (currentUpgrade >= 2) {
                changeMiniTarget = !changeMiniTarget;
            }
        }
        
    }
    
    
    // Other instance methods
    public void updateDisplay(int userCash, double damageDealt, String[] stats, 
            double[] modifiers, boolean hiddenVision) {
        damageDealtLabel.setText("<html>" + damageLabel + damageDealt + "</html>");
        
        // Update upgrade button
        this.userCash = userCash;
        if (userCash >= (int)(nextUpgradeCost*modifiers[2])) {
            nextUpgradeButton.setBackground(Color.GREEN);
        } else {
            nextUpgradeButton.setBackground(Color.GRAY);
        }
        
        // Upgrade other upgrade data
        if (currentUpgrade == 4 && !maxUpgrades[towerType]) {
            maxUpgrade = true;
            maxUpgrades[towerType] = true;
        } else if (currentUpgrade == 3 && maxUpgrades[towerType]) {
            maxUpgrade = true;
        }
        if (maxUpgrade) {
            nextUpgradeButton.setText("N/A");
            nextUpgradeLabel.setText("<html>Next upgrade:<br/>No more upgrades</html>");
            nextUpgradeDesc.setText("N/A");
        } else {
            nextUpgradeCost = Integer.parseInt(towerUpgrades[currentUpgrade][0]);
            nextUpgradeButton.setText("Buy: $" + (int)(nextUpgradeCost*modifiers[2]));
            int maxUpgradeAmount;
            if (maxUpgrades[towerType]) {
                maxUpgradeAmount = 3;
            } else {
                maxUpgradeAmount = 4;
            }
            nextUpgradeLabel.setText("<html>Next upgrade (" + (currentUpgrade+1) 
                    + "/" + maxUpgradeAmount + "):<br/>" + 
                    upgradeLabels[currentUpgrade][0] + "</html>");
            nextUpgradeDesc.setText(upgradeLabels[currentUpgrade][1]);
        }
        
        // Update stats
        for (int i = 0; i < stats.length; i++) {
            statsLabels[i].setText(stats[i]);
        }
        
        // Update modifier bar
        Color[] modifierColors = new Color[5];
        for (int i = 0; i < 4; i++) {
            if (modifiers[i] != 1) {
                switch (i) {
                    case 0 -> modifierColors[i] = Color.CYAN;
                    case 1 -> modifierColors[i] = new Color(216,191,216);
                    case 2 -> modifierColors[i] = new Color(255,204,0);
                    default -> modifierColors[i] = Color.RED;
                }
            } else {
                modifierColors[i] = Color.WHITE;
            }
        }
        if (hiddenVision) {
            modifierColors[4] = new Color(0,180,0);
        } else {
            modifierColors[4] = Color.WHITE;
        }
        for (int i = 0; i < 5; i++) {
            modifierBarSections[i].setBackground(modifierColors[i]);
        }
        
        // Updating targeting label
        String targetingText;
        switch (targeting) {
            case -1 -> targetingText = "None";
            case 0 -> targetingText = "First";
            case 1 -> targetingText = "Last";
            case 2 -> targetingText = "Weakest";
            case 3 -> targetingText = "Strongest";
            case 4 -> targetingText = "Closest";
            case 5 -> targetingText = "Furthest";
            default -> targetingText = "Random";
        }
        targetingLabel.setText(targetingText);
        
        // Updating sell price label
        deleteButton.setText("Sell: $" + sellPrice);
        
        // Plane updates
        if (towerType == 5) {
            if (planePath == 0) {
                changePathButton.setText("Path: Circle");
            } else {
                changePathButton.setText("Path: Figure Eight");
            }
        }
        
        // Minigunner updates
        if (towerType == 6) {
            if (changeMiniTarget) {
                changeMiniTargetButton.setText("Click new target...");
            } else {
                changeMiniTargetButton.setText("Change Target");
            }
        }
        
    }
    
    
    
    
}
