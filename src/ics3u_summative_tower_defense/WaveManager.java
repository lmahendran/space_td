/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ics3u_summative_tower_defense;

/** Wave Manager class
 *
 * @author Logesh Mahendran
 */

// Imports
import balloon.*;
import java.net.URL;
import java.time.Clock;
import java.util.Random;

public class WaveManager {
    
    // Instance variables
    
    // Objects
    private static Random random = new Random();
    private static Clock waveClock;
    private static URL waveFileLocation;
    
    // Primitives
    private static int wave;
    private static long[] waveStartTimer;
    private static String[][] waveInfo;
    private static long[][] waveTimers;
    private static int[] balloonCounts;
    private static boolean waveRunning,spawningBalloons;
    private static double waveSpeedBuff,waveHealthBuff;
    
    // Final variables
    private final static int MAX_WAVE = 100;
    
    
    // Getters
    public static int getWave() {
        return wave;
    }
    
    public static boolean getWaveRunning() {
        return waveRunning;
    }
    
    public static boolean getSpawningBalloons() {
        return spawningBalloons;
    }
    
    public static double getWaveSpeedBuff() {
        return waveSpeedBuff;
    }
    
    public static double getWaveHealthBuff() {
        return waveHealthBuff;
    }
    
    public static int getMAX_WAVE() {
        return MAX_WAVE;
    }
    
    // Setters
    public static void setWave(int w) {
        wave = w;
    }
    
    public static void setWaveRunning(boolean w) {
        waveRunning = w;
    }
    
    public static void setWavePathLocation(URL w) {
        waveFileLocation = w;
    }
    
    // Next wave method
    public static void nextWave() {
        wave++;
        updateWaveBuffs(wave);
    }
    
    // Update wave buffs method (modifies boss balloon health and general balloon speed as waves go on)
    public static void updateWaveBuffs(int wave) {
        waveSpeedBuff = 1 + (0.01*wave);
        waveHealthBuff = 1 + (0.005*wave);
    }
    
    // Generates a random wave with a formula below
    public static String[] randomWave(int wave) {
        
        // First, determine a slightly random number of balloons
        int numBalloonsInWave = (int)(wave*1.2) + random.nextInt(-10,11);
        
        // Determine a random amount of "splits" of balloons (how many sets of balloons will there be?)
        int numSplits = (int)(wave/20.0)+random.nextInt(-3,6);
        
        // For each split, determine the number of balloons in the split. Must add up to number of total balloons
        int[] splitValues = new int[numSplits];
        int evenPortion = (int)((double)numBalloonsInWave/(double)numSplits);
        for (int i = 0; i < numSplits; i++) {
            splitValues[i] = evenPortion + random.nextInt((int)(-0.1*evenPortion),(int)(0.1*evenPortion));
        }
        
        // Dealing with having too many balloons planned for the wave
        boolean overflow = true;
        while (overflow) {
            overflow = false;
            int sumSplits = 0;
            for (int i = 0; i < numSplits; i++) {
                sumSplits += splitValues[i];
            }
            if (sumSplits > numBalloonsInWave) {
                overflow = true;
                int randomIndex = random.nextInt(0,numSplits);
                splitValues[randomIndex]--;
            }
        }
        
        // Picking a random balloon type based on the wave
        // Trying an idea for scaling randomness, calling it the "hit" system.
        // I essentially picture each balloons as if they were placed on a spectrum.
        // 1 is red, 2 is blue, 3 green, 4 yellow, 5-9 pink, 10-29 rainbow, 30-149 metal, 150-599 boss, 600-2999 tank, 3000+ galaxy
        // So, I will effectively place an "equilibrium line", which will hold some value on this spectrum.
        // It's pretty clear that this value will converge mainly to the boss+ balloons as the rounds increase, which is realistic and works for this context.
        // All that is required is a function for the position on the spectrum that would make sense, starting from round 100. Ideally starting with many takes and bosses from 100, and by 200 it should be mostly tanks and galaxies
        
        // Determining spectrum position
        int balloonSpectrumPosition = 24*(wave-107)+600;
        int amplitude = balloonSpectrumPosition;
        // I chose a larger amplitude so that around round 100, the smaller balloons can still be picked. As we drift away, these will eventually be gone.
        
        // Coming up with balloon types
        String[] balloonTypes = new String[numSplits];
        for (int i = 0; i < numSplits; i++) {
            int balloonNumber = balloonSpectrumPosition + random.nextInt(-1*amplitude,amplitude+1);
            if (balloonNumber == 1) {
                balloonTypes[i] = "red";
            } else if (balloonNumber == 2) {
                balloonTypes[i] = "blue";
            } else if (balloonNumber == 3) {
                balloonTypes[i] = "green";
            } else if (balloonNumber == 4) {
                balloonTypes[i] = "yellow";
            } else if (balloonNumber >= 5 && balloonNumber <= 9) {
                balloonTypes[i] = "pink";
            } else if (balloonNumber >= 10 && balloonNumber <= 29) {
                balloonTypes[i] = "rain";
            } else if (balloonNumber >= 30 && balloonNumber <= 149) {
                balloonTypes[i] = "metal";
            } else if (balloonNumber >= 150 && balloonNumber <= 599) {
                balloonTypes[i] = "boss";
            } else if (balloonNumber >= 600 && balloonNumber <= 2999) {
                balloonTypes[i] = "tank";
            } else if (balloonNumber >= 3000) {
                balloonTypes[i] = "gal";
            }
        }
        
        // Randomizing all the other stats
        String[] finalWaveInfo = new String[numSplits];
        for (int i = 0; i < numSplits; i++) {
            finalWaveInfo[i] = ExtraMethods.round(
                    random.nextDouble(0,15), 2) + ":" +
                    balloonTypes[i] + ":";
            int randomEffect = 0;
            if (!balloonTypes[i].equals("boss") && !balloonTypes[i].equals("tank") &&
                    !balloonTypes[i].equals("gal")) {
                randomEffect = random.nextInt(0,4);
            }
            finalWaveInfo[i] += randomEffect + ":" + splitValues[i] + ":" + 
                    ExtraMethods.round(random.nextDouble(0,1), 1);
        }
        
        return finalWaveInfo;
        
    }
    
    // Wave info - spawn time:balloon type:balloon effect:balloon count:delay between balloons (see waves.txt to see in practice)
    public static void startCurrentWave() {
        waveRunning = true;
        spawningBalloons = true;
        waveClock = Clock.systemDefaultZone();
        waveStartTimer = null;
        waveStartTimer = new long[2];
        waveStartTimer[0] = waveClock.millis();
        String[] waveSections;
        if (wave <= MAX_WAVE) {
            waveSections = ExtraMethods.readLine(waveFileLocation,wave).split(",");
        } else {
            waveSections = randomWave(wave);
        }
        waveInfo = null;
        waveInfo = new String[waveSections.length][];
        waveTimers = null;
        waveTimers = new long[waveSections.length][2];
        balloonCounts = null;
        balloonCounts = new int[waveSections.length];
        for (int i = 0; i < waveSections.length; i++) {
            waveInfo[i] = waveSections[i].split(":");
            waveTimers[i][0] = -1;
            waveTimers[i][1] = -1;
            balloonCounts[i] = 0;
        }
    }
    
    // Method to continue the current wave, based on the start times and balloon timers
    public static void playCurrentWave(Path p) {
        if (spawningBalloons) {
            
            for (int i = 0; i < waveTimers.length; i++) {
            
                // Detect a new spawn time, and set the timer for that set of balloons
                waveStartTimer[1] = waveClock.millis();
                if ((waveStartTimer[1] - waveStartTimer[0])/1000.0 >= Double.parseDouble(waveInfo[i][0]) 
                        && waveTimers[i][0] == -1) {
                    waveTimers[i][0] = waveClock.millis();
                    waveTimers[i][1] = waveClock.millis();
                }
                
                // If it is time to spawn a balloon according to instructions, then do so
                if (waveTimers[i][0] != -1 && waveTimers[i][0] != -2) {
                    waveTimers[i][1] = waveClock.millis();
                    if ((waveTimers[i][1]-waveTimers[i][0])/1000.0 >= Double.parseDouble(waveInfo[i][4])) {
                        balloonCounts[i]++;
                        int balloonEffect = Integer.parseInt(waveInfo[i][2]);
                        Balloon newBalloon = null;
                        switch (waveInfo[i][1]) {
                            case "red" -> newBalloon = new BalloonColoured(0,balloonEffect,1,p);
                            case "blue" -> newBalloon = new BalloonColoured(1,balloonEffect,1,p);
                            case "green" -> newBalloon = new BalloonColoured(2,balloonEffect,1,p);
                            case "yellow" -> newBalloon = new BalloonColoured(3,balloonEffect,1,p);
                            case "pink" -> newBalloon = new BalloonColoured(4,balloonEffect,1,p);
                            case "rain" -> newBalloon = new BalloonRainbow(balloonEffect,2,p);
                            case "metal" -> newBalloon = new BalloonMetal(balloonEffect,3,p);
                            case "boss" -> newBalloon = new BalloonBoss(balloonEffect,4,p);
                            case "tank" -> newBalloon = new BalloonTank(balloonEffect,4,p);
                            case "gal" -> newBalloon = new BalloonGalaxy(balloonEffect,4,p);
                        }
                        Balloon.balloons.add(newBalloon);
                        waveTimers[i][0] = waveTimers[i][1];
                    }
                    // If all balloons spawned for that segment, stop spawning balloons
                    if (balloonCounts[i] >= Integer.parseInt(waveInfo[i][3])) {
                        waveTimers[i][0] = -2;
                        waveTimers[i][1] = -2;
                        // -1 means before spawn frame, -2 means after
                    }
                }
                
            }
            
            spawningBalloons = false;
            for (int i = 0; i < waveTimers.length; i++) {
                if (waveTimers[i][0] != -2) {
                    spawningBalloons = true;
                }
            }
            
        }
    }
    
    public static void freezeTimers() {
        // Timer for when to start spawning balloons
        long delay = waveStartTimer[1] - waveStartTimer[0];
        waveStartTimer[1] = waveClock.millis();
        waveStartTimer[0] = waveStartTimer[1] - delay;
        
        // Balloon spawning timers
        for (int i = 0; i < waveTimers.length; i++) {
            if (waveTimers[i][0] != -1 && waveTimers[i][0] != -2) {
                long remainingTime = waveTimers[i][1] - waveTimers[i][0];
                waveTimers[i][1] = waveClock.millis();
                waveTimers[i][0] = waveTimers[i][1] - remainingTime;
            }
        }
    }
    
}
