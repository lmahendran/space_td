/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ics3u_summative_tower_defense;

/** Obstacle class
 *
 * @author Logesh Mahendran
 */

import java.awt.*;

public class Obstacle {
    
    // Instance/final variables
    private final int SHAPE; // 0 for rectangle, 1 for circle
    private final Rectangle BOUNDS;
    
    // Static variables
    private static int numObstacles;
    
    // Array
    public static Obstacle[] obstacles;
    
    // Constructor
    public Obstacle(int x, int y, int w, int h, int s) {
        SHAPE = s;
        BOUNDS = new Rectangle(x,y,w,h);
        numObstacles++;
    }
    
    // Getter methods
    public int getSHAPE() {
        return SHAPE;
    }
    
    public Rectangle getBOUNDS() {
        return BOUNDS;
    }
    
    // Other instance methods
    public void addGraphics(Graphics2D g) {
        g.setColor(Color.WHITE);
        if (SHAPE == 0) {
            g.fillRect(BOUNDS.x,BOUNDS.y,BOUNDS.width,BOUNDS.height);
        } else {
            g.fillOval(BOUNDS.x,BOUNDS.y,BOUNDS.width,BOUNDS.height);
        }
    }
    
    // Static methods
    public static int getNumObstacles() {
        return numObstacles;
    }
    
    public static void setNumObstacles(int n) {
        numObstacles = n;
    }
    
}
