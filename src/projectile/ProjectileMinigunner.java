/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectile;

/** Minigunner projectile - subclass of Projectile
 *
 * @author Logesh Mahendran
 */

// Imports
import balloon.*;
import ics3u_summative_tower_defense.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ProjectileMinigunner extends Projectile {
    
    // Constructor
    public ProjectileMinigunner(double[] p, double a, double s, int t, double r, int u) {
        super(t,a,s,r,p,new Dimension(16,4));
    }
    
    // Overriden doAction method
    @Override
    public void doAction(int u) {
        super.updateDistance();
        super.doAction(u);
        super.setPosition(super.getPosition()[0] + super.getSpeed()*
                    Math.cos(Math.toRadians(super.getAngle())),
                    super.getPosition()[1] + super.getSpeed()*
                    Math.sin(Math.toRadians(super.getAngle())));
        if (super.getTowerUpgrade() <= 3) {
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
    }

    // Overriden addGraphics method
    @Override
    public void addGraphics(Graphics2D g) {
        BufferedImage sprite;
        switch (super.getTowerUpgrade()) {
            case 3 -> sprite = ExtraMethods.loadImage(getClass().getResource("/img/projectile_minigunner_1.png"));
            default -> sprite = ExtraMethods.loadImage(getClass().getResource("/img/projectile_minigunner_0.png"));
        }
        if (super.getTowerUpgrade() >= 3) {
            super.setDimensions(new Dimension(32,8));
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
