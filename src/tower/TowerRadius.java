/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tower;

/** Tower radius class
 * 
 *
 * @author Logesh Mahendran
 */

// Imports
import ics3u_summative_tower_defense.*;
import java.awt.*;

public class TowerRadius {
    
    // Instance variables
    private double radius;
    private Point position;
    private Point[] keyPoints;
    private boolean showReds;
    
    // Static variables
    private static int numRadii;
    public TowerRadius[] radii;
    
    // Constructor
    public TowerRadius(double r, Point p, boolean s) {
        radius = r;
        position = p;
        keyPoints = new Point[72];
        showReds = s;
        for (int i = 0; i < 72; i++) {
            keyPoints[i] = new Point(-1,-1);
        }
    }
    
    // Getters
    public double getRadius() {
        return radius;
    }
    
    // Setters
    public void setRadius(double r) {
        radius = r;
    }
    
    public void setPosition(Point p) {
        position = p;
    }
    
    // Check obstacles
    public static boolean checkObstacles(double x, double y) {
        for (int i = 0; i < Obstacle.getNumObstacles(); i++) {
            // Rectangle case
            if (Obstacle.obstacles[i].getSHAPE() == 0 && 
                    x >= Obstacle.obstacles[i].getBOUNDS().x 
                    && x <= Obstacle.obstacles[i].getBOUNDS().x + 
                    Obstacle.obstacles[i].getBOUNDS().width 
                    && y >= Obstacle.obstacles[i].getBOUNDS().y 
                    && y <= Obstacle.obstacles[i].getBOUNDS().y + 
                    Obstacle.obstacles[i].getBOUNDS().height) { 
                return true;
            } else if (Obstacle.obstacles[i].getSHAPE() == 1) {
                int obsRadius = Obstacle.obstacles[i].getBOUNDS().width/2;
                if (ExtraMethods.distance(Obstacle.obstacles[i].getBOUNDS().x + obsRadius,
                        Obstacle.obstacles[i].getBOUNDS().y + obsRadius, x, y) <= obsRadius) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Update
    // In this method, lines with slope (tan(theta)) are calculated in all direction. It finds keyPoints in which addGraphics will show red when there is an obstacle
    public void update() {
        if (showReds) {
            int increment;
            for (int i = 0; i < 72; i++) {
                keyPoints[i] = new Point(-1,-1);
                double angle = Math.toRadians(i*5);
                if (i*5 != 90 && i*5 != 270) {
                    if (i*5 <= 135 || i*5 > 315) {
                        increment = 1;
                    } else {
                        increment = -1;
                    }
                    double slope = Math.tan(angle);

                    if (Math.abs(slope) < 1) {
                        for (int j = 0; Math.abs(j) < Math.abs(radius*Math.cos(angle)); j += increment) {
                            double progressX; double progressY;
                            progressX = j + position.x;
                            progressY = (int)(slope*j + position.y);
                            checkObstacles(progressX,progressY);
                            if (checkObstacles(progressX,progressY)) {
                                keyPoints[i] = new Point((int)progressX,(int)progressY);
                                break;
                            }
                        }
                    } else if (Math.abs(slope) > 1) {
                        for (int j = 0; Math.abs(j) < Math.abs(radius*Math.sin(angle)); j += increment) {
                            double progressX; double progressY;
                            progressY = j + position.y;
                            progressX = (int)((1/slope)*j + position.x);
                            if (checkObstacles(progressX,progressY)) {
                                keyPoints[i] = new Point((int)progressX,(int)progressY);
                                break;
                            }
                        }
                    }

                } else {
                    switch (i*5) {
                        case 90 -> increment = 1;
                        default -> increment = -1;
                    }
                    for (int j = 0; Math.abs(j) < Math.abs(radius); j += increment) {
                            double progressX; double progressY;
                            progressX = position.x;
                            progressY = j + position.y;
                            if (checkObstacles(progressX,progressY)) {
                                keyPoints[i] = new Point((int)progressX,(int)progressY);
                                break;
                            }
                        }

                }
            }
        }
        
    }
    
    // Add graphics method (uses keyPoints from update method)
    public void addGraphics(Graphics2D g, boolean onPath) {
        for (int i = 0; i < 72; i++) {
            double angle = Math.toRadians(i*5);
            if (onPath) {
                g.setColor(Color.RED);
                g.drawLine((int)(position.x), (int)(position.y), 
                        (int)(Math.cos(angle)*radius + position.x), 
                        (int)(Math.sin(angle)*radius + position.y));
            } else if (keyPoints[i].x == -1 || !showReds) {
                g.setColor(new Color(255,255,255));
                g.drawLine((int)(position.x), (int)(position.y), 
                        (int)(Math.cos(angle)*radius + position.x), 
                        (int)(Math.sin(angle)*radius + position.y));
            } else {
                g.setColor(new Color(255,255,255));
                g.drawLine((int)(position.x), (int)(position.y), 
                        (int)keyPoints[i].x, (int)keyPoints[i].y);
                g.setColor(new Color(255,0,0));
                g.drawLine(keyPoints[i].x, keyPoints[i].y, 
                        (int)(Math.cos(angle)*radius + position.x), 
                        (int)(Math.sin(angle)*radius + position.y));
            }
            if (onPath) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawOval((int)(position.x-radius),(int)(position.y-radius),
                    (int)(2*radius),(int)(2*radius));
        }
    }
    
    // Static variables
    public static int getNumRadii() {
        return numRadii;
    }
    
    public static void setNumRadii(int n) {
        numRadii = n;
    }
    
}
