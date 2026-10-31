/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ics3u_summative_tower_defense;

/** Main panel class
 *
 * @author Logesh Mahendran
 */

// Imports
import balloon.Balloon;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import info.clearthought.layout.TableLayout;

public class MainPanel extends JPanel implements ActionListener, Runnable {
    
    // Instance variables
    
    // Objects
    Thread mainThread;
    
    // Panels
    GamePanel gamePanel;
    TitlePanel titlePanel;
    NewGamePanel newGamePanel;
    TowerPanel towerPanel;
    LosePanel losePanel;
    PausePanel pausePanel;
    
    // Constructor
    public MainPanel() {
        
        // Main Panel configuration
        this.setPreferredSize(new Dimension(900, 600));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // Sets offscreen buffering to be true (draws components off screen)
        this.setLayout(null);
        
        // TitlePanel
        titlePanel = new TitlePanel(this);
        titlePanel.setBounds(0,0,900,600);
        this.add(titlePanel);
        
        // New game panel
        newGamePanel = new NewGamePanel(this);
        newGamePanel.setBounds(0,0,900,600);
        newGamePanel.setVisible(false);
        this.add(newGamePanel);
        
        // Towers panel
        towerPanel = new TowerPanel(this);
        towerPanel.setBounds(0,0,900,600);
        towerPanel.setVisible(false);
        this.add(towerPanel);
        
        // Thread
        mainThread = new Thread(this);
        mainThread.start();
        
    }
    
    // Run method
    @Override
    public void run() {
        
        while (mainThread != null) {
            
            // Check if the game is lost, then display the losing menu
            if (gamePanel != null && gamePanel.getLoseStatus()) {
                losePanel = new LosePanel(this,WaveManager.getWave()-1,gamePanel.getDifficulty(),
                    gamePanel.getGameCode());
                losePanel.setBounds(0,0,900,600);
                this.add(losePanel);
                losePanel.setVisible(true);
                gamePanel.setVisible(false);
                gamePanel.startMusic(false);
                losePanel.repaint();
                gamePanel.repaint();
                gamePanel.endLoop();
                gamePanel = null;
            }
            
            // Check if game is paused, then display the pause menu
            if (gamePanel != null && gamePanel.getPauseClicked()) {
                pausePanel = new PausePanel(this,gamePanel.getGameCode(),
                gamePanel.isPlayingMusic(),Balloon.getPopSoundsEnabled());
                pausePanel.setBounds(0,0,900,600);
                this.add(pausePanel);
                gamePanel.setPauseClicked(false);
                pausePanel.setVisible(true);
                gamePanel.setVisible(false);
                pausePanel.repaint();
                gamePanel.repaint();
            }
            
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                
            }
            
        }
        
    }

    // ActionListener Interface method
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Title panel actions
        if (titlePanel != null) {
            // Title Panel play button
            if (e.getSource() == titlePanel.playButton) {
                newGamePanel.setVisible(true);
                titlePanel.setVisible(false);
                newGamePanel.repaint();
                titlePanel.repaint();
            }

            // Title Panel towers button
            if (e.getSource() == titlePanel.towerButton) {
                towerPanel.setVisible(true);
                titlePanel.setVisible(false);
                towerPanel.repaint();
                titlePanel.repaint();
            }
        }
        
        // Tower panel actions
        if (towerPanel != null) {
            // Back to Title from Tower Panel
            if (e.getSource() == towerPanel.backToTitleButton) {
                titlePanel.setVisible(true);
                towerPanel.setVisible(false);
                titlePanel.repaint();
                towerPanel.repaint();
            }
        }
        
        // New game panel actions
        if (newGamePanel != null) {
            // Back to Title from New Game
            if (e.getSource() == newGamePanel.backToTitleButton) {
                titlePanel.setVisible(true);
                newGamePanel.setVisible(false);
                titlePanel.repaint();
                newGamePanel.repaint();
            }

            // Starting game as new game
            if (e.getSource() == newGamePanel.beginButton) {
                if (newGamePanel.getMap() != -1) {
                    gamePanel = new GamePanel(newGamePanel.getDifficulty(),
                            newGamePanel.getMap(),null);
                    gamePanel.setBounds(0,0,900,600);
                    gamePanel.setVisible(false);
                    this.add(gamePanel);
                    gamePanel.setVisible(true);
                    newGamePanel.setVisible(false);
                    titlePanel.stopMenuMusic();
                    gamePanel.repaint();
                    newGamePanel.repaint();
                    newGamePanel = null;
                    towerPanel = null;
                    titlePanel = null;
                }
            // Starting game from previous game
            } else if (e.getSource() == newGamePanel.codeBeginButton) {
                if (newGamePanel.codeField.getText().length() == 6) {
                    java.io.File saveFile = ExtraMethods.getSaveFile(newGamePanel.codeField.getText());
                    String validCheck = ExtraMethods.readLine(saveFile,1);
                    if (!validCheck.equals("")) {
                        String[] prevInfo = {
                            validCheck,
                            ExtraMethods.readLine(saveFile,2),
                            ExtraMethods.readLine(saveFile,3)
                        };
                        System.out.println(Arrays.toString(prevInfo));
                        gamePanel = new GamePanel(0,0,prevInfo);
                        gamePanel.setBounds(0,0,900,600);
                        newGamePanel.setVisible(false);
                        this.add(gamePanel);
                        titlePanel.stopMenuMusic();
                        gamePanel.repaint();
                        newGamePanel.repaint();
                        newGamePanel = null;
                        towerPanel = null;
                        titlePanel = null;
                    } else {
                        newGamePanel.setInvalidCodeAttempt(2);
                    }
                } else {
                    newGamePanel.setInvalidCodeAttempt(1);
                }
            }
        }
        
        // Lose panel actions
        if (losePanel != null) {
            // Back to title button from lose screen
            if (losePanel != null && e.getSource() == losePanel.returnToMenuButton) {
                // Remake all of the screens (which reset their values)
                // Title Panel
                titlePanel = new TitlePanel(this);
                titlePanel.setBounds(0,0,900,600);
                this.add(titlePanel);

                // New game panel
                newGamePanel = new NewGamePanel(this);
                newGamePanel.setBounds(0,0,900,600);
                newGamePanel.setVisible(false);
                this.add(newGamePanel);

                // Towers panel
                towerPanel = new TowerPanel(this);
                towerPanel.setBounds(0,0,900,600);
                towerPanel.setVisible(false);
                this.add(towerPanel);

                titlePanel.setVisible(true);
                losePanel.setVisible(false);
                titlePanel.repaint();
                losePanel.repaint();
                losePanel = null;
            }
        }
        
        // Pause panel actions
        if (pausePanel != null) {
            // Back to title button from lose screen
            if (e.getSource() == pausePanel.returnToMenuButton) {
                // Remake all of the screens (which reset their values)
                // Title Panel
                titlePanel = new TitlePanel(this);
                titlePanel.setBounds(0,0,900,600);
                this.add(titlePanel);

                // New game panel
                newGamePanel = new NewGamePanel(this);
                newGamePanel.setBounds(0,0,900,600);
                newGamePanel.setVisible(false);
                this.add(newGamePanel);

                // Towers panel
                towerPanel = new TowerPanel(this);
                towerPanel.setBounds(0,0,900,600);
                towerPanel.setVisible(false);
                this.add(towerPanel);
                
                // Remove game panel
                gamePanel.setVisible(false);
                gamePanel.endLoop();
                gamePanel.startMusic(false);
                gamePanel.repaint();
                gamePanel = null;

                titlePanel.setVisible(true);
                pausePanel.setVisible(false);
                titlePanel.repaint();
                pausePanel.repaint();
                pausePanel = null;
            
            
            // Resuming game
            } else if (e.getSource() == pausePanel.resumeButton) {
                gamePanel.setVisible(true);
                pausePanel.setVisible(false);
                gamePanel.setPaused(false);
                gamePanel.repaint();
                pausePanel.repaint();
                pausePanel = null;
            // Toggling music
            } else if (e.getSource() == pausePanel.musicToggleButton) {
                gamePanel.startMusic(!gamePanel.isPlayingMusic());
                pausePanel.toggleMusicIcon();
            } else if (e.getSource() == pausePanel.effectsToggleButton) {
                Balloon.setPopSoundsEnabled(!Balloon.getPopSoundsEnabled());
                pausePanel.toggleEffectsIcon();
            }
            
        }
        
    }

    
}
