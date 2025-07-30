/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ics3u_summative_tower_defense;

/** New Game panel class
 *
 * @author Logesh Mahendran
 */

// Imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Clock;
import layout.TableLayout;

public class NewGamePanel extends JLayeredPane implements Runnable,ActionListener,
        KeyListener {

    // Instance variables
    
    // Objects
    Thread panelThread;
    Clock panelClock;
    
    // Swing objects
    JButton beginButton,randomMapButton,codeBeginButton,codeInfoButton,removeCodeInfo,
            backToTitleButton,generalInfoButton,removeGeneralInfo;
    JButton[] chooseMapButtons, difficultyButtons;
    JLabel titleLabel,difficultyTitleLabel,mapTitleLabel,codeLabel,codeTitleLabel,
            codeInfoTitle,codeInfoContent,generalInfoTitle,generalInfoContent,
            codeResponsePopup;
    JPanel mapButtonPanel,difficultyButtonPanel,codePanel,codeInfoPanel,
            generalInfoPanel;
    JTextField codeField;
    
    // Primitives
    private int difficulty, map;
    private int invalidCodeAttempt;
    // -1 for no map, 0 for random, 1-4 for provided maps
    private long[] popupTimer;
    
    
    public NewGamePanel(ActionListener l) {
        
        // Clock
        panelClock = Clock.systemDefaultZone();
        
        // Title Panel configuration
        this.setPreferredSize(new Dimension(900, 600));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // Sets offscreen buffering to be true (draws components off screen)
        this.setLayout(null);
        
        // Title Label
        titleLabel = new JLabel("<html>New game!</html>");
        titleLabel.setFont(new Font("Helvetica",Font.BOLD,27));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(20,10,270,100);
        this.add(titleLabel);
        
        // Back to Title
        backToTitleButton = new JButton();
        backToTitleButton.setBounds(3,3,80,40);
        backToTitleButton.setIcon(new ImageIcon(getClass().getResource("/img/back_button_icon.png")));
        backToTitleButton.setFocusPainted(false);
        backToTitleButton.addActionListener(l);
        this.add(backToTitleButton);
        
        // Begin button
        beginButton = new JButton();
        beginButton.setIcon(new ImageIcon(getClass().getResource("/img/begin_game_icon.png")));
        beginButton.addActionListener(l);
        beginButton.setBounds(675,25,200,80);
        beginButton.setFocusPainted(false);
        this.add(beginButton);
        
        // Difficulty buttons
        double[][] difficultyButtonPanelConstraints = {
            {100,20,100,20,100,20,100},
            {20,5,60}
        };
        difficulty = 0;
        difficultyButtonPanel = new JPanel(new TableLayout(difficultyButtonPanelConstraints));
        difficultyButtonPanel.setBounds(200,20,460,85);
        difficultyButtonPanel.setOpaque(false);
        difficultyTitleLabel = new JLabel("Difficulty:");
        difficultyTitleLabel.setFont(new Font("Helvetica",Font.BOLD,18));
        difficultyTitleLabel.setForeground(Color.WHITE);
        difficultyButtonPanel.add(difficultyTitleLabel,"0,0,4,0");
        difficultyButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            difficultyButtons[i] = new JButton();
            ImageIcon difficultyIcon;
            switch (i) {
                case 0 -> difficultyIcon = new ImageIcon(getClass().getResource("/img/difficulty_icon_0_1.png"));
                case 1 -> difficultyIcon = new ImageIcon(getClass().getResource("/img/difficulty_icon_1_0.png"));
                case 2 -> difficultyIcon = new ImageIcon(getClass().getResource("/img/difficulty_icon_2_0.png"));
                default -> difficultyIcon = new ImageIcon(getClass().getResource("/img/difficulty_icon_3_0.png"));
            }
            difficultyButtons[i].setIcon(difficultyIcon);
            difficultyButtons[i].addActionListener(this);
            difficultyButtons[i].setFocusPainted(false);
            difficultyButtonPanel.add(difficultyButtons[i],(2*i) + ",2");
        }
        this.add(difficultyButtonPanel,JLayeredPane.MODAL_LAYER);
        
        
        // Map button panel
        double[][] mapButtonPanelConstraints = {
            {5,240,20,240,20,240,5},
            {20,5,180,20,180,5}
        };
        map = -1;
        mapButtonPanel = new JPanel(new TableLayout(mapButtonPanelConstraints));
        mapButtonPanel.setBounds(60,100,770,410);
        mapButtonPanel.setOpaque(false);
        mapTitleLabel = new JLabel(" Maps:");
        mapTitleLabel.setFont(new Font("Helvetica",Font.BOLD,18));
        mapTitleLabel.setForeground(Color.WHITE);
        mapButtonPanel.add(mapTitleLabel,"0,0,4,0");
        chooseMapButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            chooseMapButtons[i] = new JButton();
            String panelLocation;
            switch (i) {
                case 0 -> panelLocation = "1,2";
                case 1 -> panelLocation = "3,2";
                case 2 -> panelLocation = "1,4";
                default -> panelLocation = "3,4";
            }
            chooseMapButtons[i].setIcon(new ImageIcon(getClass().getResource("/img/map_selection_icon_" + i + "_0.png")));
            chooseMapButtons[i].addActionListener(this);
            mapButtonPanel.add(chooseMapButtons[i],panelLocation);
        }
        randomMapButton = new JButton();
        randomMapButton.setIcon(new ImageIcon(getClass().getResource("/img/random_button_icon_0.png")));
        randomMapButton.addActionListener(this);
        randomMapButton.setFocusPainted(false);
        mapButtonPanel.add(randomMapButton,"5,2,5,4");
        this.add(mapButtonPanel,JLayeredPane.PALETTE_LAYER);
        
        // Code components
        double[][] codePanelConstraints = {
            {150,10,150,20,100,10,40},
            {40,40}
        };
        codePanel = new JPanel(new TableLayout(codePanelConstraints));
        codePanel.setOpaque(false);
        codePanel.setBounds(25,510,480,80);
        // Code title label
        codeTitleLabel = new JLabel("Or... Load a game");
        codeTitleLabel.setFont(new Font("Helvetica",Font.BOLD,25));
        codeTitleLabel.setForeground(Color.WHITE);
        codePanel.add(codeTitleLabel,"0,0,6,0");
        // Code label
        codeLabel = new JLabel("Enter Code: ");
        codeLabel.setForeground(Color.WHITE);
        codeLabel.setFont(new Font("Helvetica",Font.PLAIN,25));
        codePanel.add(codeLabel,"0,1");
        // Code field
        codeField = new JTextField();
        codeField.setHorizontalAlignment(JTextField.CENTER);
        codeField.addKeyListener(this);
        codeField.setBackground(Color.LIGHT_GRAY);
        codeField.setFont(new Font("Helvetica",Font.BOLD,20));
        codePanel.add(codeField,"2,1");
        // Begin from code button
        codeBeginButton = new JButton();
        codeBeginButton.setIcon(new ImageIcon(getClass().getResource("/img/code_begin_icon.png")));
        codeBeginButton.addActionListener(l);
        codeBeginButton.setFocusPainted(false);
        codePanel.add(codeBeginButton,"4,1");
        this.add(codePanel,JLayeredPane.PALETTE_LAYER);
        // Code info button
        codeInfoButton = new JButton();
        codeInfoButton.setIcon(new ImageIcon(getClass().getResource("/img/code_info_icon.png")));
        codeInfoButton.addActionListener(this);
        codePanel.add(codeInfoButton,"6,1");
        
        // Code info panel
        double[][] codeInfoConstraints = {
            {10,260,40,10},
            {10,40,180,10}
        };
        codeInfoPanel = new JPanel(new TableLayout(codeInfoConstraints));
        codeInfoPanel.setBounds(290,180,320,240);
        codeInfoPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE,2));
        codeInfoPanel.setBackground(new Color(200,200,255));
        // Remove code info button
        removeCodeInfo = new JButton();
        removeCodeInfo.setBackground(Color.RED);
        removeCodeInfo.setFocusPainted(false);
        removeCodeInfo.setIcon(new ImageIcon(getClass().getResource("/img/x_icon.png")));
        removeCodeInfo.addActionListener(this);
        codeInfoPanel.add(removeCodeInfo,"2,1");
        // Code info title
        codeInfoTitle = new JLabel("Code Info");
        codeInfoTitle.setFont(new Font("Helvetica",Font.BOLD,30));
        codeInfoPanel.add(codeInfoTitle,"1,1");
        // Code info content
        codeInfoContent = new JLabel(ExtraMethods.readLine(getClass().getResource("/txt/game_info_file.txt"), 1));
        codeInfoContent.setFont(new Font("Monospaced",Font.PLAIN,12));
        codeInfoPanel.add(codeInfoContent,"1,2,2,2");
        
        codeInfoPanel.setVisible(false);
        this.add(codeInfoPanel,JLayeredPane.POPUP_LAYER);
        
        // General Info panel
        double[][] generalInfoConstraints = {
            {10,260,40,10},
            {10,40,220,10}
        };
        generalInfoButton = new JButton();
        generalInfoButton.setIcon(new ImageIcon(getClass().getResource("/img/general_info_icon.png")));
        generalInfoButton.setBounds(840,125,40,40);
        generalInfoButton.addActionListener(this);
        generalInfoButton.setFocusPainted(false);
        this.add(generalInfoButton);
        
        generalInfoPanel = new JPanel(new TableLayout(generalInfoConstraints));
        generalInfoPanel.setBounds(290,160,320,280);
        generalInfoPanel.setBorder(BorderFactory.createLineBorder(Color.RED,2));
        generalInfoPanel.setBackground(new Color(255,200,200));
        // Remove general info button
        removeGeneralInfo = new JButton();
        removeGeneralInfo.setBackground(Color.RED);
        removeGeneralInfo.setFocusPainted(false);
        removeGeneralInfo.setIcon(new ImageIcon(getClass().getResource("/img/x_icon.png")));
        removeGeneralInfo.addActionListener(this);
        generalInfoPanel.add(removeGeneralInfo,"2,1");
        // General info title
        generalInfoTitle = new JLabel("General Info");
        generalInfoTitle.setFont(new Font("Helvetica",Font.BOLD,30));
        generalInfoPanel.add(generalInfoTitle,"1,1");
        // General info content
        generalInfoContent = new JLabel(ExtraMethods.readLine(getClass().getResource("/txt/game_info_file.txt"), 2));
        generalInfoContent.setFont(new Font("Monospaced",Font.PLAIN,12));
        generalInfoPanel.add(generalInfoContent,"1,2,2,2");
        
        generalInfoPanel.setVisible(false);
        this.add(generalInfoPanel,JLayeredPane.POPUP_LAYER);
        
        // Code response popup label
        codeResponsePopup = new JLabel("Invalid code!");
        codeResponsePopup.setForeground(Color.RED);
        codeResponsePopup.setFont(new Font("Helvetica",Font.PLAIN,30));
        codeResponsePopup.setBounds(550,550,300,40);
        this.add(codeResponsePopup);
        popupTimer = new long[2];
        popupTimer[0] = panelClock.millis();
        
        // Thread
        panelThread = new Thread(this);
        panelThread.start();
       
    }
    
    // Getters
    public int getDifficulty() {
        return difficulty;
    }
    
    public int getMap() {
        return map;
    }
    
    // Setters
    public void setDifficulty(int d) {
        difficulty = d;
    }
    
    public void setMap(int m) {
        map = m;
    }
    
    public void setInvalidCodeAttempt(int i) {
        invalidCodeAttempt = i;
    }
    
    // Override paintComponent for background
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;
        
        g2.drawImage(ExtraMethods.loadImage(getClass()
                .getResource("/img/new_game_background.png")),0,0,null);
    }
    
    
    // Override setVisible to account for run method
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (!aFlag) {
            panelThread = null;
            popupTimer[0] = popupTimer[1];
            codeResponsePopup.setText("");
        } else {
            panelThread = new Thread(this);
            panelThread.start();
        }
    }
    
    // Set all buttons non-editable (when there is a popup)
    public void setComponents(boolean active) {
        for (int i = 0; i < 3; i++) {
            difficultyButtons[i].setDisabledIcon(difficultyButtons[i].getIcon());
            difficultyButtons[i].setEnabled(active);
        }
        for (int i = 0; i < 4; i++) {
            chooseMapButtons[i].setDisabledIcon(chooseMapButtons[i].getIcon());
            chooseMapButtons[i].setEnabled(active);
        }
        randomMapButton.setDisabledIcon(randomMapButton.getIcon());
        randomMapButton.setEnabled(active);
        beginButton.setDisabledIcon(beginButton.getIcon());
        beginButton.setEnabled(active);
        codeBeginButton.setDisabledIcon(codeBeginButton.getIcon());
        codeBeginButton.setEnabled(active);
        codeField.setEditable(active);
        codeInfoButton.setDisabledIcon(codeInfoButton.getIcon());
        codeInfoButton.setEnabled(active);
        generalInfoButton.setDisabledIcon(generalInfoButton.getIcon());
        generalInfoButton.setEnabled(active);
    }
    
    // Run method
    @Override
    public void run() {
        
        // Exit condition
        while (panelThread != null) {
            
            // Update popup timer so that popup goes away after one second
            popupTimer[1] = panelClock.millis();
            if (popupTimer[1] - popupTimer[0] >= 1000) {
                codeResponsePopup.setText("");
            }
            
            // If an invalid attempt to load a code is found
            if (invalidCodeAttempt == 1) {
                popupTimer[0] = panelClock.millis();
                codeResponsePopup.setText("Invalid Code!");
                invalidCodeAttempt = 0;
            } else if (invalidCodeAttempt == 2) {
                popupTimer[0] = panelClock.millis();
                codeResponsePopup.setText("Game not found!");
                invalidCodeAttempt = 0;
            }
            
        }
        
    }

    // ActionListener interface method
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Difficulty buttons
        for (int i = 0; i < 4; i++) {
            if (e.getSource() == difficultyButtons[i]) {
                difficulty = i;
                difficultyButtons[i].setIcon(new ImageIcon(getClass()
                        .getResource("/img/difficulty_icon_" + i + "_1.png")));
                for (int j = 0; j < 4; j++) {
                    if (j != i) {
                        difficultyButtons[j].setIcon(new ImageIcon(getClass()
                                .getResource("/img/difficulty_icon_" + j + "_0.png")));
                    }
                }
            }
        }
        
        // Map buttons
        if (e.getSource() == randomMapButton) {
            map = 0;
            randomMapButton.setIcon(new ImageIcon(getClass()
                    .getResource("/img/random_button_icon_1.png")));
            for (int i = 0; i < 4; i++) {
                chooseMapButtons[i].setIcon(new ImageIcon(getClass()
                        .getResource("/img/map_selection_icon_" + i + "_0.png")));
            }
        }
        for (int i = 0; i < 4; i++) {
            if (e.getSource() == chooseMapButtons[i]) {
                map = i+1;
                chooseMapButtons[i].setIcon(new ImageIcon(getClass()
                        .getResource("/img/map_selection_icon_" + i + "_1.png")));
                randomMapButton.setIcon(new ImageIcon(getClass()
                        .getResource("/img/random_button_icon_0.png")));
                for (int j = 0; j < 4; j++) {
                    if (j != i) {
                        chooseMapButtons[j].setIcon(new ImageIcon(getClass()
                                .getResource("/img/map_selection_icon_" + j + "_0.png")));
                    }
                }
            }
        }
        
        // Code information buttons
        if (e.getSource() == codeInfoButton) {
            codeInfoPanel.setVisible(true);
            setComponents(false);
            codeInfoPanel.repaint();
        }
        
        // Removing code info
        if (e.getSource() == removeCodeInfo) {
            codeInfoPanel.setVisible(false);
            setComponents(true);
            codeInfoPanel.repaint();
        }
        
        // General information buttons
        if (e.getSource() == generalInfoButton) {
            generalInfoPanel.setVisible(true);
            setComponents(false);
            codeInfoPanel.repaint();
        }
        
        // Remove general info
        if (e.getSource() == removeGeneralInfo) {
            generalInfoPanel.setVisible(false);
            setComponents(true);
            generalInfoPanel.repaint();
        }
        
    }

    // KeyListener interface methods
    @Override
    public void keyTyped(KeyEvent e) {
        
        // Error trapping the code field to allow up to 6 numbers only
        if (e.getSource() == codeField) {
            char c = e.getKeyChar();
            if (!(c >= 48 && c <= 57) ||
                    codeField.getText().length() > 5) {
                e.consume();
            }
        }
        
        
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    
}
