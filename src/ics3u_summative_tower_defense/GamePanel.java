/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ics3u_summative_tower_defense;

/** Game Panel class
 *
 * @author Logesh Mahendran
 * @credits Daniel Barbalace for TableLayout
 */

// Imports
import tower.*;
import balloon.*;
import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Random;
import info.clearthought.layout.TableLayout;

public class GamePanel extends JPanel implements Runnable,ActionListener,
        KeyListener,MouseListener {
    
    // Instance variables
    
    // Objects
    private Thread gameThread;
    private Random random;
    private Clock gameClock;
    
    // My objects
    private Path path;
    private Clip backgroundClip;
    private AudioInputStream audioStream;
    private FloatControl volumeControl;
    
    // Swing objects
    JButton waveButton;
    JLabel waveLabel,cashIcon,userCashLabel,healthIcon,healthLabel,gameCodeLabel,
            towersTitle;
    JPanel towerSelectPanel,sandboxModePanel;
    JButton[] towerSelectButtons;
    
    // Sandbox-specific
    JLabel waveFieldTitle,balloonTitle,otherTitle;
    JTextField waveField;
    JButton openSandboxMenuButton,closeSandboxMenuButton,waveInfoText;
    JButton[] balloonButtons, otherButtons;
    
    // Variables
    private int gameCode,difficulty,userHealth,userCash,towerRequested,changedMini;
    private boolean placingTower, waveClicked, sandboxMode, balloonsOnPath,loseStatus,
            paused,pauseClicked;
    private long[] gameTimer;
    private String[] saveInfo;
    
    
    // Final variables
    private final int FPS = 360;
    
    // Constructor
    public GamePanel(int diff, int map,String[] prevSaveInfo) {
        
        // Game Panel configuration
        this.setPreferredSize(new Dimension(900, 600));
        this.setBackground(Color.black);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.setFocusable(true);
        this.setDoubleBuffered(true); // Sets offscreen buffering to be true (draws components off screen)
        
        // Background music
        try {
            audioStream = AudioSystem.getAudioInputStream(getClass().getResource("/sound/treasures_within_the_abomination.wav"));
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);
            volumeControl = (FloatControl)backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-15f);
            backgroundClip.start();
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {}
        
        // TableLayout setup (these are the individual invisible bounds on the whole screen, to be used by tableLayout)
        double[][] gameplayConstraints = {
            {5,15,5,100,525,40,60,40,10,100},
            {3,15,3,15,3,15,6,480,10,40,10}
        };
        this.setLayout(new TableLayout(gameplayConstraints));

        // Objects
        random = new Random();
        gameClock = Clock.systemDefaultZone();
        Tower.towers = null;
        Tower.towers = new Tower[100];
        Balloon.balloons = null;
        Balloon.balloons = new ArrayList<>();
        path = null;
        Obstacle.obstacles = null;
        WaveManager.setWavePathLocation(getClass().getResource("/txt/waves.txt"));
        
        // Primitive variable configurations
        towerRequested = -1;
        changedMini = -1;
        placingTower = false;
        waveClicked = false;
        loseStatus = false;
        paused = false;
        pauseClicked = false;
        Tower.setSelectedTower(-1);
        Tower.setNumTowers(0);
        Obstacle.setNumObstacles(0);
        gameTimer = new long[2];
        gameTimer[0] = gameClock.millis();
        
        // Game setup
        
        // If new game is made
        if (prevSaveInfo == null) {
            // Path
            if (map == 0) {
                path = new Path(true,0,null);
            } else {
                path = new Path(false,map,null);
            }

            // Difficulty setup
            difficulty = diff;
            WaveManager.setWave(0);
            if (difficulty == 2) {
                userHealth = 50;
                userCash = 200;
                WaveManager.setWave(2);
            } else if (difficulty == 1) {
                userHealth = 100;
                userCash = 300;
            } else if (difficulty == 0) {
                userHealth = 200;
                userCash = 400;
            } else {
                sandboxMode = true;
            }
            WaveManager.setWaveRunning(false);

            // Generate random code
            gameCode = random.nextInt(100000,999999);
            boolean codeExists = true;
            while (codeExists) {
                codeExists = false;
                if (ExtraMethods.getSaveFile(String.valueOf(gameCode)).exists()) {
                    codeExists = true;
                    gameCode = random.nextInt(100000,999999);
                }
            }
        // Loading from current save
        } else {
            // First set (code, wave, and difficulty)
            String[] infoFirstSet = prevSaveInfo[0].split(",");
            gameCode = Integer.parseInt(infoFirstSet[0]);
            WaveManager.setWave(Integer.parseInt(infoFirstSet[1]));
            difficulty = Integer.parseInt(infoFirstSet[2]);
            if (difficulty == 3) {
                sandboxMode = true;
            }
            userCash = Integer.parseInt(infoFirstSet[3]);
            userHealth = Integer.parseInt(infoFirstSet[4]);
            
            // Second set (path, obstacles)
            path = new Path(false,-1,prevSaveInfo[1]);
            
            // Third set (towers)
            if (!prevSaveInfo[2].equals("")) {
                String[] infoThirdSet = prevSaveInfo[2].split(",");
                for (int i = 0; i < infoThirdSet.length; i++) {
                    switch (Integer.parseInt(infoThirdSet[i].split(":")[0])) {
                        case 0 -> Tower.towers[Tower.getNumTowers()] = new TowerShooter(new Point(-1,-1));
                        case 1 -> Tower.towers[Tower.getNumTowers()] = new TowerCaster(new Point(-1,-1));
                        case 2 -> Tower.towers[Tower.getNumTowers()] = new TowerBooster(new Point(-1,-1));
                        case 3 -> Tower.towers[Tower.getNumTowers()] = new TowerDeflator(new Point(-1,-1));
                        case 4 -> Tower.towers[Tower.getNumTowers()] = new TowerGenerator(new Point(-1,-1));
                        case 5 -> Tower.towers[Tower.getNumTowers()] = new TowerFlyer(new Point(-1,-1));
                        default -> Tower.towers[Tower.getNumTowers()] = new TowerMinigunner(new Point(-1,-1));
                    }
                    Tower.towers[Tower.getNumTowers()-1].interpretCSV(infoThirdSet[i], userCash);
                    this.add(Tower.towers[Tower.getNumTowers()-1].getTowerStats(),"5,7,7,7");
                }
            }
            
        }
        
        // Sandbox mode only
        if (sandboxMode) {
            
            // Open button
            openSandboxMenuButton = new JButton();
            openSandboxMenuButton.setIcon(new ImageIcon(getClass().getResource("/img/open_sandbox_icon.png")));
            openSandboxMenuButton.addActionListener(this);
            openSandboxMenuButton.setVisible(false);
            this.add(openSandboxMenuButton,"6,9");
            
            double[][] sandboxModeConstraints = {
                {5,40,10,40,5},
                {5,10,15,10,10,5,40,10,40,10,40,10,40,10,40,10,10,5,40,5,40,5,40,5,20,5}
            };
            // 1 - Wave field title
            // 2 - Wave Field
            // 4 - Balloon select title
            // 6,8,10,12,14 - Balloon button rows
            // 16 - Other options title
            // 18,20,22 - Other options - replenish cash, health, clear balloons
            // 24 - close button
            sandboxModePanel = new JPanel(new TableLayout(sandboxModeConstraints));
            sandboxModePanel.setBackground(new Color(150,150,255));
            
            // Round field
            waveFieldTitle = new JLabel("Enter wave:");
            sandboxModePanel.add(waveFieldTitle,"1,1,3,1");
            waveField = new JTextField();
            waveField.addKeyListener(this);
            sandboxModePanel.add(waveField,"1,2,3,2");
            
            // Balloon buttons
            balloonTitle = new JLabel("Balloons:");
            sandboxModePanel.add(balloonTitle,"1,4,3,4");
            balloonButtons = new JButton[10];
            for (int i = 0; i < 10; i++) {
                balloonButtons[i] = new JButton();
                balloonButtons[i].setIcon(new ImageIcon(getClass().getResource("/img/sandbox_balloon_icon_" + i + ".png")));
                balloonButtons[i].setFocusPainted(false);
                balloonButtons[i].addActionListener(this);
                sandboxModePanel.add(balloonButtons[i],
                        (2*(i%2)+1) + "," + ((i/2)*2 + 6));
            }
            
            // Other buttons
            otherTitle = new JLabel("Options:");
            sandboxModePanel.add(otherTitle,"1,16,3,16");
            otherButtons = new JButton[3];
            for (int i = 0; i < 3; i++) {
                otherButtons[i] = new JButton();
                ImageIcon otherIcon;
                switch (i) {
                    case 0 -> otherIcon = new ImageIcon(getClass().getResource("/img/replenish_cash_icon.png"));
                    case 1 -> otherIcon = new ImageIcon(getClass().getResource("/img/replenish_health_icon.png"));
                    default -> otherIcon = new ImageIcon(getClass().getResource("/img/remove_balloons_icon.png"));
                }
                otherButtons[i].setIcon(otherIcon);
                otherButtons[i].setFocusPainted(false);
                otherButtons[i].addActionListener(this);
                sandboxModePanel.add(otherButtons[i],
                        "1," + (2*i + 18) + ",3," + (2*i + 18));
            }
            
            // Close button
            closeSandboxMenuButton = new JButton();
            closeSandboxMenuButton.setIcon(new ImageIcon(getClass().getResource("/img/close_sandbox_icon.png")));
            closeSandboxMenuButton.setFocusPainted(false);
            closeSandboxMenuButton.addActionListener(this);
            sandboxModePanel.add(closeSandboxMenuButton,"1,24,3,24");
            
            this.add(sandboxModePanel,"6,7,7,7");
            
            // Sandbox mode settings
            userCash = 999999999;
            userHealth = 999999999;
            
            
        }
        
        // SWING OBJECTS
        
        // Tower select Panel
        towerSelectPanel = new JPanel();
        double[][] towerSelectConstraints = {
            {0.1,0.8,0.1},
            {0.0025,0.04,0.015,0.12,0.015,0.12,0.015,0.12,0.015,0.12,0.015,0.12,0.015,0.12,0.015,0.12,0.0225}
        };
        towerSelectPanel.setPreferredSize(new Dimension(100,600));
        towerSelectPanel.setBackground(Color.WHITE);
        towerSelectPanel.setLayout(new TableLayout(towerSelectConstraints));
        // Side panel - Towers title
        towersTitle = new JLabel("<html>TOWERS (0/100)</html>");
        towersTitle.setHorizontalAlignment(JLabel.CENTER);
        towersTitle.setFont(new Font("Nimbus",Font.BOLD,11));
        towerSelectPanel.add(towersTitle,"1,1,1,2");
        // Side panel - Tower buttons
        towerSelectButtons = new JButton[7];
        for (int i = 0; i < 7; i++) {
            towerSelectButtons[i] = new JButton();
            towerSelectButtons[i].addActionListener(this);
            towerSelectButtons[i].addMouseListener(this);
            towerSelectButtons[i].setFocusPainted(false);
            towerSelectButtons[i].setIcon(new ImageIcon(getClass().getResource("/img/tower_selection_" + i + ".png")));
            towerSelectPanel.add(towerSelectButtons[i],"1," + ((i+1)*2 + 1));
        }
        this.add(towerSelectPanel,"9,0,9,10");
        
        // Wave label
        waveLabel = new JLabel();
        if (!sandboxMode) {
            waveLabel.setText("Next wave: " + (WaveManager.getWave()+1));
        } else {
            waveLabel.setText("Next wave: N/A");
        }
        waveLabel.setFont(new Font("Helvetica",Font.BOLD,15));
        waveLabel.setForeground(Color.BLUE);
        this.add(waveLabel,"1,1,4,1");
        
        // Wave information text
        if (!sandboxMode) {
            waveInfoText = new JButton("<html>" + 
                    ExtraMethods.readLine(getClass().getResource("/txt/wave_messages.txt"),WaveManager.getWave()+1) +
                    "</html>");
            waveInfoText.setMargin(new Insets(5,5,5,5));
            waveInfoText.setBorderPainted(false);
            waveInfoText.setFocusPainted(false);
            waveInfoText.setBackground(new Color(255,255,255,100));
            waveInfoText.setForeground(Color.WHITE);
            waveInfoText.setFont(new Font("Helvetica",Font.PLAIN,10));
            waveInfoText.addActionListener(this);
            this.add(waveInfoText,"4,1,4,6");
        }
        
        // User cash label and icon
        cashIcon = new JLabel(new ImageIcon(getClass().getResource("/img/cash_icon.png")));
        userCashLabel = new JLabel(String.valueOf(userCash));
        userCashLabel.setFont(new Font("Helvetica",Font.BOLD,15));
        userCashLabel.setForeground(Color.GREEN);
        this.add(cashIcon,"1,3");
        this.add(userCashLabel,"3,3");
        
        // Health label and icon
        healthIcon = new JLabel(new ImageIcon(getClass().getResource("/img/health_icon.png")));
        healthLabel = new JLabel(String.valueOf(userHealth));
        healthLabel.setFont(new Font("Helvetica",Font.BOLD,15));
        healthLabel.setForeground(Color.RED);
        this.add(healthIcon,"1,5");
        this.add(healthLabel,"3,5");
        
        // Wave button
        waveButton = new JButton();
        waveButton.setIcon(new ImageIcon(getClass().getResource("/img/wave_next_icon.png")));
        waveButton.addActionListener(this);
        this.add(waveButton,"7,9");
        
        // Game code label
        if (!sandboxMode) {
            gameCodeLabel = new JLabel("Game code: " + gameCode);
        } else {
            gameCodeLabel = new JLabel("Game code: N/A");
        }
        gameCodeLabel.setFont(new Font("Helvetica",Font.BOLD,15));
        gameCodeLabel.setForeground(Color.CYAN);
        gameCodeLabel.setHorizontalAlignment(JLabel.RIGHT);
        this.add(gameCodeLabel,"5,1,8,1");
        
        // Starting up save information for this game
        saveInfo = new String[3];
        // Order1: Code, Wave, difficulty, 
        // Order2: Path/Obstacles (same as stored in paths file)
        // Order3: Towers
        
        // Towers info: Type, damage, upgrade, x, y, 
        
        // Thread
        gameThread = new Thread(this);
        gameThread.start();
        
    }
    
    
    // Getters
    public boolean getLoseStatus() {
        return loseStatus;
    }
    
    public boolean getPaused() {
        return paused;
    }
    
    public boolean getPauseClicked() {
        return pauseClicked;
    }
    
    public int getDifficulty() {
        return difficulty;
    }
    
    public int getGameCode() {
        return gameCode;
    }
    
    public float getVolume() {
        return volumeControl.getValue();
    }
    
    // Setters
    public void setPaused(boolean p) {
        paused = p;
    }
    
    public void setPauseClicked(boolean p) {
        pauseClicked = p;
    }
    
    
    // Run method
    @Override
    public void run() {

        // An exit condition
        while (gameThread != null) {
            
            // Using nano time for more precision
            double drawInterval = 1000000000/FPS;
            // Sets the next draw time right away
            double nextDrawTime = System.nanoTime() + drawInterval;
            
            // UPDATES
            update();
            
            // DRAW (calls paintComponent)
            repaint();
            
            // After the above two method, some amount of time will have passed
            // The code below will sleep for the rest of that time in one draw interval
            // This will keep the game as close as possible to the desired FPS
            
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1000000;
                
                if (remainingTime < 0) {
                    remainingTime = 0;
                }
                
                Thread.sleep((long)remainingTime);
            } catch (InterruptedException ex) {}
            
        }
        
    } // END RUN METHOD
    
    // End loop
    public void endLoop() {
        gameThread = null;
    }
    
    // Returns if music is playing
    public boolean isPlayingMusic() {
        return backgroundClip.isRunning();
    }
    
    // Starting music
    public void startMusic(boolean isPlaying) {
        if (isPlaying) {
            backgroundClip.start();
        } else {
            backgroundClip.stop();
        }
    }
    
    // Update method
    public void update() {
        
        // Only run when not paused
        if (!paused) {
            // Update user cash
            userCash += Balloon.getCashPool();
            userCash += TowerGenerator.getCashPool();
            userCash -= TowerStats.getUserDebt();
            if (sandboxMode && userCash > 999999999) {
                userCash = 999999999;
            }
            Balloon.setCashPool(0);
            TowerGenerator.setCashPool(0);
            TowerStats.setUserDebt(0);
            String displayText = "";
            int tempCash = userCash;
            // Added commas every 3 digits
            for (int i = 0; i < String.valueOf(userCash).length(); i++) {
                displayText = (tempCash%10) + displayText;
                tempCash /= 10;
                if (i % 3 == 2) {
                    displayText = "," + displayText;
                }
            }
            if (displayText.charAt(0) == ',') {
                displayText = displayText.substring(1);
            }
            userCashLabel.setText(displayText);

            // Update health
            userHealth -= Balloon.getHealthPool();
            if (sandboxMode && userHealth < 0) {
                userHealth = 999999999;
            }
            Balloon.setHealthPool(0);
            displayText = "";
            int tempHealth = userHealth;
            if (tempHealth < 0) {
                tempHealth = Math.abs(tempHealth);
            }
            // Adding commas every 3 digits
            for (int i = 0; i < String.valueOf(Math.abs(userHealth)).length(); i++) {
                displayText = (tempHealth%10) + displayText;
                tempHealth /= 10;
                if (i % 3 == 2) {
                    displayText = "," + displayText;
                }
            }
            if (displayText.charAt(0) == ',') {
                displayText = displayText.substring(1);
            }
            if (userHealth < 0) {
                displayText = "-" + displayText;
            }
            healthLabel.setText(displayText);

            // Update towers title
            if (placingTower) {
                towersTitle.setText("<html>TOWERS (" + (Tower.getNumTowers()-1) + "/100)</html>");
            } else {
                towersTitle.setText("<html>TOWERS (" + Tower.getNumTowers() + "/100)</html>");
            }

            // WAVE UPDATES
            
            // Starting next wave from wave button
            if (waveClicked) {
                WaveManager.nextWave();
                WaveManager.startCurrentWave();
                if (!sandboxMode) {
                    waveInfoText.setVisible(false);
                }
                waveClicked = false;
            } else {
                // While current wave is running
                if (WaveManager.getWaveRunning() || balloonsOnPath) {
                    // Edit labels and icons accordingly
                    waveButton.setIcon(new ImageIcon(getClass().getResource("/img/wave_progress_icon.png")));
                    if (balloonsOnPath) {
                        waveLabel.setText("Current wave: N/A");
                    } else {
                        waveLabel.setText("Current wave: " + WaveManager.getWave());
                    }
                    if (!balloonsOnPath) {
                        // Check if wave is running constantly
                        WaveManager.playCurrentWave(path);
                        // If wave is over, reward cash and save game
                        if (!WaveManager.getSpawningBalloons() && Balloon.balloons.isEmpty() && userHealth > 0) {
                            WaveManager.setWaveRunning(false);
                            userCash += WaveManager.getWave()*50;

                            // Saving game to text file
                            if (!sandboxMode) {
                                saveInfo[0] = gameCode + "," + WaveManager.getWave() + "," + 
                                        difficulty + "," + userCash + "," + userHealth;
                                saveInfo[1] = path.toCSV();
                                saveInfo[2] = "";
                                for (int i = 0; i < Tower.getNumTowers(); i++) {
                                    if (!(placingTower && i == Tower.getSelectedTower())) {
                                        saveInfo[2] += Tower.towers[i].toCSV();
                                        if (i < Tower.getNumTowers() - 1) {
                                            saveInfo[2] += ",";
                                        }
                                    }
                                }

                                File gameFile = ExtraMethods.getSaveFile(String.valueOf(gameCode));
                                ExtraMethods.write(gameFile,saveInfo[0],false);
                                ExtraMethods.write(gameFile,saveInfo[1],true);
                                ExtraMethods.write(gameFile,saveInfo[2],true);
                                
                                // Redisplay next round's wave message
                                if (!ExtraMethods.readLine(getClass().getResource("/txt/wave_messages.txt"),WaveManager.getWave()+1).equals("")) {
                                    waveInfoText.setText("<html>" + 
                                            ExtraMethods.readLine(getClass().getResource("/txt/wave_messages.txt"),WaveManager.getWave()+1) +
                                            "</html>");
                                    waveInfoText.setVisible(true);
                                    waveInfoText.repaint();
                                }
                            }
                        }
                    } else {
                        if (sandboxMode && Balloon.balloons.isEmpty()) {
                            balloonsOnPath = false;
                        }
                    }
                } else {
                    waveButton.setIcon(new ImageIcon(getClass().getResource("/img/wave_next_icon.png")));
                    if (!sandboxMode) {
                        waveLabel.setText("Next wave: " + (WaveManager.getWave()+1));
                    } else {
                        waveLabel.setText("Next wave: N/A");
                    }

                }
            }

            // New tower
            if (towerRequested != -1) {
                if (this.getMousePosition() != null) {
                    if (towerRequested == 0) {
                        Tower.towers[Tower.getNumTowers()] = new TowerShooter(this.getMousePosition());
                    } else if (towerRequested == 1) {
                        Tower.towers[Tower.getNumTowers()] = new TowerCaster(this.getMousePosition());
                    } else if (towerRequested == 2) {
                        Tower.towers[Tower.getNumTowers()] = new TowerBooster(this.getMousePosition());
                    } else if (towerRequested == 3) {
                        Tower.towers[Tower.getNumTowers()] = new TowerDeflator(this.getMousePosition());
                    } else if (towerRequested == 4) {
                        Tower.towers[Tower.getNumTowers()] = new TowerGenerator(this.getMousePosition());
                    } else if (towerRequested == 5) {
                        Tower.towers[Tower.getNumTowers()] = new TowerFlyer(this.getMousePosition());
                    } else if (towerRequested == 6) {
                        Tower.towers[Tower.getNumTowers()] = new TowerMinigunner(this.getMousePosition());
                    }
                    Tower.setSelectedTower(Tower.getNumTowers()-1);
                    this.add(Tower.towers[Tower.getSelectedTower()].getTowerStats(),"5,7,7,7");
                    placingTower = true;
                }
                towerRequested = -1;
            }

            // TOWER UPDATES

            // Reset tower modifiers
            for (int i = 0; i < Tower.getNumTowers(); i++) {
                for (int j = 0; j < 4; j++) {
                    Tower.towers[i].setModifiers(j,1);
                }
                Tower.towers[i].setHiddenVision(false);
                if (Tower.towers[i] instanceof TowerGenerator) {
                    TowerGenerator tempGen = (TowerGenerator)Tower.towers[i];
                    tempGen.setCashModifier(1);
                }
            }

            // Reset balloon modifiers
            for (int i = 0; i < Balloon.balloons.size(); i++) {
                if (Balloon.balloons.get(i) != null) {
                    Balloon.balloons.get(i).setSModifier(1);
                    Balloon.balloons.get(i).setRegrowTime(3);
                }
            }

            // Update Booster towers first
            for (int i = 0; i < Tower.getNumTowers(); i++) {
                if (Tower.towers[i] instanceof TowerBooster) {
                    if (i != Tower.getSelectedTower() || !placingTower) {
                        Tower.towers[i].doAction();
                    }
                    Tower.towers[i].updateRadius();
                    Tower.towers[i].updateTowerStats(userCash);
                }
            }

            // Other towers
            for (int i = 0; i < Tower.getNumTowers(); i++) {
                if (!(Tower.towers[i] instanceof TowerBooster)) {
                    if (i != Tower.getSelectedTower() || !placingTower) {
                        Tower.towers[i].doAction();
                    }
                    if (!WaveManager.getWaveRunning() && !balloonsOnPath) {
                        Tower.towers[i].setTowerTimer(0,Tower.towers[i].getTowerTimer()[1]);
                    }
                    Tower.towers[i].updateRadius();
                    Tower.towers[i].updateTowerStats(userCash);
                }

            }

            // Update minigunners
            for (int i = 0; i < Tower.getNumTowers(); i++) {
                if (Tower.towers[i] instanceof TowerMinigunner) {
                    if (!placingTower) {
                        if (Tower.towers[i].getCurrentUpgrade() <= 1) {
                            try {
                                Tower.towers[i].setAngle(Math.toDegrees(Math.atan2(
                                        this.getMousePosition().y-Tower.towers[i].getPosition().y,
                                        this.getMousePosition().x-Tower.towers[i].getPosition().x)));
                            } catch (Exception e) {}

                        } else {
                            if (Tower.towers[i].getTowerStats().getChangeMiniTarget()) {
                                changedMini = i;
                            }

                            if (changedMini != -1) {
                                TowerMinigunner tempMini = (TowerMinigunner)Tower.towers[changedMini];
                                if (!tempMini.getTowerStats().getChangeMiniTarget()) {
                                    changedMini = -1;
                                }
                            }
                        }
                    }
                }
            }

            // Check if any towers are deleted
            for (int i = 0; i < 100; i++) {
                if (Tower.towers[i] != null) {
                    if (Tower.towers[i].getTowerStats().getSold()) {
                        userCash += Tower.towers[i].getTowerStats().getSellPrice();
                        Tower.towers[i].getTowerStats().setVisible(false);
                        Tower.towers[i] = null;
                        for (int j = i+1; j < 100; j++) {
                            Tower.towers[j-1] = Tower.towers[j];
                        }

                        Tower.setNumTowers(Tower.getNumTowers()-1);
                        Tower.setSelectedTower(-1);
                        Tower.towers[99] = null;
                    }
                }
            }

            // If tower is being placed, make it follow cursor
            if (this.getMousePosition() != null && Tower.getSelectedTower() != -1 && placingTower) {
                Tower.towers[Tower.getSelectedTower()].setPosition(this.getMousePosition());
            }

            // Balloon updates
            for (int i = 0; i < Balloon.balloons.size(); i++) {
                if (Balloon.balloons.get(i) != null) {
                    Balloon.balloons.get(i).moveAlongPath(path,i);
                    Balloon.balloons.get(i).regrowCheck(path,i);
                    Balloon.balloons.get(i).updateStage(path,i);
                }
            }


            // Checking if game is lost
            if (!sandboxMode && userHealth <= 0) {
                loseStatus = true;
            }
        // Freeze game timers if paused
        } else {
            // Towers
            for (int i = 0; i < Tower.getNumTowers(); i++) {
                Tower.towers[i].freezeTimers();
            }
            // Waves
            WaveManager.freezeTimers();
        }
        
    } // END UPDATE METHOD
    
    // Paint component method
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;
        
        // Add graphics to g here
        
        // Path graphics
        path.addGraphics(g2);
        
        // Minigunner ray
        for (int i = 0; i < Tower.getNumTowers(); i++) {
            if (Tower.towers[i] instanceof TowerMinigunner) {
                TowerMinigunner tempMini = (TowerMinigunner)Tower.towers[i];
                tempMini.drawRay(g2);
            }
        }
        
        // Balloon graphics
        for (int i = 0; i < Balloon.balloons.size(); i++) {
            if (Balloon.balloons.get(i) != null) {
                Balloon.balloons.get(i).addGraphics(g2);
            }
        }
        
        // Obstacle graphics
        for (int i = 0; i < Obstacle.getNumObstacles(); i++) {
            Obstacle.obstacles[i].addGraphics(g2);
        }
        
        // Radius graphics
        if (Tower.getSelectedTower() != -1) {
            Tower.towers[Tower.getSelectedTower()].drawRadius(g2,path);
        }
        
        // Tower graphics
        
        // Boosters first (so that their radius doesn't go over towers)
        for (int i = 0; i < Tower.getNumTowers(); i++) {
            if (Tower.towers[i] instanceof TowerBooster) {
                Tower.towers[i].addGraphics(g2);
            }
        }
        
        // Tower graphics for other towers
        for (int i = 0; i < Tower.getNumTowers(); i++) {
            for (int j = 0; j < Tower.towers[i].projectiles.size(); j++) {
                if (Tower.towers[i].projectiles.get(j) != null) {
                    Tower.towers[i].projectiles.get(j).addGraphics(g2);
                }
            }
            if (!(Tower.towers[i] instanceof TowerBooster)) {
                Tower.towers[i].addGraphics(g2);
            }
        }
        for (int i = 0; i < Tower.getNumTowers(); i++) {
            if (Tower.towers[i] instanceof TowerFlyer) {
                TowerFlyer tempPlane = (TowerFlyer)Tower.towers[i];
                tempPlane.drawPlane(g2);
            }
        }
        
        // Minigunner target graphics
        for (int i = 0; i < Tower.getNumTowers(); i++) {
            if (Tower.towers[i] instanceof TowerMinigunner) {
                if (i == Tower.getSelectedTower() && Tower.towers[i].getCurrentUpgrade() >= 2) {
                    TowerMinigunner tempMini = (TowerMinigunner)Tower.towers[i];
                    g.drawImage(ExtraMethods.loadImage(getClass().getResource("/img/minigunner_target.png")),
                            tempMini.getTargetPoint().x-20,
                            tempMini.getTargetPoint().y-20,null);
                }
            }
        }
        
        // Ability graphics
        for (int i = 0; i < Tower.getNumTowers(); i++) {
            if (Tower.towers[i].getCurrentUpgrade() == 4) {
                if (Tower.towers[i] instanceof TowerBooster) {
                    g.drawImage(ExtraMethods.loadImage(getClass().getResource("/img/ability_booster.png")),
                            10,555,null);
                    TowerBooster tempBoost = (TowerBooster)Tower.towers[i];
                    if (tempBoost.getAbilityStage() != 1) {
                        g.setColor(new Color(0,0,0,170));
                        g.fillRect(10,555,35,(int)(35-((tempBoost.getAbilityTimer()[1]-
                                tempBoost.getAbilityTimer()[0])/30000.0)*35));
                    }
                    
                }
                if (Tower.towers[i] instanceof TowerDeflator) {
                    g.drawImage(ExtraMethods.loadImage(getClass().getResource("/img/ability_deflator.png")),
                            55,555,null);
                    TowerDeflator tempDeflate = (TowerDeflator)Tower.towers[i];
                    if (tempDeflate.getAbilityStage() != 1) {
                        g.setColor(new Color(0,0,0,170));
                        g.fillRect(55,555,35,(int)(35-((tempDeflate.getAbilityTimer()[1]-
                                tempDeflate.getAbilityTimer()[0])/20000.0)*35));
                    }
                }
            }
        }
        
        // TowerStats graphics
        for (int i = 0; i < Tower.getNumTowers(); i++) {
            if (i != Tower.getSelectedTower() || placingTower) {
                Tower.towers[i].getTowerStats().setVisible(false);
            } else {
                Tower.towers[i].getTowerStats().setVisible(true);
            }
            Tower.towers[i].getTowerStats().repaint();
        }
        
        // Repaint wave text button
        if (!sandboxMode) {
            waveInfoText.repaint();
        }
        
    }
    

    // ActionListener Interface method
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Clicking to place a tower
        for (int i = 0; i < 7; i++) {
            if (e.getSource() == towerSelectButtons[i] && !placingTower && changedMini == -1 &&
                    userCash >= Tower.getCOSTS()[i] && Tower.getNumTowers() < 100) {
                towerRequested = i;
                if (sandboxMode) {
                    openSandboxMenuButton.setVisible(false);
                    waveField.setText("");
                    sandboxModePanel.setVisible(false);
                    sandboxModePanel.repaint();
                    openSandboxMenuButton.repaint();
                }
            }
        }
        
        // Wave button
        if (e.getSource() == waveButton) {
            if (!WaveManager.getWaveRunning() && !balloonsOnPath) {
                if (!sandboxMode) {
                    waveClicked = true;
                } else if (sandboxMode && !waveField.getText().equals("")) {
                    waveClicked = true;
                    WaveManager.setWave(Integer.parseInt(waveField.getText())-1);
                }
            // Pausing
            } else {
                paused = !paused;
                pauseClicked = true;
            }
        }
        
        if (sandboxMode) {
            // Opening sandbox menu
            if (e.getSource() == openSandboxMenuButton) {
                sandboxModePanel.setVisible(true);
                openSandboxMenuButton.setVisible(false);
                sandboxModePanel.repaint();
                openSandboxMenuButton.repaint();
            }

            // Closing sandbox menu
            if (e.getSource() == closeSandboxMenuButton) {
                sandboxModePanel.setVisible(false);
                waveField.setText("");
                openSandboxMenuButton.setVisible(true);
                sandboxModePanel.repaint();
                openSandboxMenuButton.repaint();
            }

            // Spawning a single balloon in sandbox mode
            for (int i = 0; i < 10; i++) {
                WaveManager.updateWaveBuffs(0);
                if (e.getSource() == balloonButtons[i] && !WaveManager.getWaveRunning()) {
                    balloonsOnPath = true;
                    int randomEffect = random.nextInt(0,5);
                    if (i < 5) {
                        Balloon.balloons.add(new BalloonColoured(i,randomEffect%4,0,path));
                    } else if (i == 5) {
                        Balloon.balloons.add(new BalloonRainbow(randomEffect%4,1,path));
                    } else if (i == 6) {
                        Balloon.balloons.add(new BalloonMetal(randomEffect%4,2,path));
                    } else if (i == 7) {
                        Balloon.balloons.add(new BalloonBoss(0,3,path));
                    } else if (i == 8) {
                        Balloon.balloons.add(new BalloonTank(0,4,path));
                    } else {
                        Balloon.balloons.add(new BalloonGalaxy(0,5,path));
                    }
                }
            }

            // Other buttons in sandbox mode
            for (int i = 0; i < 3; i++) {
                if (e.getSource() == otherButtons[i]) {
                    if (i == 0) {
                        userCash = 999999999;
                    } else if (i == 1) {
                        userHealth = 999999999;
                    } else {
                        for (int j = 0; j < Balloon.balloons.size(); j++) {
                            // Sets to -10000 so that balloon class recognizes it to not play a pop sound
                            Balloon.balloons.get(j).setHealth(-10000);
                        }
                        balloonsOnPath = false;
                        WaveManager.setWaveRunning(false);
                    }
                }
            }
            
        }
        
        // Wave text
        if (!sandboxMode) {
            if (e.getSource() == waveInfoText) {
                waveInfoText.setVisible(false);
                waveInfoText.repaint();
            }
        }
        
    }

    // KeyListener interface method
    @Override
    public void keyTyped(KeyEvent e) {
        
        // Error trapping sandbox mode wave field
        if (sandboxMode) {
            if (e.getSource() == waveField) {
                if ((e.getKeyChar() < 48 || e.getKeyChar() > 57)) {
                    e.consume();
                }
            }
        }
        
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    // MouseListener Interface methods
    @Override
    public void mouseClicked(MouseEvent e) {
        
        if (e.getSource() == this) {
            this.setFocusable(true);
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (changedMini == -1) {
                    // Tower placed
                    if (placingTower && !Tower.towers[Tower.getSelectedTower()].checkValidPlacement(path)) {
                        Tower.towers[Tower.getSelectedTower()].updateTowerStats(userCash);
                        userCash -= Tower.getCOSTS()[Tower.towers[Tower.getSelectedTower()].getTYPE()];
                        placingTower = false;
                    } else {
                        boolean towerClicked = false;
                        for (int i = 0; i < Tower.getNumTowers(); i++) {
                            // Check if abilities pressed
                            if (((e.getPoint().x >= 10 && e.getPoint().x <= 45) ||
                                    e.getPoint().x >= 55 && e.getPoint().x <= 90) &&
                                            (e.getPoint().y >= 555 && e.getPoint().y <= 590)) {
                                if (Tower.towers[i].getCurrentUpgrade() == 4) {
                                    if (Tower.towers[i] instanceof TowerBooster) {
                                        TowerBooster tempBoost = (TowerBooster)Tower.towers[i];
                                        if (tempBoost.getAbilityStage() == 1) {
                                            tempBoost.setAbilityStage(2);
                                        }
                                    }
                                    if (Tower.towers[i] instanceof TowerDeflator) {
                                        TowerDeflator tempDeflate = (TowerDeflator)Tower.towers[i];
                                        if (tempDeflate.getAbilityStage() == 1) {
                                            tempDeflate.setAbilityStage(2);
                                        }
                                    }
                                }
                            // Check if tower is selected
                            } else {
                                if (Math.sqrt(Math.pow(e.getPoint().x - 
                                    Tower.towers[i].getPosition().x,2) + 
                                    Math.pow(e.getPoint().y - 
                                    Tower.towers[i].getPosition().y,2)) <= 
                                    Tower.towers[i].getDimensions().width/2) {
                                    Tower.setSelectedTower(i);
                                    towerClicked = true;
                                    if (sandboxMode) {
                                        sandboxModePanel.setVisible(false);
                                        waveField.setText("");
                                        openSandboxMenuButton.setVisible(false);
                                        sandboxModePanel.repaint();
                                        openSandboxMenuButton.repaint();
                                    }
                                }
                                // If no tower is clicked, reset values
                                if (!towerClicked) {
                                    Tower.setSelectedTower(-1);
                                    if (sandboxMode && !sandboxModePanel.isVisible()) {
                                        openSandboxMenuButton.setVisible(true);
                                        openSandboxMenuButton.repaint();
                                    }
                                }
                            }
                        }
                    }
                // Minigunner target setting
                } else {
                    if (e.getPoint() != null) {
                        if (e.getPoint().x < 800 ) {
                            TowerMinigunner tempMini = (TowerMinigunner)Tower.towers[changedMini];
                            tempMini.setTargetPoint(e.getPoint());
                            Tower.towers[changedMini].getTowerStats().setChangeMiniTarget(false);
                            changedMini = -1;
                        }
                    }
                }
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                if (!placingTower) {
                    // Checking if a Generator tower is left clicked to collect cash
                    for (int i = 0; i < Tower.getNumTowers(); i++) {
                        if (ExtraMethods.distance(e.getPoint().x,e.getPoint().y,
                                Tower.towers[i].getPosition().x,
                                Tower.towers[i].getPosition().y) <= 20 &&
                                Tower.towers[i] instanceof TowerGenerator &&
                                Tower.towers[i].getCurrentUpgrade() < 4) {
                            TowerGenerator tempGen = (TowerGenerator)Tower.towers[i];
                            userCash += tempGen.getCashStored();
                            tempGen.changeDamageDealt(tempGen.getCashStored());
                            tempGen.setCashStored(0);
                        }
                    }
                } else {
                    Tower.towers[Tower.getSelectedTower()] = null;
                    Tower.setNumTowers(Tower.getNumTowers()-1);
                    Tower.setSelectedTower(-1);
                    placingTower = false;
                }
            }
        }
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
        // Clicking to place a tower
        for (int i = 0; i < 7; i++) {
            if (e.getSource() == towerSelectButtons[i] && !placingTower && changedMini == -1 &&
                    userCash >= Tower.getCOSTS()[i]) {
                towerRequested = i;
                if (sandboxMode) {
                    openSandboxMenuButton.setVisible(false);
                    waveField.setText("");
                    sandboxModePanel.setVisible(false);
                    sandboxModePanel.repaint();
                    openSandboxMenuButton.repaint();
                }
            }
        }
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
    
    
}
