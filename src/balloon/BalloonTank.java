/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package balloon;

/** Tank balloon class
 *
 * @author Logesh Mahendran
 * @courtesy WallpaperSafari for camo overlay
 */

// Imports
import ics3u_summative_tower_defense.*;
import java.awt.*;

public class BalloonTank extends Balloon {
    
    // Constructor
    public BalloonTank(int e, int oSt, Path p) {
        super(0.1,600*WaveManager.getWaveHealthBuff(),e,5,oSt,200,new Dimension(60,60),p);
    }
    
    // Overriden addGraphics method
    @Override
    public void addGraphics(Graphics2D g) {
        g.drawImage(ExtraMethods.loadImage(getClass().getResource("/img/balloon_tank_sprite.png")),
                super.getScreenPos().x,super.getScreenPos().y,null);
        if (super.getEffect() == 1 || super.getEffect() == 3) {
            g.drawImage(ExtraMethods.loadImage(getClass().getResource("/img/balloon_camo.png"))
                    .getScaledInstance(60,60,0),
                    super.getScreenPos().x,super.getScreenPos().y,null);
        }
        if (super.getEffect() == 2 || super.getEffect() == 3) {
            g.drawImage(ExtraMethods.loadImage(getClass().getResource("/img/balloon_regrow.png"))
                    .getScaledInstance(60,60,0),
                    super.getScreenPos().x,super.getScreenPos().y,null);
        }
    }
    
}
