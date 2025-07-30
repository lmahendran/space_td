/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tower;

/** Shooter tower - subclass of Tower
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

public class TowerShooter extends Tower {
    
    // Constructor
    public TowerShooter(Point p) {
        super(1,1,100,false,new Dimension(40,40),0,p);
    }
    
    // Overriden doAction method
    @Override
    public void doAction() {
        
        // Update hidden vision (mainly to make sure Boosters don't remove it)
        if (super.getCurrentUpgrade() == 4) {
            super.setHiddenVision(true);
        }
        
        super.doAction();
        
        // Search for target index
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
        
        if (super.getTargetIndex() != -1) {
            
            if (super.getTowerTimer()[1] - super.getTowerTimer()[0] >= 
                    super.getSpeed()*super.getModifiers()[0]*1000) {
                
                // Update angle
                super.setAngle(Math.toDegrees(Math.atan2(
                    super.getPosition().y - Balloon.balloons.get
                    (super.getTargetIndex()).getPosition()[1],
                    super.getPosition().x - Balloon.balloons.get
                    (super.getTargetIndex()).getPosition()[0])));
                
                // Make projectile
                double[] positionArray = {
                    super.getPosition().x,super.getPosition().y
                };
                int projectileSpeed;
                switch (super.getCurrentUpgrade()) {
                    case 4 -> projectileSpeed = 3;
                    default -> projectileSpeed = 3;
                }
                super.projectiles.add(new ProjectileShooter(positionArray,
                    super.getAngle(),projectileSpeed,super.getTargetIndex(),
                        super.getRange()*super.getModifiers()[1]));
                super.setTowerTimer(0,super.getTowerClock().millis());
            }
        }

        // Update projectiles
        for (int i = 0; i < super.projectiles.size(); i++) {
            if (super.projectiles.get(i) != null) {
                super.projectiles.get(i).doAction(super.getCurrentUpgrade());
                if (super.projectiles.get(i).getHitEnemy() ||
                        super.projectiles.get(i).reachedEdge()) {
                    if (super.projectiles.get(i).getHitEnemy()) {
                        double damage = Balloon.balloons.get(super.projectiles.get(i).getTargetIndex()).getHealth()-super.getAction();
                        if (damage < 0) {
                            damage = Balloon.balloons.get(super.projectiles.get(i).getTargetIndex()).getHealth();
                        } else {
                            damage = super.getAction();
                        }
                        Balloon.balloons.get(super.projectiles.get(i).getTargetIndex()).changeHealth(-1*super.getAction());
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
                } else if (singleStatInfo[0].equals("r")) {
                    super.setRange(Double.parseDouble(singleStatInfo[1]));
                } else if (singleStatInfo[0].equals("a")) {
                    super.setAction(Double.parseDouble(singleStatInfo[1]));
                } else {
                    super.setHiddenVision((Integer.parseInt(singleStatInfo[1]) == 1));
                }
            }
        }
        if (super.getTargeting() != super.getTowerStats().getTargeting()) {
            super.setTargeting(super.getTowerStats().getTargeting());
        }
    }
    
    // Overriden statsToArray method
    @Override
    public String[] statsToArray() {
        String[] statsArray = {
            "Attack: " + String.valueOf(super.getAction()*super.getModifiers()[3]),
            "Speed: " + String.valueOf(super.getSpeed()*super.getModifiers()[0]),
            "Range: " + String.valueOf(super.getRange()*super.getModifiers()[1])
        };
        return statsArray;
    }
    
    // Overriden addGraphics method
    @Override
    public void addGraphics(Graphics2D g) {
        super.addGraphics(g);
        BufferedImage sprite = ExtraMethods.loadImage(getClass().getResource("/img/shooter_sprite_" + 
                super.getCurrentUpgrade() + ".png"));
        AffineTransform spriteTransform = new AffineTransform();
        spriteTransform.rotate(Math.toRadians(super.getAngle()),super.getDimensions().width/2,super.getDimensions().height/2);
        BufferedImage rotatedSprite = new BufferedImage(40,40,sprite.getType());
        rotatedSprite.createGraphics().drawImage(sprite,spriteTransform,null);
        g.drawImage(rotatedSprite, super.getScreenPos().x, super.getScreenPos().y,null);
    }
    
}
