/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package balloon;

/** Balloon class
 * 
 *
 * @author Logesh Mahendran
 */

// Imports
import ics3u_summative_tower_defense.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Random;

public class Balloon {
    
    // Instance variables
    private Point screenPos;
    private double speed, health, pathProgressAmount, sModifier, regrowTime;
    private double[] position;
    private int pathProgress, direction, effect, stage, balloonID;
    private Clock balloonClock;
    private long[] balloonTimer;
    private Random random;
    
    // Static variables
    private static int numBalloons;
    private static int cashPool = 0, healthPool = 0;
    public static ArrayList<Balloon> balloons;
    public static ArrayList<Balloon> balloonsToAdd;
    public static boolean popSoundsEnabled = true;
    
    // Final variables
    private final int REWARD, ORIGINAL_STAGE;
    private final int PATH_PROGRESS_END;
    private final Dimension DIMENSIONS;
    
    // Constructor
    public Balloon(double s, double h, int e, int st, int oSt, int r, Dimension d, Path p) {
        speed = s; effect = e; stage = st;
        health = h;
        pathProgress = 0; direction = 1; pathProgressAmount = 0;
        regrowTime = 3;
        position = new double[2];
        position[0] = p.getKEYPOINTS()[0][0];
        position[1] = p.getKEYPOINTS()[0][1];
        sModifier = 1;
        random = new Random();
        REWARD = r; DIMENSIONS = d; ORIGINAL_STAGE = oSt;
        PATH_PROGRESS_END = p.getNUMSECTIONS();
        screenPos = new Point((int)position[0] - DIMENSIONS.width, (int)position[1] - DIMENSIONS.height);
        balloonClock = Clock.systemDefaultZone();
        balloonTimer = new long[2];
        balloonTimer[0] = balloonClock.millis();
        numBalloons++;
        balloonID = numBalloons;
    }
    
    // Getter methods
    public double[] getPosition() {
        return position;
    }
    
    public Point getScreenPos() {
        return screenPos;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public double getHealth() {
        return health;
    }
    
    public double getSModifier() {
        return sModifier;
    }
    
    public int getPathProgress() {
        return pathProgress;
    }
    
    public double getPathProgressAmount() {
        return pathProgressAmount;
    }
    
    public int getDirection() {
        return direction;
    }
    
    public int getEffect() {
        return effect;
    }
    
    public double getRegrowTime() {
        return regrowTime;
    }
    
    public int getStage() {
        return stage;
    }
    
    public int getREWARD() {
        return REWARD;
    }
    
    public Dimension getDIMENSIONS() {
        return DIMENSIONS;
    }
    
    public int getBalloonID() {
        return balloonID;
    }
    
    // Setter methods
    public void setPosition(double x, double y) {
        position[0] = x; position[1] = y;
        screenPos = new Point((int)x - DIMENSIONS.width/2, (int)y - DIMENSIONS.height/2);
    }
    
    public void setSpeed(double s) {
        speed = s;
    }
    
    public void setHealth(double h) {
        health = h;
    }
    
    public void setSModifier(double m) {
        sModifier = m;
    }
    
    public void setPathProgress(int p) {
        pathProgress = p;
    }
    
    public void setPathProgressAmount(double p) {
        pathProgressAmount = p;
    }
    
    public void setDirection(int d) {
        direction = d;
    }
    
    public void setEffect(int e) {
        effect = e;
    }
    
    public void setRegrowTime(double r) {
        regrowTime = r;
    }
    
    public void setStage(int s) {
        stage = s;
    }
    
    public void setBalloonID(int id) {
        balloonID = id;
    }
    
    // Ohter instance methods
    public void changeHealth(double c) {
        health += c;
    }
    
    public void changePathProgressAmount(double c) {
        pathProgressAmount += c;
    }
    
    // Update balloon stage method
    public void updateStage(Path p, int j) {
        int targetStage = -1;
        int reward = 0;
        // If balloon is dead or reaches end of path
        if (health <= 0 || pathProgress == PATH_PROGRESS_END) {
            if (stage == 1) {
                if (speed == 0.1) {
                    reward = 1;
                } else if (speed == 0.2) {
                    reward = 2;
                } else if (speed == 0.3) {
                    reward = 3;
                } else if (speed == 0.5) {
                    reward = 4;
                } else {
                    reward = 5;
                }
            } else {
                switch (stage) {
                    case 2 -> reward = 20;
                    case 3 -> reward = 40;
                    case 4 -> reward = 120;
                    case 5 -> reward = 480;
                    case 6 -> reward = 1500;
                }
            }
            targetStage = 0;
            // If balloon reaches end of path
            if (pathProgress == PATH_PROGRESS_END && health > 0) {
                reward = 0;
                healthPool += health;
            // Play sound if balloon popped (not killed by sandbox mode)
            } else {
                if (popSoundsEnabled && health != -10000) {
                    playPopEffect();
                }
            }
            balloons.set(j,null);
            balloons.remove(j);
        // If balloons health drops below that of the stage below, set the target stage to be the one to transform into
        } else if (health <= 5 && health > 0) {
            targetStage = 1;
        } else if (health <= 10 && health > 5) {
            targetStage = 2;
        } else if (health <= 30 && health > 10) {
            targetStage = 3;
        // Health buff is applied onto boss+ balloons
        } else if (health <= 150*WaveManager.getWaveHealthBuff() && health > 30) {
            targetStage = 4;
        } else if (health <= 600*WaveManager.getWaveHealthBuff() && health > 150*WaveManager.getWaveHealthBuff()) {
            targetStage = 5;
        }
        // Set new speed values for coloured balloons (as speed is a way to differentiate them)
        if (targetStage == 1 && stage == 1) {
            if (health <= 1) {
                speed = 0.1;
            } else if (health > 1 && health <= 2) {
                speed = 0.2;
            } else if (health > 2 && health <= 3) {
                speed = 0.3;
            } else if (health > 3 && health <= 4) {
                speed = 0.5;
            } else {
                speed = 1;
            }
        }
        // Determine the amount of resulting balloons depending on how many stages have been passed through
        else if (stage > 1 && targetStage < stage && targetStage != -1 && targetStage != 0) {
            int newBalloons;
            if (stage == 2) {
                newBalloons = 4;
            } else if (stage == 3) {
                switch (targetStage) {
                    case 2 -> newBalloons = 2;
                    default -> newBalloons = 8;
                }
            } else if (stage == 4) {
                switch (targetStage) {
                    case 3 -> newBalloons = 3;
                    case 2 -> newBalloons = 6;
                    default -> newBalloons = 24;
                }
            } else if (stage == 5) {
                switch (targetStage) {
                    case 4 -> newBalloons = 4;
                    case 3 -> newBalloons = 12;
                    case 2 -> newBalloons = 24;
                    default -> newBalloons = 96;
                }
            } else {
                switch (targetStage) {
                    case 5 -> newBalloons = 4;
                    case 4 -> newBalloons = 16;
                    case 3 -> newBalloons = 48;
                    case 2 -> newBalloons = 96;
                    default -> newBalloons = 384;
                }
            }
            // Save old balloon information
            double[] oldPosition = balloons.get(j).position;
            double oldHealth = balloons.get(j).getHealth();
            int oldEffect = balloons.get(j).effect;
            int oldProgress = balloons.get(j).pathProgress;
            double oldPathProgressAmount = balloons.get(j).pathProgressAmount;
            int oldDirection = balloons.get(j).direction;
            int oldOriginalStage = balloons.get(j).ORIGINAL_STAGE;
            int oldID = balloons.get(j).balloonID;
            balloons.set(j,null);
            // Cast information to new balloon object
            for (int i = 0; i < newBalloons; i++) {
                Balloon addedBalloon;
                switch (targetStage) {
                    case 1 -> addedBalloon = new BalloonColoured(4,oldEffect,oldOriginalStage,p);
                    case 2 -> addedBalloon = new BalloonRainbow(oldEffect,oldOriginalStage,p);
                    case 3 -> addedBalloon = new BalloonMetal(oldEffect,oldOriginalStage,p);
                    case 4 -> addedBalloon = new BalloonBoss(oldEffect,oldOriginalStage,p);
                    default -> addedBalloon = new BalloonTank(oldEffect,oldOriginalStage,p);
                }
                addedBalloon.setPosition(oldPosition[0],oldPosition[1]);
                addedBalloon.setHealth(oldHealth);
                addedBalloon.setPathProgress(oldProgress);
                addedBalloon.setPathProgressAmount(oldPathProgressAmount);
                addedBalloon.position[0] += 2*(i+1)*oldDirection;
                addedBalloon.position[1] = p.evaluateFunction(addedBalloon.position[0], oldProgress);
                if (i == 0) {
                    addedBalloon.setBalloonID(oldID);
                    balloons.set(j, addedBalloon);
                } else {
                    balloons.add(addedBalloon);
                }
            }
        }
        cashPool += reward;
    }
    
    // Check for regrows
    public void regrowCheck(Path p, int j) {
        balloonTimer[1] = balloonClock.millis();
        // If timer is set off, then activate the regrow
        if (effect >= 2 && balloonTimer[1] - balloonTimer[0] >= regrowTime*1000
                && (stage < ORIGINAL_STAGE || ORIGINAL_STAGE == 1)) {
            balloonTimer[0] = balloonClock.millis();
            if (stage == 1 && speed != 1) {
                if (speed == 0.1) {
                    speed = 0.2; health = 2;
                } else if (speed == 0.2) {
                    speed = 0.3; health = 3;
                } else if (speed == 0.3) {
                    speed = 0.5; health = 4;
                } else if (speed == 0.5) {
                    speed = 1; health = 5;
                }
            // New balloon function similar to that of the updateStage method
            } else if (ORIGINAL_STAGE != 1) {
                double[] oldPosition = balloons.get(j).position;
                int oldEffect = balloons.get(j).effect;
                int oldProgress = balloons.get(j).pathProgress;
                double oldPathProgressAmount = balloons.get(j).pathProgressAmount;
                int oldDirection = balloons.get(j).direction;
                int oldOriginalStage = balloons.get(j).ORIGINAL_STAGE;
                int oldID = balloons.get(j).balloonID;
                Balloon newBalloon;
                switch (stage) {
                    case 1 -> newBalloon = new BalloonRainbow(oldEffect,oldOriginalStage,p);
                    case 2 -> newBalloon = new BalloonMetal(oldEffect,oldOriginalStage,p);
                    default -> newBalloon = new BalloonBoss(oldEffect,oldOriginalStage,p);
                }
                newBalloon.setPosition(oldPosition[0],oldPosition[1]);
                newBalloon.setPathProgress(oldProgress);
                newBalloon.setPathProgressAmount(oldPathProgressAmount);
                newBalloon.setDirection(oldDirection);
                newBalloon.setBalloonID(oldID);
                balloons.set(j,newBalloon);
            }
        }
    }
    
    // Move along path method (calculates next position using the functions from the path object)
    public void moveAlongPath(Path p, int j) {
        if (pathProgress < PATH_PROGRESS_END) {
            boolean foundPoint = false;
            double increment = 0.001*direction;
            while (!foundPoint) {
                double iy = p.evaluateFunction(position[0]+increment,pathProgress);
                if (Math.sqrt(Math.pow(increment, 2) + Math.pow(iy-position[1], 2)) >= 
                        speed*sModifier*WaveManager.getWaveSpeedBuff()) {
                    foundPoint = true;
                    setPosition(position[0] + increment, iy);
                    pathProgressAmount += Math.abs(increment);
                    pathProgress = p.updatePathProgress(position[0],pathProgress);
                    direction = p.updateDirection(pathProgress);
                }
                increment+=0.001*direction;
            }
        }
    }
    
    // Add graphics method (to be overriden by subclasses)
    public void addGraphics(Graphics2D g) {
        
    }
    
    // Static methods
    public static int getNumBalloons() {
        return numBalloons;
    }
    
    public static void setNumBalloons(int n) {
        numBalloons = n;
    }
    
    public static int getCashPool() {
        return cashPool;
    }
    
    public static int getHealthPool() {
        return healthPool;
    }
    
    public static boolean getPopSoundsEnabled() {
        return popSoundsEnabled;
    }
    
    public static void setCashPool(int c) {
        cashPool = c;
    }
    
    public static void setHealthPool(int h) {
        healthPool = h;
    }
    
    public static void setPopSoundsEnabled(boolean p) {
        popSoundsEnabled = p;
    }
    
    // Sound effect method (plays whenever a balloon is popped)
    public static void playPopEffect() {
        try {
            AudioInputStream popInputStream = AudioSystem.getAudioInputStream(Balloon.class.getResource("/sound/pop.wav"));
            Clip popEffect = AudioSystem.getClip();
            popEffect.open(popInputStream);
            popEffect.start();
        } catch (Exception f) {}
    }
    
}
