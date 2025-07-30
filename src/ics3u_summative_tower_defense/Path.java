/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ics3u_summative_tower_defense;

/** Path class
 *
 * @author Logesh Mahendran
 */

import java.awt.*;
import java.util.Random;

public class Path {
    
    // Instance/final variables
    private final boolean RANDOM;
    private final int SELECTION, NUMSECTIONS;
    private final int[] FUNCTIONS;
    private final double[][] KEYPOINTS;
    private final double[][] COEFFICIENTS;
    private Random random;
    
    // Constructor
    public Path(boolean r, int s, String info) {
        // FUNCTIONS: 1 for line, 2 for parabola, 3 for sine
        RANDOM = r; SELECTION = s;
        random = new Random();
        // For chosen path or loaded path from save
        if (!RANDOM) {
            // Get path information from file
            String pathInfoText = ExtraMethods.readLine(getClass().getResource("/txt/file_paths.txt"),SELECTION);
            System.out.println(pathInfoText);
            String[] pathInfoArr;
            // If there is previous info about a path from a save, use that instead
            if (info == null) {
                pathInfoArr = pathInfoText.split(",");
            } else {
                pathInfoArr = info.split(",");
            }
            
            // Set the number of sections
            NUMSECTIONS = Integer.parseInt(pathInfoArr[0]);
            String[] functionsArr = pathInfoArr[1].split(":");
            
            // Set the functions
            FUNCTIONS = new int[NUMSECTIONS];
            for (int i = 0; i < NUMSECTIONS; i++) {
                FUNCTIONS[i] = Integer.parseInt(functionsArr[i]);
            }
            
            // Set the coefficients
            String[] coeffsPerFunc = pathInfoArr[2].split(":");
            String[][] coeffsArr = new String[NUMSECTIONS][];
            COEFFICIENTS = new double[NUMSECTIONS][];
            for (int i = 0; i < NUMSECTIONS; i++) {
                coeffsArr[i] = coeffsPerFunc[i].split("/");
                COEFFICIENTS[i] = new double[coeffsArr[i].length];
                for (int j = 0; j < coeffsArr[i].length; j++) {
                    COEFFICIENTS[i][j] = Double.parseDouble(coeffsArr[i][j]);
                }
            }
            
            // Set the key points
            KEYPOINTS = new double[NUMSECTIONS+1][2];
            String[] pointsByFunc = pathInfoArr[3].split(":");
            String[][] pointsArr = new String[NUMSECTIONS+1][2];
            for (int i = 0; i < NUMSECTIONS+1; i++) {
                pointsArr[i] = pointsByFunc[i].split("/");
                KEYPOINTS[i][0] = Double.parseDouble(pointsArr[i][0]);
                KEYPOINTS[i][1] = Double.parseDouble(pointsArr[i][1]);
            }
            
            // Set the obstacles
            Obstacle.obstacles = new Obstacle[Integer.parseInt(pathInfoArr[4])];
            String[] boundStrings = pathInfoArr[5].split(":");
            String[][] obstacleBounds = new String[Obstacle.obstacles.length][5];
            for (int i = 0; i < Obstacle.obstacles.length; i++) {
                obstacleBounds[i] = boundStrings[i].split("/");
                int obsShape = Integer.parseInt(obstacleBounds[i][0]);
                int obsX = Integer.parseInt(obstacleBounds[i][1]);
                int obsY = Integer.parseInt(obstacleBounds[i][2]);
                int obsWidth = Integer.parseInt(obstacleBounds[i][3]);
                int obsHeight = Integer.parseInt(obstacleBounds[i][4]);
                if (obsShape == 1) {
                    obsHeight = obsWidth;
                }
                Obstacle.obstacles[i] = new Obstacle(obsX,obsY,obsWidth,obsHeight,obsShape);
            }
        
        // If a random path was chosen
        } else {
            // Determine numSections
            // For each section, determine the function
            // Then, determine the coefficients of each function by selecting key points (not all keypoints to determine function coefficients will be saved as keyPoints of the path)
            // Throughout, cast these informations to the arrays
            
            // Random number of sections
            NUMSECTIONS = random.nextInt(2,5);
            
            // Random functions
            FUNCTIONS = new int[NUMSECTIONS];
            for (int i = 0; i < NUMSECTIONS; i++) {
                FUNCTIONS[i] = random.nextInt(1,4);
            }
            
            // Random keypoints (for parabola, y values should be in line
            KEYPOINTS = new double[NUMSECTIONS+1][2];
            for (int i = 0; i < NUMSECTIONS+1; i++) {
                if (i == 0) {
                    KEYPOINTS[i][0] = 0;
                } else if (i == NUMSECTIONS) {
                    KEYPOINTS[i][0] = 800-800*(1-(NUMSECTIONS%2));
                } else {
                    if (i % 2 == 1) {
                        KEYPOINTS[i][0] = random.nextInt(650,751);
                    } else {
                        KEYPOINTS[i][0] = random.nextInt(50,151);
                    }
                }
                if (i != 0 && FUNCTIONS[i-1] == 2) {
                    KEYPOINTS[i][1] = KEYPOINTS[i-1][1];
                } else {
                    KEYPOINTS[i][1] = random.nextInt(25,576);
                }
            }
            
            // Random coefficients
            COEFFICIENTS = new double[NUMSECTIONS][];
            for (int i = 0; i < NUMSECTIONS; i++) {
                double[] coeffsTemp;
                if (FUNCTIONS[i] == 1) {
                    coeffsTemp = new double[2];
                    coeffsTemp[0] = (double)(KEYPOINTS[i+1][1]-KEYPOINTS[i][1])/
                            (double)(KEYPOINTS[i+1][0]-KEYPOINTS[i][0]);
                    coeffsTemp[1] = KEYPOINTS[i][1] - coeffsTemp[0]*KEYPOINTS[i][0];
                } else if (FUNCTIONS[i] == 2) {
                    coeffsTemp = new double[3];
                    double[] vertex = {
                        0.5*(KEYPOINTS[i][0] + KEYPOINTS[i+1][0]),
                        random.nextInt(25,576)
                    };
                    double stretch = (KEYPOINTS[i][1] - vertex[1])/(Math.pow(KEYPOINTS[i][0]-vertex[0],2));
                    coeffsTemp[0] = stretch;
                    coeffsTemp[1] = -2*vertex[0]*stretch;
                    coeffsTemp[2] = Math.pow(vertex[0],2)*stretch + vertex[1];
                } else {
                    coeffsTemp = new double[5];
                    coeffsTemp[3] = (double)(KEYPOINTS[i+1][1]-KEYPOINTS[i][1])/
                            (double)(KEYPOINTS[i+1][0]-KEYPOINTS[i][0]);
                    coeffsTemp[4] = KEYPOINTS[i][1] - coeffsTemp[3]*KEYPOINTS[i][0];
                    coeffsTemp[0] = random.nextInt(10,21);
                    if (i%2 == 0) {
                        coeffsTemp[2] = KEYPOINTS[i][0];
                    } else {
                        coeffsTemp[2] = KEYPOINTS[i+1][0];
                    }
                    coeffsTemp[1] = (random.nextInt(1,7))*(2*Math.PI)/Math.abs(KEYPOINTS[i][0]-KEYPOINTS[i+1][0]);
                }
                COEFFICIENTS[i] = coeffsTemp;
            }
            
            // Random obstacles
            Obstacle.obstacles = new Obstacle[5];
            for (int i = 0; i < 5; i++) {
                int randShape = random.nextInt(0,2);
                int randWidth = random.nextInt(20,100);
                int randHeight = random.nextInt(20,100);
                if (randShape == 1) {
                    randHeight = randWidth;
                }
                Obstacle.obstacles[i] = new Obstacle(random.nextInt(10,790-randWidth),
                random.nextInt(10,590-randHeight),randWidth,randHeight,randShape);
            }
            
        }
    }
    
    // Getters
    public int getNUMSECTIONS() {
        return NUMSECTIONS;
    }
    
    public double[][] getKEYPOINTS() {
        return KEYPOINTS;
    }
    
    // Other instance methods
    public double evaluateFunction(double x, int progress) {
        
        if (FUNCTIONS[progress] == 1) {
            return COEFFICIENTS[progress][0]*x + COEFFICIENTS[progress][1];
        } else if (FUNCTIONS[progress] == 2) {
            return COEFFICIENTS[progress][0]*x*x + COEFFICIENTS[progress][1]*x + 
                    COEFFICIENTS[progress][2];
        } else {
            return COEFFICIENTS[progress][0]*Math.sin(COEFFICIENTS[progress][1]*
                    (x - COEFFICIENTS[progress][2])) + COEFFICIENTS[progress][3]*x + 
                    COEFFICIENTS[progress][4];
        }
        
    }
    
    // Returns the path progress (to be used by a balloon) so that the balloon knows where to change directions
    public int updatePathProgress(double x, int progress) {
        boolean endOfSection;
        switch (progress%2) {
            case 0 -> endOfSection = (x > KEYPOINTS[progress+1][0]);
            default -> endOfSection = (x < KEYPOINTS[progress+1][0]);
        }
        if (endOfSection) {
            return progress+1;
        } else {
            return progress;
        }
    }
    
    // Used by the balloons to change their direction based on progress
    public int updateDirection(int progress) {
        int direction;
        switch (progress%2) {
            case 0 -> direction = 1;
            default -> direction = -1;
        }
        return direction;
    }
    
    // Adding graphics of the path by drawing a lot of circles along the piecewise function
    public void addGraphics(Graphics2D g) {
        g.setColor(new Color(180,180,180));
        double x = KEYPOINTS[0][0];
        double y = KEYPOINTS[0][1];
        double drawDirection = 1;
        int drawProgress = 0;
        while (drawProgress < NUMSECTIONS) {
            boolean foundPoint = false;
            double increment = 0.001*drawDirection;
            while (!foundPoint) {
                double iy = evaluateFunction(x+increment,drawProgress);
                if (Math.sqrt(Math.pow(increment, 2) + Math.pow(iy-y, 2)) >= 1) {
                    foundPoint = true;
                    x += increment;
                    y = iy;
                    g.fillOval((int)x-10,(int)y-10,20,20);
                    drawProgress = updatePathProgress(x,drawProgress);
                    drawDirection = updateDirection(drawProgress);
                }
                increment+=0.001*drawDirection;
            }
        }
    }
    
    // Converts all of the path information to a CSV format, to be used by a save file
    public String toCSV() {
        // Number of sections
        String csvText = NUMSECTIONS + ",";
        
        // Functions
        for (int i = 0; i < FUNCTIONS.length; i++) {
            csvText += FUNCTIONS[i];
            if (i < FUNCTIONS.length-1) {
                csvText += ":";
            }
        }
        
        csvText += ",";
        
        // Coefficients
        for (int i = 0; i < COEFFICIENTS.length; i++) {
            for (int j = 0; j < COEFFICIENTS[i].length; j++) {
                csvText += COEFFICIENTS[i][j];
                if (j < COEFFICIENTS[i].length-1) {
                    csvText += "/";
                }
            }
            if (i < COEFFICIENTS.length-1) {
                csvText += ":";
            }
        }
        
        csvText += ",";
        
        // Key Points
        for (int i = 0; i < KEYPOINTS.length; i++) {
            for (int j = 0; j < KEYPOINTS[i].length; j++) {
                csvText += KEYPOINTS[i][j];
                if (j < KEYPOINTS[i].length-1) {
                    csvText += "/";
                }
            }
            if (i < KEYPOINTS.length-1) {
                csvText += ":";
            }
        }
        
        csvText += ",";
        
        // Number of obstacles
        csvText += Obstacle.getNumObstacles() + ",";
        
        // Obstacles
        for (int i = 0; i < Obstacle.obstacles.length; i++) {
            csvText += Obstacle.obstacles[i].getSHAPE() + "/" +
                    Obstacle.obstacles[i].getBOUNDS().x + "/" +
                    Obstacle.obstacles[i].getBOUNDS().y + "/" +
                    Obstacle.obstacles[i].getBOUNDS().width + "/" +
                    Obstacle.obstacles[i].getBOUNDS().height;
            if (i < Obstacle.getNumObstacles()-1) {
                csvText += ":";
            }
        }
        
        return csvText;
        
    }
    
}
