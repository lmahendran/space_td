/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectile;

/** Projectile class
 * 
 *
 * @author Logesh Mahendran
 */

import balloon.*;
import ics3u_summative_tower_defense.*;
import java.awt.*;

public class Projectile {
    
    // Instance variables
    private double[] position;
    private Point screenPos;
    private double angle, prevAngle, speed, distance;
    private int target, targetIndex, towerUpgrade;
    private boolean hitEnemy;
    private Dimension dimensions;
    
    // Final variables
    public double[] TOWER_POSITION;
    private double MAX_DISTANCE;
    
    // Constructor
    public Projectile(int t, double a, double s, double r, double[] p,
            Dimension d) {
        targetIndex = t; speed = s; hitEnemy = false;
        if (targetIndex == -1) {
            target = -1;
        } else {
            target = Balloon.balloons.get(targetIndex).getBalloonID();
        }
        MAX_DISTANCE = r;
        angle = a;
        distance = 0;
        position = new double[2];
        position[0] = p[0];
        position[1] = p[1];
        TOWER_POSITION = new double[2];
        TOWER_POSITION[0] = p[0];
        TOWER_POSITION[1] = p[1];
        dimensions = d;
        screenPos = new Point((int)(position[0]-dimensions.width/2),
                (int)(position[1]-dimensions.height/2));
    }
    
    // Getter methods
    public double[] getPosition() {
        return position;
    }
    
    public Point getScreenPos() {
        return screenPos;
    }
    
    public double getAngle() {
        return angle;
    }
    
    public double getPrevAngle() {
        return prevAngle;
    }
    
    public int getTarget() {
        return target;
    }
    
    public int getTargetIndex() {
        return targetIndex;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public double getDistance() {
        return distance;
    }
    
    public boolean getHitEnemy() {
        return hitEnemy;
    }
    
    public Dimension getDimensions() {
        return dimensions;
    }
    
    public double getMAX_DISTANCE() {
        return MAX_DISTANCE;
    }
    
    public int getTowerUpgrade() {
        return towerUpgrade;
    }
    
    // Setter methods
    public void setPosition(double x, double y) {
        position[0] = x; position[1] = y;
        screenPos = new Point((int)(position[0]-dimensions.width/2),
                (int)(position[1]-dimensions.height/2));
    }
    
    public void setAngle(double a) {
        angle = a;
        if (angle < 0) {
            angle += 360;
        }
    }
    
    public void setPrevAngle(double a) {
        prevAngle = a;
    }
    
    public void setDimensions(Dimension d) {
        dimensions = d;
    }
    
    public void setSpeed(double s) {
        speed = s;
    }
    
    public void setTarget(int t) {
        target = t;
    }
    
    public void setTargetIndex(int t) {
        targetIndex = t;
    }
    
    public void setHitEnemy(boolean hE) {
        hitEnemy = hE;
    }
    
    public void setTowerUpgrade(int t) {
        towerUpgrade = t;
    }
    
    // Other instance methods
    public void updateDistance() {
        distance = ExtraMethods.distance(position[0],position[1],
                TOWER_POSITION[0],TOWER_POSITION[1]);
    }
    
    // Detects if the edge is reached and returns it as a boolean
    public boolean reachedEdge() {
        return (position[0] < 0 || position[0] > 800 || position[1] < 0 ||
                position[1] > 600);
    }
    
    // To be overriden by subclasses
    public void doAction(int u) {
        towerUpgrade = u;
    }
    
    // To be overriden by subclasses
    public void addGraphics(Graphics2D g) {
        
    }
    
}
