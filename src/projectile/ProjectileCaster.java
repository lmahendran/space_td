/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectile;

/** Caster projectile  - subclass of Projectile
 *
 * @author Logesh Mahendran
 */

// Imports
import balloon.*;
import ics3u_summative_tower_defense.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ProjectileCaster extends Projectile {
    
    // Constructor
    public ProjectileCaster(double[] p, double a, double s, int t, double r) {
        super(t,a,s,r,p,new Dimension(10,8));
    }
    
    // Overriden doAction method
    @Override
    public void doAction(int u) {
        super.updateDistance();
        super.doAction(u);
        if (u == 4) {
            super.setDimensions(new Dimension(20,16));
        }
        try {
            // Get target with binary search
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
            // Always move toward the target (angle and position-wise)
            if (super.getTargetIndex() != -1) {
                double[] targetPos = new double[2];
                targetPos[0] = Balloon.balloons.get(super.getTargetIndex()).getPosition()[0];
                targetPos[1] = Balloon.balloons.get(super.getTargetIndex()).getPosition()[1];
                super.setAngle(Math.toDegrees(Math.atan2(targetPos[1]-super.getPosition()[1],
                        targetPos[0]-super.getPosition()[0])));
                // System.out.println(super.getAngle());
                if (ExtraMethods.distance(super.getPosition()[0],super.getPosition()[1],
                        targetPos[0],targetPos[1]) <= 3) {
                    super.setHitEnemy(true);
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
            case 4 -> sprite = ExtraMethods.loadImage(getClass().getResource("/img/projectile_caster_1.png"));
            default -> sprite = ExtraMethods.loadImage(getClass().getResource("/img/projectile_caster_0.png"));
        }
        // Uses an AffineTransform object to apply a transformation onto a Graphics object
        AffineTransform spriteTransform = new AffineTransform();
        double radiansAngle = Math.toRadians(super.getAngle());
        // The formula below is the one that calculates the bounds of the container Rectangle of a rotated rectangle, based on width, height of original rectangle and the angle.
        int newWidth = (int) Math.abs(super.getDimensions().width * 
                Math.cos(radiansAngle)) + (int) Math.abs(super.getDimensions().height * Math.sin(radiansAngle));
        int newHeight = (int) Math.abs(super.getDimensions().height * Math.cos(radiansAngle)) + 
                        (int) Math.abs(super.getDimensions().width * Math.sin(radiansAngle));
        spriteTransform.rotate(radiansAngle,newWidth/2,newHeight/2);
        spriteTransform.translate((newWidth-super.getDimensions().width)/2,
                newHeight-super.getDimensions().height);
        BufferedImage rotatedSprite = new BufferedImage(newWidth,newHeight,sprite.getType());
        rotatedSprite.createGraphics().drawImage(sprite,spriteTransform,null);
        g.drawImage(rotatedSprite,(int)(super.getPosition()[0]-newHeight/2),
                (int)(super.getPosition()[1]-newHeight*0.5),null);
    }
    
}
