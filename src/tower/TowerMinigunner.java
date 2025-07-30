/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tower;

/** Minigunner tower class
 *
 * @author Logesh Mahendran
 */

// Imports
import ics3u_summative_tower_defense.*;
import balloon.*;
import projectile.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Collections;
import java.time.Clock;

public class TowerMinigunner extends Tower {
    
    // Instance variables
    private double angleOffset;
    private boolean setTargeting,rayUpgrade;
    private Random random;
    private Clock rayClock;
    private long[] rayTimer;
    private int rayInput;
    private Point targetPoint;
    private int[] edgePos;
    
    // Constructor
    public TowerMinigunner(Point p) {
        super(0.3,1,-1,false,new Dimension(40,40),6,p);
        angleOffset = 15; setTargeting = false; rayUpgrade = false;
        random = new Random();
        rayClock = Clock.systemDefaultZone();
        rayTimer = new long[2];
        rayTimer[0] = rayClock.millis();
        rayInput = 0;
        targetPoint = new Point(0,0);
        edgePos = new int[2];
    }
    
    // Getters
    public Point getTargetPoint() {
        return targetPoint;
    }
    
    // Setters
    public void setTargetPoint(Point t) {
        targetPoint = t;
    }
    
    // Override so that there is no target
    @Override
    public void updateTargetEnemy() {
        super.setTargetEnemy(-1);
        super.setTargetIndex(-1);
    }
    
    // Freeze ability timer
    @Override
    public void freezeTimers() {
        super.freezeTimers();
        long delay = rayTimer[1] - rayTimer[0];
        rayTimer[0] = super.getTowerClock().millis();
        rayTimer[1] = rayTimer[0] + delay;
    }
    
    // Overriden doAction method
    @Override
    public void doAction() {
        super.doAction();
        
        // Update angle(if there is set targeting)
        if (super.getCurrentUpgrade() >= 2) {
            super.setAngle(180+ Math.toDegrees(Math.atan2(
                    super.getPosition().y-targetPoint.y,
                    super.getPosition().x-targetPoint.x)));
        }
        
        // Update ray timer (for up6grade 4)
        if (rayUpgrade) {
            rayTimer[1] = rayClock.millis();
            if (rayTimer[1] - rayTimer[0] >= 1000*super.getSpeed()*super.getModifiers()[0]) {
                // Update function input for ray animation
                rayInput++;
                if (rayInput > 11) {
                    rayInput = 0;
                }
                
                // Update ray
                int direction;
                double slope = Math.tan(Math.toRadians(super.getAngle()));
                double intercept = super.getPosition().y-slope*super.getPosition().x;
                if (Math.cos(Math.toRadians(super.getAngle())) < 0) {
                    direction = -1;
                } else if (Math.cos(Math.toRadians(super.getAngle())) > 0) {
                    direction = 1;
                } else {
                    direction = 0;
                }
                if (Math.abs(90-Math.abs(super.getAngle())) <= 1) {
                    direction = 0;
                }
                // Simply put, based on the angle, it runs through a bunch of points until it either reaches and obstacle or reaches one end of the screen.
                // While doing so, it checks if any balloons are close enough to the ray, and deals damage accordingly.
                if (direction == -1) {
                    edgePos[0] = -40;
                    edgePos[1] = (int)(slope*edgePos[0] + intercept);
                    for (int i = super.getPosition().x; i > -40; i--) {
                        int x = i;
                        int y = (int)(slope*x + intercept);
                        for (int j = 0; j < Balloon.balloons.size(); j++) {
                            if (ExtraMethods.distance(x,y,Balloon.balloons.get(j).getPosition()[0],
                                    Balloon.balloons.get(j).getPosition()[1]) <= 20) {
                                Balloon.balloons.get(j).changeHealth(-1*super.getAction()*super.getModifiers()[3]);
                            }
                        }
                        if (y < -40 || y > 640) {
                            edgePos[0] = x;
                            if (y > 0) {
                                edgePos[1] = 640;
                            } else {
                                edgePos[1] = -40;
                            }
                            break;
                        } else {
                            boolean obstacleBlocked = false;
                            for (int j = 0; j < Obstacle.getNumObstacles(); j++) {
                                if (x >= Obstacle.obstacles[j].getBOUNDS().x &&
                                        x <= Obstacle.obstacles[j].getBOUNDS().x + 
                                        Obstacle.obstacles[j].getBOUNDS().width &&
                                        y >= Obstacle.obstacles[j].getBOUNDS().y &&
                                        y <= Obstacle.obstacles[j].getBOUNDS().y + 
                                        Obstacle.obstacles[j].getBOUNDS().height) {
                                    edgePos[0] = x;
                                    edgePos[1] = y;
                                    obstacleBlocked = true;
                                    break;
                                }
                            }
                            if (obstacleBlocked) {
                                break;
                            }
                        }
                    }
                } else if (direction == 1) {
                    edgePos[0] = 840;
                    edgePos[1] = (int)(slope*edgePos[0] + intercept);
                    for (int i = super.getPosition().x; i < 840; i++) {
                        int x = i;
                        int y = (int)(slope*x + intercept);
                        for (int j = 0; j < Balloon.balloons.size(); j++) {
                            if (ExtraMethods.distance(x,y,Balloon.balloons.get(j).getPosition()[0],
                                    Balloon.balloons.get(j).getPosition()[1]) <= 20) {
                                Balloon.balloons.get(j).changeHealth(-1*super.getAction());
                            }
                        }
                        if (y < -40 || y > 640) {
                            edgePos[0] = x;
                            if (y > 0) {
                                edgePos[1] = 640;
                            } else {
                                edgePos[1] = -40;
                            }
                            break;
                        } else {
                            boolean obstacleBlocked = false;
                            for (int j = 0; j < Obstacle.getNumObstacles(); j++) {
                                if (x >= Obstacle.obstacles[j].getBOUNDS().x &&
                                        x <= Obstacle.obstacles[j].getBOUNDS().x + 
                                        Obstacle.obstacles[j].getBOUNDS().width &&
                                        y >= Obstacle.obstacles[j].getBOUNDS().y &&
                                        y <= Obstacle.obstacles[j].getBOUNDS().y + 
                                        Obstacle.obstacles[j].getBOUNDS().height) {
                                    edgePos[0] = x;
                                    edgePos[1] = y;
                                    obstacleBlocked = true;
                                    break;
                                }
                            }
                            if (obstacleBlocked) {
                                break;
                            }
                        }
                    }
                } else {
                    int vertDirection;
                    if (super.getAngle() < 0) {
                        vertDirection = -1;
                    } else {
                        vertDirection = 1;
                    }
                    if (vertDirection == 1) {
                        edgePos[0] = super.getPosition().x;
                        edgePos[1] = 640;
                        for (int i = super.getPosition().y; i < 640; i++) {
                            int x = super.getPosition().x;
                            int y = i;
                            for (int j = 0; j < Balloon.balloons.size(); j++) {
                                if (ExtraMethods.distance(x,y,Balloon.balloons.get(j).getPosition()[0],
                                        Balloon.balloons.get(j).getPosition()[1]) <= 20) {
                                    Balloon.balloons.get(j).changeHealth(-1*super.getAction());
                                }
                            }
                            boolean obstacleBlocked = false;
                            for (int j = 0; j < Obstacle.getNumObstacles(); j++) {
                                if (x >= Obstacle.obstacles[j].getBOUNDS().x &&
                                        x <= Obstacle.obstacles[j].getBOUNDS().x + 
                                        Obstacle.obstacles[j].getBOUNDS().width &&
                                        y >= Obstacle.obstacles[j].getBOUNDS().y &&
                                        y <= Obstacle.obstacles[j].getBOUNDS().y + 
                                        Obstacle.obstacles[j].getBOUNDS().height) {
                                    edgePos[0] = x;
                                    edgePos[1] = y;
                                    obstacleBlocked = true;
                                    break;
                                }
                            }
                            if (obstacleBlocked) {
                                break;
                            }
                        }
                    } else if (vertDirection == -1) {
                        edgePos[0] = super.getPosition().x;
                        edgePos[1] = -40;
                        for (int i = super.getPosition().y; i > -40; i--) {
                            int x = super.getPosition().x;
                            int y = i;
                            for (int j = 0; j < Balloon.balloons.size(); j++) {
                                if (ExtraMethods.distance(x,y,Balloon.balloons.get(j).getPosition()[0],
                                        Balloon.balloons.get(j).getPosition()[1]) <= 20) {
                                    Balloon.balloons.get(j).changeHealth(-1*super.getAction());
                                }
                            }
                            boolean obstacleBlocked = false;
                            for (int j = 0; j < Obstacle.getNumObstacles(); j++) {
                                if (x >= Obstacle.obstacles[j].getBOUNDS().x &&
                                        x <= Obstacle.obstacles[j].getBOUNDS().x + 
                                        Obstacle.obstacles[j].getBOUNDS().width &&
                                        y >= Obstacle.obstacles[j].getBOUNDS().y &&
                                        y <= Obstacle.obstacles[j].getBOUNDS().y + 
                                        Obstacle.obstacles[j].getBOUNDS().height) {
                                    edgePos[0] = x;
                                    edgePos[1] = y;
                                    obstacleBlocked = true;
                                    break;
                                }
                            }
                            if (obstacleBlocked) {
                                break;
                            }
                        }
                    }
                }
                
                // Deal damage to balloons

                
                
                rayTimer[0] = rayClock.millis();
            }
        } else {
            if (super.getTowerTimer()[1] - super.getTowerTimer()[0] >= 
                    super.getSpeed()*super.getModifiers()[0]*1000) {

                // Make spontaneous angle
                double randomAngle = super.getAngle() + 
                        random.nextDouble(-1*angleOffset,angleOffset+1);

                // Make projectiles
                double bulletSpeed = 2;
                if (super.getCurrentUpgrade() == 4) {
                    bulletSpeed = 4;
                } else if (super.getCurrentUpgrade() == 3) {
                    bulletSpeed = 6;
                }
                double[] positionArray = {
                    super.getPosition().x,super.getPosition().y
                };
                super.projectiles.add(new ProjectileMinigunner(positionArray,
                        randomAngle,bulletSpeed,
                        super.getTargetIndex(),super.getRange()*super.getModifiers()[1],
                        super.getCurrentUpgrade()));
                super.setTowerTimer(0,super.getTowerClock().millis());
            }
        }

        // Update projectiles
        for (int i = 0; i < super.projectiles.size(); i++) {
            if (super.projectiles.get(i) != null) {
                super.projectiles.get(i).doAction(super.getCurrentUpgrade());
                
                // Check obstacles (minigunner specific)
                boolean hitObstacle = false;
                for (int j = 0; j < Obstacle.getNumObstacles(); j++) {
                    if (super.projectiles.get(i).getPosition()[0] >= Obstacle.obstacles[j].getBOUNDS().x &&
                            super.projectiles.get(i).getPosition()[0] <= Obstacle.obstacles[j].getBOUNDS().x + 
                            Obstacle.obstacles[j].getBOUNDS().width &&
                            super.projectiles.get(i).getPosition()[1] >= Obstacle.obstacles[j].getBOUNDS().y &&
                            super.projectiles.get(i).getPosition()[1] <= Obstacle.obstacles[j].getBOUNDS().y + 
                            Obstacle.obstacles[j].getBOUNDS().height) {
                        hitObstacle = true;
                        break;
                    }
                }
                
                if (super.projectiles.get(i).getHitEnemy() ||
                        super.projectiles.get(i).reachedEdge() ||
                        hitObstacle) {
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
                } else if (singleStatInfo[0].equals("aO")) {
                    angleOffset = Integer.parseInt(singleStatInfo[1]);
                } else if (singleStatInfo[0].equals("sT")) {
                    setTargeting = (Integer.parseInt(singleStatInfo[1]) == 1);
                } else if (singleStatInfo[0].equals("rY")) {
                    rayUpgrade = (Integer.parseInt(singleStatInfo[1]) == 1);
                } else if (singleStatInfo[0].equals("a")) {
                    super.setAction(Double.parseDouble(singleStatInfo[1]));
                }
            }
            if (super.getCurrentUpgrade() == 2) {
                targetPoint = new Point((int)(super.getPosition().x+10*Math.cos(Math.toRadians(super.getAngle()))),
                    (int)(super.getPosition().y+10*Math.sin(Math.toRadians(super.getAngle()))));
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
            "Inaccuracy: " + angleOffset + "°",
            "Range: Infinite"
        };
        return statsArray;
    }
    
    // Draw ray method (uses the same idea as doing action with the ray, but then uses the midpoint and an AffineTransform to rotate the actual ray sprite)
    // Also, uses an absolute value function to calculate exactly what stage of the ray to display
    public void drawRay(Graphics2D g) {
        // Ray sprite
        if (rayUpgrade) {
            int rayValue = -1*Math.abs(rayInput-6)+6;
            BufferedImage raySprite = ExtraMethods.loadImage(getClass().getResource("/img/ray_" + rayValue + ".png"));
            int[] edgePos = new int[2];
            int direction;
            double slope = Math.tan(Math.toRadians(super.getAngle()));
            double intercept = super.getPosition().y-slope*super.getPosition().x;
            if (Math.cos(Math.toRadians(super.getAngle())) < 0) {
                direction = -1;
            } else if (Math.cos(Math.toRadians(super.getAngle())) > 0) {
                direction = 1;
            } else {
                direction = 0;
            }
            if (Math.abs(90-Math.abs(super.getAngle())) <= 1) {
                direction = 0;
            }
            if (direction == -1) {
                edgePos[0] = -40;
                edgePos[1] = (int)(slope*edgePos[0] + intercept);
                for (int i = super.getPosition().x; i > -40; i--) {
                    int x = i;
                    int y = (int)(slope*x + intercept);
                    if (y < -40 || y > 640) {
                        edgePos[0] = x;
                        if (y > 0) {
                            edgePos[1] = 640;
                        } else {
                            edgePos[1] = -40;
                        }
                        break;
                    } else {
                        boolean obstacleBlocked = false;
                        for (int j = 0; j < Obstacle.getNumObstacles(); j++) {
                            if (x >= Obstacle.obstacles[j].getBOUNDS().x &&
                                    x <= Obstacle.obstacles[j].getBOUNDS().x + 
                                    Obstacle.obstacles[j].getBOUNDS().width &&
                                    y >= Obstacle.obstacles[j].getBOUNDS().y &&
                                    y <= Obstacle.obstacles[j].getBOUNDS().y + 
                                    Obstacle.obstacles[j].getBOUNDS().height) {
                                edgePos[0] = x;
                                edgePos[1] = y;
                                obstacleBlocked = true;
                                break;
                            }
                        }
                        if (obstacleBlocked) {
                            break;
                        }
                    }
                }
            } else if (direction == 1) {
                edgePos[0] = 840;
                edgePos[1] = (int)(slope*edgePos[0] + intercept);
                for (int i = super.getPosition().x; i < 840; i++) {
                    int x = i;
                    int y = (int)(slope*x + intercept);
                    if (y < -40 || y > 640) {
                        edgePos[0] = x;
                        if (y > 0) {
                            edgePos[1] = 640;
                        } else {
                            edgePos[1] = -40;
                        }
                        break;
                    } else {
                        boolean obstacleBlocked = false;
                        for (int j = 0; j < Obstacle.getNumObstacles(); j++) {
                            if (x >= Obstacle.obstacles[j].getBOUNDS().x &&
                                    x <= Obstacle.obstacles[j].getBOUNDS().x + 
                                    Obstacle.obstacles[j].getBOUNDS().width &&
                                    y >= Obstacle.obstacles[j].getBOUNDS().y &&
                                    y <= Obstacle.obstacles[j].getBOUNDS().y + 
                                    Obstacle.obstacles[j].getBOUNDS().height) {
                                edgePos[0] = x;
                                edgePos[1] = y;
                                obstacleBlocked = true;
                                break;
                            }
                        }
                        if (obstacleBlocked) {
                            break;
                        }
                    }
                }
            } else {
                int vertDirection;
                if (super.getAngle() < 0) {
                    vertDirection = -1;
                } else {
                    vertDirection = 1;
                }
                if (vertDirection == 1) {
                    edgePos[0] = super.getPosition().x;
                    edgePos[1] = 640;
                    for (int i = super.getPosition().y; i < 640; i++) {
                        int x = super.getPosition().x;
                        int y = i;
                        boolean obstacleBlocked = false;
                        for (int j = 0; j < Obstacle.getNumObstacles(); j++) {
                            if (x >= Obstacle.obstacles[j].getBOUNDS().x &&
                                    x <= Obstacle.obstacles[j].getBOUNDS().x + 
                                    Obstacle.obstacles[j].getBOUNDS().width &&
                                    y >= Obstacle.obstacles[j].getBOUNDS().y &&
                                    y <= Obstacle.obstacles[j].getBOUNDS().y + 
                                    Obstacle.obstacles[j].getBOUNDS().height) {
                                edgePos[0] = x;
                                edgePos[1] = y;
                                obstacleBlocked = true;
                                break;
                            }
                        }
                        if (obstacleBlocked) {
                            break;
                        }
                    }
                } else if (vertDirection == -1) {
                    edgePos[0] = super.getPosition().x;
                    edgePos[1] = -40;
                    for (int i = super.getPosition().y; i > -40; i--) {
                        int x = super.getPosition().x;
                        int y = i;
                        boolean obstacleBlocked = false;
                        for (int j = 0; j < Obstacle.getNumObstacles(); j++) {
                            if (x >= Obstacle.obstacles[j].getBOUNDS().x &&
                                    x <= Obstacle.obstacles[j].getBOUNDS().x + 
                                    Obstacle.obstacles[j].getBOUNDS().width &&
                                    y >= Obstacle.obstacles[j].getBOUNDS().y &&
                                    y <= Obstacle.obstacles[j].getBOUNDS().y + 
                                    Obstacle.obstacles[j].getBOUNDS().height) {
                                edgePos[0] = x;
                                edgePos[1] = y;
                                obstacleBlocked = true;
                                break;
                            }
                        }
                        if (obstacleBlocked) {
                            break;
                        }
                    }
                }
            }
            int rayWidth = (int)ExtraMethods.distance(super.getPosition().x,super.getPosition().y,edgePos[0],edgePos[1]);
            Point midpoint = new Point((int)(edgePos[0]+super.getPosition().x)/2,
                    (int)(edgePos[1]+super.getPosition().y)/2);
            BufferedImage rayRescaled = new BufferedImage(rayWidth,60,raySprite.getType());
            rayRescaled.getGraphics().drawImage(raySprite.getScaledInstance(rayWidth,60,0),0,0,null);
            AffineTransform rayTransform = new AffineTransform();
            double radiansAngle = Math.toRadians(super.getAngle());
            int newWidth = (int) Math.abs(rayWidth * 
                    Math.cos(radiansAngle)) + (int) Math.abs(60 * Math.sin(radiansAngle));
            int newHeight = (int) Math.abs(60 * Math.cos(radiansAngle)) + 
                            (int) Math.abs(rayWidth * Math.sin(radiansAngle));
            rayTransform.rotate(radiansAngle,newWidth/2,newHeight/2);
            rayTransform.translate((newWidth-rayWidth)/2,
                    (newHeight-60)/2);
            BufferedImage rotatedRay = new BufferedImage(newWidth,newHeight,rayRescaled.getType());
            rotatedRay.createGraphics().drawImage(rayRescaled,rayTransform,null);
            g.drawImage(rotatedRay,(int)(midpoint.x-newWidth/2),
                    (int)(midpoint.y-newHeight*0.5),null);
        }
    }
    
    // Overriden addGraphics method
    @Override
    public void addGraphics(Graphics2D g) {
        super.addGraphics(g);

        // Tower sprite
        BufferedImage sprite = ExtraMethods.loadImage(getClass().getResource("/img/minigunner_sprite_" + 
                super.getCurrentUpgrade() + ".png"));
        AffineTransform spriteTransform = new AffineTransform();
        spriteTransform.rotate(Math.toRadians(super.getAngle()+180),super.getDimensions().width/2,super.getDimensions().height/2);
        BufferedImage rotatedSprite = new BufferedImage(40,40,sprite.getType());
        rotatedSprite.createGraphics().drawImage(sprite,spriteTransform,null);
        g.drawImage(rotatedSprite, super.getScreenPos().x, super.getScreenPos().y,null);
    }
    
}
