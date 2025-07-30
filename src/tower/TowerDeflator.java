/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tower;

/** Deflator tower - subclass of Tower
 *
 * @author Logesh Mahendran
 */

// Imports
import balloon.*;
import ics3u_summative_tower_defense.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class TowerDeflator extends Tower {
    
    // Instance variables
    double speedDebuff, regrowTime;
    boolean removeRegrow, abilityBuff;
    private long[] abilityTimer;
    private int abilityStage;;
    
    
    // Constructor
    public TowerDeflator(Point p) {
        super(0,0,100,false,new Dimension(40,40),3,p);
        speedDebuff = 0.9; regrowTime = 3;
        removeRegrow = false;
        abilityBuff = false;
        abilityStage = 0;
        abilityTimer = new long[3];
    }
    
    // Getters
    public int getAbilityStage() {
        return abilityStage;
    }
    
    public long[] getAbilityTimer() {
        return abilityTimer;
    }
    
    // Setters
    public void setAbilityStage(int a) {
        abilityStage = a;
    }
    
    // Freeze ability timer
    @Override
    public void freezeTimers() {
        super.freezeTimers();
        long delay = abilityTimer[1] - abilityTimer[0];
        abilityTimer[0] = super.getTowerClock().millis();
        abilityTimer[1] = abilityTimer[0] + delay;
    }
    
    // Overriden doAction method
    @Override
    public void doAction() {
        super.doAction();
        
        // Ability Timer
        if (!abilityBuff) {
            abilityTimer[0] = super.getTowerClock().millis();
            abilityTimer[1] = super.getTowerClock().millis();
            abilityTimer[2] = super.getTowerClock().millis();
        } else {
            if (abilityStage == 2) {
                speedDebuff = 0;
                abilityTimer[1] = super.getTowerClock().millis();
                abilityTimer[2] = super.getTowerClock().millis();
                if (abilityTimer[2] - abilityTimer[0] >= 5000) {
                    abilityStage = 0;
                    abilityTimer[2] = abilityTimer[0];
                }
            } else if (abilityStage == 0) {
                speedDebuff = 0.4;
                abilityTimer[1] = super.getTowerClock().millis();
                if (abilityTimer[1] - abilityTimer[0] >= 20000) {
                    abilityStage = 1;
                }
            } else if (abilityStage == 1) {
                abilityTimer[0] = super.getTowerClock().millis();
                abilityTimer[1] = super.getTowerClock().millis();
                abilityTimer[2] = super.getTowerClock().millis();
            }
        }
        
        for (int i = 0; i < Balloon.balloons.size(); i++) {
            if (Balloon.balloons.get(i) != null) {
                if ((abilityStage != 2 && ExtraMethods.distance(super.getPosition().x,super.getPosition().y,
                            Balloon.balloons.get(i).getPosition()[0],
                            Balloon.balloons.get(i).getPosition()[1]) 
                        <= super.getRange()*super.getModifiers()[1]) ||
                        (abilityStage == 2)) {

                    Balloon.balloons.get(i).setSModifier(speedDebuff);
                    Balloon.balloons.get(i).setRegrowTime(regrowTime);
                }
            }
        }
        
        // Remove hidden property
        super.setTowerTimer(1,super.getTowerClock().millis());
        if (removeRegrow && super.getTowerTimer()[1] - super.getTowerTimer()[0]
                >= 3000*super.getModifiers()[0]) {
            for (int j = 0; j < Balloon.balloons.size(); j++) {
                if (Balloon.balloons.get(j) != null) {
                    if (ExtraMethods.distance(super.getPosition().x,super.getPosition().y,
                                Balloon.balloons.get(j).getPosition()[0],
                                Balloon.balloons.get(j).getPosition()[1]) 
                            <= super.getRange()*super.getModifiers()[1]) {
                        int newEffect;
                        if (Balloon.balloons.get(j).getEffect() == 3) {
                            newEffect = 1;
                        } else if (Balloon.balloons.get(j).getEffect() == 2) {
                            newEffect = 0;
                        } else {
                            newEffect = Balloon.balloons.get(j).getEffect();
                        }
                        Balloon.balloons.get(j).setEffect(newEffect);
                    }
                }
            }
            super.setTowerTimer(0,super.getTowerTimer()[1]);
        }
        
        
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
                if (singleStatInfo[0].equals("sD")) {
                    speedDebuff = Double.parseDouble(singleStatInfo[1]);
                } else if (singleStatInfo[0].equals("rT")) {
                    regrowTime = Double.parseDouble(singleStatInfo[1]);
                } else if (singleStatInfo[0].equals("rA")) {
                    removeRegrow = (Integer.parseInt(singleStatInfo[1]) == 1);
                } else if (singleStatInfo[0].equals("r")) {
                    super.setRange(Double.parseDouble(singleStatInfo[1]));
                } else if (singleStatInfo[0].equals("P")) {
                    abilityBuff = (Integer.parseInt(singleStatInfo[1]) == 1);
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
            "Speed Debuff: " + ExtraMethods.round(100*(1-speedDebuff),4) + "%",
            "Regrow Time: " + regrowTime + "s",
            "Range: " + (super.getRange()*super.getModifiers()[1])
        };
        return statsArray;
    }
    
    // Overriden addGraphics method
    @Override
    public void addGraphics(Graphics2D g) {
        super.addGraphics(g);
        BufferedImage sprite = ExtraMethods.loadImage(getClass().getResource("/img/deflator_sprite_" + 
                super.getCurrentUpgrade() + ".png"));
        AffineTransform spriteTransform = new AffineTransform();
        spriteTransform.rotate(Math.toRadians(super.getAngle()),super.getDimensions().width/2,super.getDimensions().height/2);
        BufferedImage rotatedSprite = new BufferedImage(40,40,sprite.getType());
        rotatedSprite.createGraphics().drawImage(sprite,spriteTransform,null);
        
        // Special radius
        if (super.getCurrentUpgrade() == 4) {
            double tempRadius = super.getRange()*super.getModifiers()[1];
            int circleRadius = (int)(tempRadius*(super.getTowerTimer()[1]
                    -super.getTowerTimer()[0])/(3000.0*super.getModifiers()[0]));
            g.setColor(new Color(255,0,255,50));
            g.drawOval(super.getPosition().x-circleRadius,super.getPosition().y-circleRadius,
                    2*circleRadius,2*circleRadius);
        }
        
        g.drawImage(rotatedSprite, super.getScreenPos().x, super.getScreenPos().y,null);
    }
    
}