/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package balloon;

/** Boss balloon - subclass of Balloon
 *
 * @author Logesh Mahendran
 * @courtesy WallpaperSafari for hidden overlay
 */

// Imports
import ics3u_summative_tower_defense.*;
import java.awt.*;

public class BalloonBoss extends Balloon {
    
    // Constructor
    public BalloonBoss(int e, int oSt, Path p) {
        super(0.2,150*WaveManager.getWaveHealthBuff(),e,4,oSt,200,new Dimension(30,30),p);
    }
    
    // Overriden addGraphics method
    @Override
    public void addGraphics(Graphics2D g) {
        g.drawImage(ExtraMethods.loadImage(getClass().getResource("/img/balloon_boss_sprite.png")),
                super.getScreenPos().x,super.getScreenPos().y,null);
        if (super.getEffect() == 1 || super.getEffect() == 3) {
            g.drawImage(ExtraMethods.loadImage(getClass().getResource("/img/balloon_camo.png")),
                    super.getScreenPos().x,super.getScreenPos().y,null);
        }
        if (super.getEffect() == 2 || super.getEffect() == 3) {
            g.drawImage(ExtraMethods.loadImage(getClass().getResource("/img/balloon_regrow.png")),
                    super.getScreenPos().x,super.getScreenPos().y,null);
        }
    }
    
}
