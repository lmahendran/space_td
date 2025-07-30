/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tower;

/** Tower class
 *
 * @author Logesh Mahendran
 */

// Imports
import balloon.*;
import ics3u_summative_tower_defense.*;
import projectile.*;
import java.awt.*;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Random;

public class Tower {
    
    // Instance variables
    private Point position, screenPos;
    private int targeting, targetEnemy, targetIndex, currentUpgrade,status;
    private double angle, prevAngle, speed, action, range, balloonDebuff, damageDealt;
    private boolean hiddenVision;
    private Clock towerClock;
    private Dimension dimensions;
    private ArrayList<Integer> balloonsInRange, towersInRange;
    private double[] towerBuffs;
    private double[] modifiers;
    private long[] towerTimer;
    private TowerRadius towerRadius;
    private Random random;
    public ArrayList<Projectile> projectiles;
    private TowerStats towerStats;
    
    
    // Static and final variables
    private static int numTowers;
    public static Tower[] towers;
    private static int selectedTower;
    private final int TYPE;
    private static final int[] COSTS = {
        100,150,175,175,200,500,450
    };
    
    // Constructor
    public Tower(double s, double a, double r, boolean h, 
            Dimension d, int t, Point p) {
        speed = s; action = a; range = r;
        hiddenVision = h;
        dimensions = d;
        TYPE = t;
        status = 0;
        position = p;
        screenPos = new Point(position.x - dimensions.width/2,position.y-dimensions.height/2);
        boolean showReds;
        if (TYPE >= 2 && TYPE <= 4) {
            showReds = false;
        } else {
            showReds =  true;
        }
        if (TYPE != 6) {
            towerRadius = new TowerRadius(range,position,showReds);
        } else {
            towerRadius = new TowerRadius(25,position,showReds);
        }
        balloonsInRange = new ArrayList<>();
        towersInRange = new ArrayList<>();
        modifiers = new double[4];
        for (int i = 0; i < 4; i++) {
            modifiers[i] = 1;
        }
        targeting = 0;
        currentUpgrade = 0;
        targetEnemy = -1;
        targetIndex = -1;
        towerClock = Clock.systemDefaultZone();
        towerTimer = new long[2];
        towerTimer[0] = towerClock.millis();
        towerTimer[1] = towerClock.millis();
        random = new Random();
        projectiles = new ArrayList<>();
        towerStats = new TowerStats(TYPE);
        numTowers++;
    }
    
    // Getter methods
    public Point getPosition() {
        return position;
    }
    
    public Point getScreenPos() {
        return screenPos;
    }
    
    public Dimension getDimensions() {
        return dimensions;
    }
    
    public double getAngle() {
        return angle;
    }
    
    public double getPrevAngle() {
        return prevAngle;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public double getAction() {
        return action;
    }
    
    public double getRange() {
        return range;
    }
    
    public boolean getHiddenVision() {
        return hiddenVision;
    }
    
    public double getDamageDealt() {
        return damageDealt;
    }
    
    public double[] getTowerBuffs() {
        return towerBuffs;
    }
    
    public double getBalloonDebuff() {
        return balloonDebuff;
    }
    
    public double[] getModifiers() {
        return modifiers;
    }
    
    public ArrayList<Integer> getBalloonsInRange() {
        return balloonsInRange;
    }
    
    public ArrayList<Integer> getTowersInRange() {
        return towersInRange;
    }
    
    public int getStatus() {
        return status;
    }
    
    public int getTYPE() {
        return TYPE;
    }
    
    public Clock getTowerClock() {
        return towerClock;
    }
    
    public long[] getTowerTimer() {
        return towerTimer;
    }
    
    public int getCurrentUpgrade() {
        return currentUpgrade;
    }
    
    public int getTargeting() {
        return targeting;
    }
    
    public int getTargetEnemy() {
        return targetEnemy;
    }
    
    public int getTargetIndex() {
        return targetIndex;
    }
    
    public TowerStats getTowerStats() {
        return towerStats;
    }
    
    // Setter methods
    public void setPosition(Point p) {
        if (p != null) {
            if (p.x >= dimensions.width/2 && p.y >= dimensions.height/2 && 
                    p.x <= 800-dimensions.width/2 && p.y <= 600-dimensions.height/2) {
                position = p;
                screenPos = new Point(position.x - dimensions.width/2,position.y-dimensions.height/2);
            }
        }
    }
    
    public void setDimensions(Dimension d) {
        dimensions = d;
    }
    
    public void setAngle(double a) {
        angle = a;
    }
    
    public void setPrevAngle(double a) {
        prevAngle = a;
    }
    
    public void setSpeed(double s) {
        speed = s;
    }
    
    public void setAction(double a) {
        action = a;
    }
    
    public void setTowerBuffs(int t, double tB) {
        towerBuffs[t] = tB;
    }
    
    public void setBalloonDebuff(double bD) {
        balloonDebuff = bD;
    }
    
    public void setModifiers(int type, double amount) {
        modifiers[type] = amount;
    }
    
    public void setRange(double r) {
        range = r;
    }
    
    public void setHiddenVision(boolean h) {
        hiddenVision = h;
    }
    
    public void setStatus(int st) {
        status = st;
    }
    
    public void setTargeting(int t) {
        targeting = t;
    }
    
    public void setCurrentUpgrade(int u) {
        currentUpgrade = u;
    }
    
    public void setTowerTimer(int p, long t) {
        towerTimer[p] = t;
    }
    
    public void setTargetEnemy(int t) {
        targetEnemy = t;
    }
    
    public void setTargetIndex(int t) {
        targetIndex = t;
    }
    
    // Other instance methods
    public void changeDamageDealt(double c) {
        damageDealt += c;
    }
    
    // doAction method (overriden, but this section is called as well)
    public void doAction() {
        towerTimer[1] = towerClock.millis();
        updateItemsInRange();
        updateTargetEnemy();
    }
    
    // Updates towers and balloons in range for the tower
    public void updateItemsInRange() {
        // Clear previous lists
        towersInRange.clear();
        balloonsInRange.clear();
        // Towers in range
        for (int i = 0; i < numTowers; i++) {
            if (!towers[i].equals(this) && 
                    ExtraMethods.distance(this.position.x,this.position.y,
                    towers[i].position.x,towers[i].position.y) <= range) {
                towersInRange.add(i);
            }
        }
        // Balloons in range
        for (int i = 0; i < Balloon.balloons.size(); i++) {
            if (Balloon.balloons.get(i) != null) {
                // If full-map range, add all balloons
                if (range == -1) {
                    balloonsInRange.add(i);
                // If not, check if balloons are in range or not
                } else if (ExtraMethods.distance(this.position.x,this.position.y,
                        Balloon.balloons.get(i).getPosition()[0],
                        Balloon.balloons.get(i).getPosition()[1]) <= range*modifiers[1] &&
                        (Balloon.balloons.get(i).getEffect()%2 == 0 ||
                        Balloon.balloons.get(i).getEffect()%2 == 1 && hiddenVision)) {
                    
                    // Checking if the balloon is blocked by an obstacle
                    boolean obstacleBlocked = false;
                    double incrementX, incrementY, x, y;
                    x = this.position.x; y = this.position.y;
                    double theta = Math.atan2(Balloon.balloons.get(i).getPosition()[1] -
                            this.position.y, Balloon.balloons.get(i).getPosition()[0] -
                            this.position.x);
                    incrementX = Math.cos(theta);
                    incrementY = Math.sin(theta);
                    if (incrementX < 0) {
                        while (x > Balloon.balloons.get(i).getPosition()[0]) {
                            for (int j = 0; j < Obstacle.getNumObstacles(); j++) {
                                if (TowerRadius.checkObstacles(x, y)) {
                                    obstacleBlocked = true;
                                }
                            }
                            if (obstacleBlocked) {
                                break;
                            }
                            x += incrementX; y += incrementY;
                        }
                    } else if (incrementX > 0) {
                        while (x < Balloon.balloons.get(i).getPosition()[0]) {
                            for (int j = 0; j < Obstacle.getNumObstacles(); j++) {
                                if (TowerRadius.checkObstacles(x, y)) {
                                    obstacleBlocked = true;
                                }
                            }
                            if (obstacleBlocked) {
                                break;
                            }
                            x += incrementX; y += incrementY;
                        }
                    }
                    if (!obstacleBlocked) {
                        balloonsInRange.add(i);
                    }
                }
            }
        }
    }
    
    // Updates the target enemy BASED ON the selected targeting. -1 signifies no target
    public void updateTargetEnemy() {
        if (balloonsInRange.isEmpty()) {
            targetEnemy = -1;
        } else {
            if (targeting == 6) {
                targetEnemy = Balloon.balloons.get(balloonsInRange.get(random.nextInt(0,balloonsInRange.size()))).getBalloonID();
            } else {
                int smallest = 0, biggest = 0;
                // Search once through the list, and identify both the smallest and largest value depending on the selected quantity for targeting
                for (int i = 1; i < balloonsInRange.size(); i++) {
                    if (targeting == 0 || targeting == 1) {
                        if (Balloon.balloons.get(balloonsInRange.get(i)).getPathProgressAmount() >
                                Balloon.balloons.get(balloonsInRange.get(smallest)).getPathProgressAmount()) {
                            smallest = i;
                        }
                        if (Balloon.balloons.get(balloonsInRange.get(i)).getPathProgressAmount() <
                                Balloon.balloons.get(balloonsInRange.get(biggest)).getPathProgressAmount()) {
                            biggest = i;
                        }
                    } else if (targeting == 2 || targeting == 3) {
                        if (Balloon.balloons.get(balloonsInRange.get(i)).getHealth() <
                                Balloon.balloons.get(balloonsInRange.get(smallest)).getHealth()) {
                            smallest = i;
                        }
                        if (Balloon.balloons.get(balloonsInRange.get(i)).getHealth() >
                                Balloon.balloons.get(balloonsInRange.get(biggest)).getHealth()) {
                            biggest = i;
                        }
                    } else {
                        if (ExtraMethods.distance(position.x,position.y,
                                Balloon.balloons.get(balloonsInRange.get(i)).getPosition()[0],
                                Balloon.balloons.get(balloonsInRange.get(i)).getPosition()[1]) <
                                ExtraMethods.distance(position.x,position.y,
                                Balloon.balloons.get(balloonsInRange.get(smallest)).getPosition()[0],
                                Balloon.balloons.get(balloonsInRange.get(smallest)).getPosition()[1])) {
                            smallest = i;
                        }
                        if (ExtraMethods.distance(position.x,position.y,
                                Balloon.balloons.get(balloonsInRange.get(i)).getPosition()[0],
                                Balloon.balloons.get(balloonsInRange.get(i)).getPosition()[1]) >
                                ExtraMethods.distance(position.x,position.y,
                                Balloon.balloons.get(balloonsInRange.get(biggest)).getPosition()[0],
                                Balloon.balloons.get(balloonsInRange.get(biggest)).getPosition()[1])) {
                            biggest = i;
                        }
                    }
                }
                if (targeting % 2 == 0) {
                    targetIndex = balloonsInRange.get(smallest);
                } else {
                    targetIndex = balloonsInRange.get(biggest);
                }
                targetEnemy = Balloon.balloons.get(targetIndex).getBalloonID();
                targetIndex = -1;
            }
        }
    }

    // Update tower radius with its position and its range
    public void updateRadius() {
        if (TYPE != 6) {
            towerRadius.setRadius(range*modifiers[1]);
        } else {
            towerRadius.setRadius(25);
        }
        towerRadius.setPosition(position);
        towerRadius.update();
        
    }
    
    // Update towerStats (to be overriden)
    public void updateTowerStats(int userCash) {

    }
    
    // Freeze timers
    public void freezeTimers() {
        long delay = towerTimer[1] - towerTimer[0];
        towerTimer[1] = towerClock.millis();
        towerTimer[0] = towerTimer[1] - delay;
        
    }
    
    // Stats to array (to be overriden)
    public String[] statsToArray() {
        return null;
    }
    
    // addGraphics (to be overriden)
    public void addGraphics(Graphics2D g) {
        
    }
    
    // Draw radius method
    public void drawRadius(Graphics2D g,Path p) {
        if (position != null) {
            towerRadius.addGraphics(g,checkValidPlacement(p));
        }
    }
    
    // Returns true if its placement on the map is completely valid (not on any path, any obstacle, or any other tower)
    public boolean checkValidPlacement(Path p) {
        double x = p.getKEYPOINTS()[0][0];
        double y = p.getKEYPOINTS()[0][1];
        double drawDirection = 1;
        int drawProgress = 0;
        while (drawProgress < p.getNUMSECTIONS()) {
            boolean foundPoint = false;
            double increment = 0.01*drawDirection;
            while (!foundPoint) {
                double iy = p.evaluateFunction(x+increment,drawProgress);
                if (Math.sqrt(Math.pow(increment, 2) + Math.pow(iy-y, 2)) >= 1) {
                    foundPoint = true;
                    x += increment;
                    y = iy;
                    if (ExtraMethods.distance(position.x,position.y,x,y) < 8) {
                        return true;
                    }
                    drawProgress = p.updatePathProgress(x,drawProgress);
                    drawDirection = p.updateDirection(drawProgress);
                }
                increment+=0.01*drawDirection;
            }
        }
        for (int i = 0; i < numTowers; i++) {
            if (i != selectedTower && 
                    ExtraMethods.distance(this.position.x,this.position.y,
                    towers[i].position.x,towers[i].position.y) <= 20) {
                return true;
            }
        }
        for (int i = 0; i < Obstacle.getNumObstacles(); i++) {
            if (Obstacle.obstacles[i].getSHAPE() == 0 && 
                    position.x >= Obstacle.obstacles[i].getBOUNDS().x 
                    && position.x <= Obstacle.obstacles[i].getBOUNDS().x + 
                    Obstacle.obstacles[i].getBOUNDS().width 
                    && position.y >= Obstacle.obstacles[i].getBOUNDS().y 
                    && position.y <= Obstacle.obstacles[i].getBOUNDS().y + 
                    Obstacle.obstacles[i].getBOUNDS().height) { 
                return true;
            } else if (Obstacle.obstacles[i].getSHAPE() == 1) {
                int obsRadius = Obstacle.obstacles[i].getBOUNDS().width/2;
                if (ExtraMethods.distance(Obstacle.obstacles[i].getBOUNDS().x + obsRadius,
                        Obstacle.obstacles[i].getBOUNDS().y + obsRadius, position.x, position.y) <= obsRadius) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Returns a string with the tower's information in CSV format, to be used by save files
    public String toCSV() {
        // Last three spots hold 1 - the planes path in the air, 2 - minigunners target x, 3 - minigunners target y
        String[] csvArr = {
            String.valueOf(TYPE),
            String.valueOf(position.x),
            String.valueOf(position.y),
            String.valueOf(angle),
            String.valueOf(targeting),
            String.valueOf(damageDealt),
            String.valueOf(currentUpgrade),
            "N","N","N"
        };
        if (TYPE == 5) {
            TowerFlyer tempFlyer = (TowerFlyer)this;
            csvArr[7] = String.valueOf(tempFlyer.getPathing());
        } else if (TYPE == 6) {
            TowerMinigunner tempMini = (TowerMinigunner)this;
            csvArr[8] = String.valueOf(tempMini.getTargetPoint().x);
            csvArr[9] = String.valueOf(tempMini.getTargetPoint().y);
        }
        String csvText = "";
        for (int i = 0; i < 10; i++) {
            csvText += csvArr[i];
            if (i < 9) {
                csvText += ":";
            }
        }
        return csvText;
    }
    
    // Interprets informatino from a set of CSV data for this tower, then casts that information accordingly
    public void interpretCSV(String csvArr,int userCash) {
        // Get information
        String[] towerInfo = csvArr.split(":");
        int targetType = Integer.parseInt(towerInfo[0]);
        setPosition(new Point(Integer.parseInt(towerInfo[1]),Integer.parseInt(towerInfo[2])));
        angle = Double.parseDouble(towerInfo[3]);
        targeting = Integer.parseInt(towerInfo[4]);
        damageDealt = Double.parseDouble(towerInfo[5]);
        int targetUpgrade = Integer.parseInt(towerInfo[6]);
        int targetPathing = -1;
        Point targetPoint = new Point(-1,-1);
        if (targetType == 5) {
            targetPathing = Integer.parseInt(towerInfo[7]);
        } else if (targetType == 6) {
            targetPoint = new Point(Integer.parseInt(towerInfo[8]),Integer.parseInt(towerInfo[9]));
        }
        // Load all upgrades (must be done one at a time so every aspect is saved)
        for (int i = 1; i <= targetUpgrade; i++) {
            towerStats.setCurrentUpgrade(i);
            towerStats.setSellPrice(towerStats.getSellPrice() + (int)ExtraMethods.round(towerStats.getNextUpgradeCost()*0.25,0));
            this.updateTowerStats(userCash);
        }
        
        // Tower specific variables
        if (targetType == 5) {
            TowerFlyer tempFlyer = (TowerFlyer)this;
            tempFlyer.setPathing(targetPathing);
            towerStats.setPlanePath(targetPathing);
        } else if (targetType == 6) {
            TowerMinigunner tempMini = (TowerMinigunner)this;
            tempMini.setTargetPoint(targetPoint);
        }
        
    }
    
    // Static methods
    public static int getNumTowers() {
        return numTowers;
    }
    
    public static int getSelectedTower() {
        return selectedTower;
    }
    
    public static int[] getCOSTS() {
        return COSTS;
    }
    
    public static void setNumTowers(int n) {
        numTowers = n;
    }
    
    public static void setSelectedTower(int s) {
        selectedTower = s;
    }
    
}
