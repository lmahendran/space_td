/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectile;

/** Flyer projectile - subclass of Projectile
 *
 * @author Logesh Mahendran
 */

// Imports
import balloon.*;
import ics3u_summative_tower_defense.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.time.Clock;

public class ProjectileFlyer extends Projectile {
    
    // Instance variables
    private Clock projectileClock;
    private long launchTime;
    private boolean currentlySeeking;
    private boolean seekingProperty;
    
    // Constructor
    public ProjectileFlyer(double[] p, double a, double s, int t, double r, int u,
            boolean seeking) {
        super(t,a,s,r,p,new Dimension(24,8));
        projectileClock = Clock.systemDefaultZone();
        launchTime = projectileClock.millis();
        seekingProperty = seeking;
        currentlySeeking = seeking;
    }
    
    // Getters
    public boolean getSeekingProperty() {
        return seekingProperty;
    }
    
    // Overriden doAction method
    @Override
    public void doAction(int u) {
        super.updateDistance();
        super.doAction(u);
        
        try {
            super.setTargetIndex(-1);
            if (super.getTarget() != -1) {
                int lowest = 0, highest = Balloon.balloons.size()-1;
                if (highest == 0) {
                    super.setTargetIndex(0);
                } else if (Balloon.balloons.size() > 1) {
                    while (lowest <= highest) {
                        int guess = (lowest+highest)/2;
                        if (Balloon.balloons.get(guess).getBalloonID() == super.getTarget()) {
                            super.setTargetIndex(guess);
                            break;
                        } else if (Balloon.balloons.get(guess).getBalloonID() < super.getTarget()) {
                            lowest = guess+1;
                        } else {
                            highest = guess-1;
                        }
                    }
                }
            }
            // If seeking is on, then wait a small bit of time before trying to reroute
            if (super.getTargetIndex() != -1 && (!seekingProperty || (seekingProperty && projectileClock.millis() - launchTime >= 200))) {
                double[] targetPos = new double[2];
                targetPos[0] = Balloon.balloons.get(super.getTargetIndex()).getPosition()[0];
                targetPos[1] = Balloon.balloons.get(super.getTargetIndex()).getPosition()[1];
                double targetAngle = Math.toDegrees(Math.atan2(targetPos[1]-super.getPosition()[1],
                        targetPos[0]-super.getPosition()[0]));
                if (targetAngle < 0) {
                    targetAngle += 360;
                }

                // Find smallest angle to target
                double angleDistance = Math.abs(super.getAngle()-targetAngle);
                if (360-angleDistance < angleDistance) {
                    angleDistance = 360-angleDistance;
                }
                
                if (angleDistance > 5 && currentlySeeking && seekingProperty) {
                    double tempDistance = Math.abs((super.getAngle()+1)%360 - targetAngle);
                    if (360-tempDistance < tempDistance) {
                        tempDistance = 360-tempDistance;
                    }
                    // Increment angle change to make it look more gradual
                    if (tempDistance < angleDistance) {
                        super.setAngle(super.getAngle()+1.5);
                    } else {
                        super.setAngle(super.getAngle()-1.5);
                    }
                } else {
                    currentlySeeking = false;
                    super.setAngle(Math.toDegrees(Math.atan2(targetPos[1]-super.getPosition()[1],
                            targetPos[0]-super.getPosition()[0])));
                }
                if (super.getTowerUpgrade() >= 3) {
                    if (ExtraMethods.distance(super.getPosition()[0],super.getPosition()[1],
                            targetPos[0],targetPos[1]) <= 3) {
                        super.setHitEnemy(true);
                    }
                }
            }
            // If not seeking, do not target any balloon at all
            if (super.getTowerUpgrade() <= 2) {
                for (int i = 0; i < Balloon.balloons.size(); i++) {
                    if (ExtraMethods.distance(super.getPosition()[0],super.getPosition()[1],
                            Balloon.balloons.get(i).getPosition()[0],
                            Balloon.balloons.get(i).getPosition()[1]) <= 10) {
                        super.setHitEnemy(true);
                        super.setTargetIndex(i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            super.setTarget(-1);
        }
        super.setPosition(super.getPosition()[0] + super.getSpeed()*
                    Math.cos(Math.toRadians(super.getAngle())),
                    super.getPosition()[1] + super.getSpeed()*
                    Math.sin(Math.toRadians(super.getAngle())));
    }
    
    // Overriden addGraphics method
    @Override
    public void addGraphics(Graphics2D g) {
        BufferedImage sprite;
        switch (super.getTowerUpgrade()) {
            case 4 -> sprite = ExtraMethods.loadImage(getClass().getResource("/img/projectile_flyer_2.png"));
            case 3 -> sprite = ExtraMethods.loadImage(getClass().getResource("/img/projectile_flyer_1.png"));
            default -> sprite = ExtraMethods.loadImage(getClass().getResource("/img/projectile_flyer_0.png"));
        }
        if (super.getTowerUpgrade() == 4) {
            super.setDimensions(new Dimension(24,16));
        }
        AffineTransform spriteTransform = new AffineTransform();
        double radiansAngle = Math.toRadians(super.getAngle());
        int newWidth = (int) Math.abs(super.getDimensions().width * 
                Math.cos(radiansAngle)) + (int) Math.abs(super.getDimensions().height * Math.sin(radiansAngle));
        int newHeight = (int) Math.abs(super.getDimensions().height * Math.cos(radiansAngle)) + 
                        (int) Math.abs(super.getDimensions().width * Math.sin(radiansAngle));
        spriteTransform.rotate(radiansAngle,newWidth/2,newHeight/2);
        spriteTransform.translate((newWidth-super.getDimensions().width)/2,
                (newHeight-super.getDimensions().height)/2);
        BufferedImage rotatedSprite = new BufferedImage(newWidth,newHeight,sprite.getType());
        rotatedSprite.createGraphics().drawImage(sprite,spriteTransform,null);
        g.drawImage(rotatedSprite,(int)(super.getPosition()[0]-newWidth/2),
                (int)(super.getPosition()[1]-newHeight/2),null);
    }
    
}
