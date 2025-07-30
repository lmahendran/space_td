/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tower;

/** Flyer tower - subclass of Tower
 * 
 *
 * @author Logesh Mahendran
 */

// Imports
import balloon.*;
import ics3u_summative_tower_defense.*;
import projectile.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Collections;

public class TowerFlyer extends Tower {
    
    // Instance variables
    int numBullets, pathing, direction;
    double[] planePosition, prevPosition;
    double planeAngle;
    boolean centered, seeking;
    Dimension planeDimension;
    Point center;
    
    // Constructor
    public TowerFlyer(Point p) {
        super(2,2,-1,false,new Dimension(40,40),5,p);
        center = new Point(-100,-100);
        planeAngle = 0;
        centered = false;
        seeking = false;
        pathing = 0;
        direction = 1;
        planePosition = new double[2];
        prevPosition = new double[2];
        planeDimension = new Dimension(40,40);
        planePosition[0] = center.x + 100*Math.cos(Math.toRadians(0));
        planePosition[1] = center.y + 100*Math.sin(Math.toRadians(0));
        numBullets = 8;
    }
    
    // Getters
    public int getPathing() {
        return pathing;
    }
    
    // Setters
    public void setPathing(int p) {
        pathing = p;
    }
    
    // Overriding updateTargetEnemy to satisfy certain plane tower conditions
    @Override
    public void updateTargetEnemy() {
        if (super.getCurrentUpgrade() < 3) {
            super.setTargetEnemy(-1);
            super.setTargetIndex(-1);
        } else {
            super.updateTargetEnemy();
        }
    }
    
    // Overriden doAction method
    @Override
    public void doAction() {
        super.doAction();
        super.setTargetIndex(-1);
        if (super.getTargetEnemy() != -1) {
            int lowest = 0, highest = Balloon.balloons.size()-1;
            if (highest == 0) {
                super.setTargetIndex(0);
            } else if (Balloon.balloons.size() > 1) {
                while (lowest <= highest) {
                    int guess = (lowest+highest)/2;
                    if (Balloon.balloons.get(guess).getBalloonID() == super.getTargetEnemy()) {
                        super.setTargetIndex(guess);
                        break;
                    } else if (Balloon.balloons.get(guess).getBalloonID() < super.getTargetEnemy()) {
                        lowest = guess+1;
                    } else {
                        highest = guess-1;
                    }
                }
            }
        }
        
        // Update position and angle
        if (centered) {
            center = new Point(400,300);
        } else {
            center = super.getPosition();
        }
        planeAngle += 0.1*direction;
        prevPosition[0] = planePosition[0];
        prevPosition[1] = planePosition[1];
        if (pathing == 0) {
            planePosition[0] = center.x + 100*Math.cos(Math.toRadians(planeAngle));
            planePosition[1] = center.y + 100*Math.sin(Math.toRadians(planeAngle));
        } else {
            planePosition[0] = center.x + 200*Math.cos(Math.toRadians(planeAngle));
            planePosition[1] = center.y + 75*Math.sin(2*Math.toRadians(planeAngle));
        }

        if ((super.getTargetIndex() != -1 && super.getCurrentUpgrade() == 4) ||
                (super.getCurrentUpgrade() != 4)) {
        
            if (super.getTowerTimer()[1] - super.getTowerTimer()[0] >= 
                    super.getSpeed()*super.getModifiers()[0]*1000) {

                // Make projectiles
                double bulletSpeed = 2;
                if (super.getCurrentUpgrade() == 4) {
                    bulletSpeed = 4;
                }
                for (int i = 0; i < numBullets; i++) {
                    super.projectiles.add(new ProjectileFlyer(planePosition,
                        i*360/numBullets,bulletSpeed,super.getTargetIndex(),
                        super.getRange()*super.getModifiers()[1],super.getCurrentUpgrade(),seeking));
                }
                super.setTowerTimer(0,super.getTowerClock().millis());
            }
        }

        // Update projectiles
        for (int i = 0; i < super.projectiles.size(); i++) {
            if (super.projectiles.get(i) != null) {
                if (super.projectiles.get(i) instanceof ProjectileFlyer) {
                    ProjectileFlyer tempProj = (ProjectileFlyer)(super.projectiles.get(i));
                    if (tempProj.getSeekingProperty()) {
                        super.projectiles.get(i).setTarget(super.getTargetEnemy());
                    }
                }
                super.projectiles.get(i).doAction(super.getCurrentUpgrade());
                if ((super.projectiles.get(i).getHitEnemy()) ||
                        super.projectiles.get(i).reachedEdge()) {
                    if (super.projectiles.get(i).getHitEnemy()) {
                        double damage = Balloon.balloons.get(super.projectiles
                                .get(i).getTargetIndex()).getHealth()-super.getAction();
                        if (damage < 0) {
                            damage = Balloon.balloons.get(super.projectiles
                                    .get(i).getTargetIndex()).getHealth();
                        } else {
                            damage = super.getAction();
                        }
                        Balloon.balloons.get(super.projectiles.get(i)
                                .getTargetIndex()).changeHealth(-1*super.getAction());
                        super.changeDamageDealt(damage);
                    }
                    super.projectiles.set(i,null);
                }
            }
        }
        super.projectiles.removeAll(Collections.singletonList(null));
        
    }
    
    // Overriden updateTowerStats method
    @Override
    public void updateTowerStats(int userCash) {
        super.getTowerStats().updateDisplay(userCash, super.getDamageDealt(), 
                statsToArray(), super.getModifiers(), super.getHiddenVision());
        if (super.getCurrentUpgrade() < super.getTowerStats().getCurrentUpgrade()) {
            super.setCurrentUpgrade(super.getTowerStats().getCurrentUpgrade());
            String[] newStats = super.getTowerStats().getTowerUpgrades()[super.getCurrentUpgrade()-1][1].split("!");
            for (int i = 0; i < newStats.length; i++) {
                String[] singleStatInfo = newStats[i].split(":");
                if (singleStatInfo[0].equals("s")) {
                    super.setSpeed(Double.parseDouble(singleStatInfo[1]));
                } else if (singleStatInfo[0].equals("n")) {
                    numBullets = Integer.parseInt(singleStatInfo[1]);
                } else if (singleStatInfo[0].equals("cE")) {
                    centered = (Integer.parseInt(singleStatInfo[1]) == 1);
                } else if (singleStatInfo[0].equals("pS")) {
                    seeking = (Integer.parseInt(singleStatInfo[1]) == 1);
                } else if (singleStatInfo[0].equals("a")) {
                    super.setAction(Double.parseDouble(singleStatInfo[1]));
                }
            }
        }
        if (super.getTargeting() != super.getTowerStats().getTargeting()) {
            super.setTargeting(super.getTowerStats().getTargeting());
        }
        pathing = super.getTowerStats().getPlanePath();
        direction = super.getTowerStats().getPlaneDirection();
        if (super.getCurrentUpgrade() == 4) {
            planeDimension = new Dimension(80,80);
        }
    }
    
    // Overriden statsToArray method
    @Override
    public String[] statsToArray() {
        String[] statsArray = {
            "Attack: " + String.valueOf(super.getAction()*super.getModifiers()[3]),
            "Speed: " + String.valueOf(super.getSpeed()*super.getModifiers()[0]),
            "Range: Infinite"
        };
        return statsArray;
    }
    
    // Draw plane method
    public void drawPlane(Graphics2D g) {
        
        // Draw path
        if (Tower.getSelectedTower() != -1 && Tower.towers[Tower.getSelectedTower()] == this) {
            g.setColor(new Color(255,0,0,50));
            for (int i = 0; i < 45; i++) {
                int circleX, circleY;
                if (pathing == 0) {
                    circleX = (int)(center.x + 100*Math.cos(Math.toRadians(i*8)));
                    circleY = (int)(center.y + 100*Math.sin(Math.toRadians(i*8)));
                } else {
                    circleX = (int)(center.x + 200*Math.cos(Math.toRadians(i*8)));
                    circleY = (int)(center.y + 75*Math.sin(2*Math.toRadians(i*8)));
                }
                g.fillOval(circleX-4,circleY-4,8,8);
            }
        }
        
        // Draw plane
        BufferedImage planeSprite = ExtraMethods.loadImage(getClass().getResource("/img/flyer_plane_sprite_" + 
                super.getCurrentUpgrade() + ".png"));
        AffineTransform planeTransform = new AffineTransform();
        double radiansAngle;
        if (pathing == 0) {
            radiansAngle = Math.toRadians(planeAngle+90*direction);
        } else {
            radiansAngle = Math.atan2(planePosition[1]-prevPosition[1],
                    planePosition[0]-prevPosition[0]);
        }
        int newWidth = (int) Math.abs(planeDimension.width * 
                Math.cos(radiansAngle)) + (int) Math.abs(planeDimension.height * Math.sin(radiansAngle));
        int newHeight = (int) Math.abs(planeDimension.height * Math.cos(radiansAngle)) + 
                        (int) Math.abs(planeDimension.width * Math.sin(radiansAngle));
        planeTransform.rotate(radiansAngle,newWidth/2,newHeight/2);
        planeTransform.translate((newWidth-planeDimension.width)/2,
                (newHeight-planeDimension.height)/2);
        BufferedImage rotatedPlane = new BufferedImage(newWidth,newHeight,planeSprite.getType());
        rotatedPlane.createGraphics().drawImage(planeSprite,planeTransform,null);
        g.drawImage(rotatedPlane,(int)(planePosition[0]-newWidth*0.5),
                (int)(planePosition[1]-newHeight*0.5),null);
    }
    
    // Overriden addGraphics method
    @Override
    public void addGraphics(Graphics2D g) {
        super.addGraphics(g);
        
        // Draw tower
        BufferedImage towerSprite = ExtraMethods.loadImage(getClass().getResource("/img/flyer_sprite_" + 
                super.getCurrentUpgrade() + ".png"));
        g.drawImage(towerSprite,super.getScreenPos().x, super.getScreenPos().y,null); 
        
        
    }
    
    
}
