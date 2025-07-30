/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package balloon;

/** Coloured balloon class
 *
 * @author Logesh
 * @courtesy WallpaperSafari for camo overlay
 */

// Imports
import ics3u_summative_tower_defense.*;
import java.awt.*;

public class BalloonColoured extends Balloon {
    
    // Constructor
    public BalloonColoured(int colour, int e, int oSt, Path p) {
        super(1,colour+1,e,1,oSt,(colour+1)*3,new Dimension(10,10),p);
        double speed;
        switch (colour) {
            case 0 -> speed = 0.1;
            case 1 -> speed = 0.2;
            case 2 -> speed = 0.3;
            case 3 -> speed = 0.5;
            default -> speed = 1;
        }
        super.setSpeed(speed);
    }
    
    // Overriden addGraphics method
    @Override
    public void addGraphics(Graphics2D g) {
        Color balloonColour;
        if (super.getSpeed() == 0.1) {
            balloonColour = Color.RED;
        } else if (super.getSpeed() == 0.2) {
            balloonColour = Color.BLUE;
        } else if (super.getSpeed() == 0.3) {
            balloonColour = Color.GREEN;
        } else if (super.getSpeed() == 0.5) {
            balloonColour = Color.YELLOW;
        } else {
            balloonColour = Color.PINK;
        }
        g.setColor(balloonColour);
        g.fillOval(super.getScreenPos().x,super.getScreenPos().y,
                super.getDIMENSIONS().width,super.getDIMENSIONS().height);
        if (super.getEffect() == 1 || super.getEffect() == 3) {
            g.drawImage(ExtraMethods.loadImage(getClass().getResource("/img/balloon_camo.png"))
                    .getScaledInstance(10,10,0),
                    super.getScreenPos().x,super.getScreenPos().y,null);
        }
        if (super.getEffect() == 2 || super.getEffect() == 3) {
            g.drawImage(ExtraMethods.loadImage(getClass().getResource("/img/balloon_regrow.png"))
                    .getScaledInstance(10,10,0),
                    super.getScreenPos().x,super.getScreenPos().y,null);
        }
    }
    
}
