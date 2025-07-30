/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tower;

/** Generator tower - subclass of Tower
 *
 * @author Logesh Mahendran
 */

// Imports
import ics3u_summative_tower_defense.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class TowerGenerator extends Tower {
    
    // Instance variables
    private boolean autoCollect;
    private double cashModifier, cashBuff;
    private int cashStored;
    
    // Static variables
    private static int cashPool = 0;
    
    // Constructor
    public TowerGenerator(Point p) {
        super(6,50,25,false,new Dimension(40,40),4,p);
        autoCollect = false; cashBuff = 1; cashModifier = 1;
        cashStored = 0;
    }
    
    // Getters
    public int getCashStored() {
        return cashStored;
    }
    
    // Setters
    public void setCashStored(int c) {
        cashStored = c;
    }
    
    public void setCashModifier(double c) {
        cashModifier = c;
    }
    
    // Overriden doAction method
    @Override
    public void doAction() {
        super.doAction();
        
        // Making cash
        if (super.getTowerTimer()[1] - super.getTowerTimer()[0] >= super.getSpeed()*1000) {
            cashStored += (int)(super.getAction()*cashModifier);
            if (autoCollect) {
                cashPool += cashStored;
                super.changeDamageDealt(cashStored);
                cashStored = 0;
            }
            super.setTowerTimer(0,super.getTowerTimer()[1]);
        }
        
        // Buffing other generators (upgrade 4)
        if (super.getCurrentUpgrade() == 4) {
            for (int i = 0; i < Tower.getNumTowers(); i++) {
                if (Tower.towers[i] instanceof TowerGenerator) {
                    if (Tower.towers[i] != null && Tower.towers[i] != this) {
                        if (ExtraMethods.distance(super.getPosition().x,super.getPosition().y,
                                    Tower.towers[i].getPosition().x,
                                    Tower.towers[i].getPosition().y) 
                                <= super.getRange()*super.getModifiers()[1]) {
                            TowerGenerator tempGen = (TowerGenerator)Tower.towers[i];
                            if (this.cashBuff > tempGen.cashModifier) {
                                tempGen.cashModifier = this.cashBuff;
                            }
                        }
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
            String[] newStats = super.getTowerStats().getTowerUpgrades()
                    [super.getCurrentUpgrade()-1][1].split("!");
            for (int i = 0; i < newStats.length; i++) {
                String[] singleStatInfo = newStats[i].split(":");
                if (singleStatInfo[0].equals("a")) {
                    super.setAction(Double.parseDouble(singleStatInfo[1]));
                } else if (singleStatInfo[0].equals("s")) {
                    super.setSpeed(Double.parseDouble(singleStatInfo[1]));
                } else if (singleStatInfo[0].equals("r")) {
                    super.setRange(Double.parseDouble(singleStatInfo[1]));
                } else if (singleStatInfo[0].equals("aC")) {
                    autoCollect = (Integer.parseInt(singleStatInfo[1]) == 1);
                } else if (singleStatInfo[0].equals("b")) {
                    cashBuff = Double.parseDouble(singleStatInfo[1]);
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
        String autoCollectText = "", cashStoredText = "";
        if (autoCollect) {
            cashStoredText = "N/A";
            autoCollectText = "Auto-collect";
        } else {
            cashStoredText = "$" + cashStored;
        }
        String[] statsArray = {
            "<html>Cash stored: " + cashStoredText + "</html>",
            "Value: $" + super.getAction()*cashModifier,
            "Speed: " + (super.getSpeed()*super.getModifiers()[1]) + "s",
            "Range: " + String.valueOf(super.getRange()*super.getModifiers()[1]),
            "Cash Buff: " + ExtraMethods.round(100*(cashBuff-1),4) + "%",
            autoCollectText
        };
        return statsArray;
    }
    
    // Overriden addGraphics method
    @Override
    public void addGraphics(Graphics2D g) {
        super.addGraphics(g);
        BufferedImage sprite = ExtraMethods.loadImage(getClass().getResource("/img/generator_sprite_" + 
                super.getCurrentUpgrade() + ".png"));
        AffineTransform spriteTransform = new AffineTransform();
        spriteTransform.rotate(Math.toRadians(super.getAngle()),super.getDimensions().width/2,super.getDimensions().height/2);
        BufferedImage rotatedSprite = new BufferedImage(40,40,sprite.getType());
        rotatedSprite.createGraphics().drawImage(sprite,spriteTransform,null);
        
        // Green circle when cash collected
        if (super.getTowerTimer()[1] - super.getTowerTimer()[0] >= 
                super.getSpeed()*1000-250) {
            g.setColor(Color.GREEN);
            g.fillOval(super.getPosition().x-30,
                    super.getPosition().y-30,60,60);
        }
        
        g.drawImage(rotatedSprite, super.getScreenPos().x, super.getScreenPos().y,null);
    }
    
    
    // Static variables
    public static int getCashPool() {
        return cashPool;
    }
    
    public static void setCashPool(int c) {
        cashPool = c;
    }
    
}
