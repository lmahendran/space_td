/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tower;

/** Booster Tower  - subclass of Tower
 *
 * @author Logesh Mahendran
 */

// Imports
import ics3u_summative_tower_defense.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TowerBooster extends Tower {
    
    // Instance variables
    private double[] towerBuffs;
    private boolean hiddenVisionBuff, abilityBuff;
    private long[] abilityTimer;
    private int abilityStage;
    
    // Constructor
    public TowerBooster(Point p) {
        super(0,0,90,false,new Dimension(40,40),2,p);
        towerBuffs = new double[4];
        for (int i = 0; i < 4; i++) {
            towerBuffs[i] = 1;
        }
        hiddenVisionBuff = false;
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
                towerBuffs[3] = 1.4;
                towerBuffs[0] = 0.5;
                abilityTimer[1] = super.getTowerClock().millis();
                abilityTimer[2] = super.getTowerClock().millis();
                if (abilityTimer[2] - abilityTimer[0] >= 5000) {
                    abilityStage = 0;
                    abilityTimer[2] = abilityTimer[0];
                }
            } else if (abilityStage == 0) {
                towerBuffs[3] = 1;
                towerBuffs[0] = 0.8;
                abilityTimer[1] = super.getTowerClock().millis();
                if (abilityTimer[1] - abilityTimer[0] >= 30000) {
                    abilityStage = 1;
                }
            } else if (abilityStage == 1) {
                abilityTimer[0] = super.getTowerClock().millis();
                abilityTimer[1] = super.getTowerClock().millis();
                abilityTimer[2] = super.getTowerClock().millis();
            }
        }
        
        // Boosting other towers
        for (int i = 0; i < Tower.getNumTowers(); i++) {
            if (!(Tower.towers[i] instanceof TowerGenerator)) {
                if (Tower.towers[i] != null && Tower.towers[i] != this) {
                    if (ExtraMethods.distance(super.getPosition().x,super.getPosition().y,
                                Tower.towers[i].getPosition().x,
                                Tower.towers[i].getPosition().y) 
                            <= super.getRange()*super.getModifiers()[1]) {
                        for (int j = 0; j < 4; j++) {
                            // Update modifiers
                            if ((j % 2 == 0 && towerBuffs[j] < Tower.towers[i].getModifiers()[j]) ||
                                    (j % 2 == 1 && towerBuffs[j] > Tower.towers[i].getModifiers()[j])) {
                                Tower.towers[i].setModifiers(j,towerBuffs[j]);
                            }
                        }
                        // Hidden vision buff
                        Tower.towers[i].setHiddenVision(hiddenVisionBuff);
                    }
                }
            }
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
                if (singleStatInfo[0].equals("sB")) {
                    towerBuffs[0] = Double.parseDouble(singleStatInfo[1]);
                } else if (singleStatInfo[0].equals("cB")) {
                    towerBuffs[2] = Double.parseDouble(singleStatInfo[1]);
                } else if (singleStatInfo[0].equals("rB")) {
                    towerBuffs[1] = Double.parseDouble(singleStatInfo[1]);
                } else if (singleStatInfo[0].equals("c")) {
                    hiddenVisionBuff = (Integer.parseInt(singleStatInfo[1]) == 3);
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
            "Speed Buff: " + ExtraMethods.round(100*(1-towerBuffs[0]),4) + "%",
            "Range Buff: " + ExtraMethods.round(100*(towerBuffs[1]-1),4) + "%",
            "Discount: " + ExtraMethods.round(100*(1-towerBuffs[2]),4) + "%",
            "Attack Buff: " + ExtraMethods.round(100*(towerBuffs[3]-1),4) + "%",
            "Range: " + String.valueOf(super.getRange()*super.getModifiers()[1])
        };
        return statsArray;
    }
    
    // Overriden addGraphics method
    @Override
    public void addGraphics(Graphics2D g) {
        super.addGraphics(g);
        BufferedImage sprite = ExtraMethods.loadImage(getClass().getResource("/img/booster_sprite_" + 
                super.getCurrentUpgrade() + ".png"));
        AffineTransform spriteTransform = new AffineTransform();
        spriteTransform.rotate(Math.toRadians(super.getAngle()),super.getDimensions().width/2,super.getDimensions().height/2);
        BufferedImage rotatedSprite = new BufferedImage(40,40,sprite.getType());
        rotatedSprite.createGraphics().drawImage(sprite,spriteTransform,null);
        
        // Special radius
        // Uses a trigonometric function to smoothly display the pulsing radius
        if (super.getCurrentUpgrade() == 4) {
            int tempRange = (int)(super.getRange()*super.getModifiers()[1]);
            g.setColor(new Color(0,255,255,(int)
                    (-45*Math.cos(2*Math.PI*super.getTowerTimer()[1]*0.001) + 50)));
            g.fillOval(super.getPosition().x-tempRange,super.getPosition().y-tempRange,
                    2*tempRange,2*tempRange);
        }
        
        g.drawImage(rotatedSprite, super.getScreenPos().x, super.getScreenPos().y,null);
    }
    
}
