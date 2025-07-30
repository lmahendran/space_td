/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package balloon;

/** Galaxy Balloon class
 *
 * @author Logesh Mahendran
 * @courtesy WallpaperSafari for camo overlay
 */

// Imports
import ics3u_summative_tower_defense.*;
import java.awt.*;

public class BalloonGalaxy extends Balloon {
    
    // Constructor
    public BalloonGalaxy(int e, int oSt, Path p) {
        super(0.05,3000*WaveManager.getWaveHealthBuff(),e,6,oSt,200,new Dimension(120,120),p);
    }
    
    // Overriden addGraphics method
    @Override
    public void addGraphics(Graphics2D g) {
        g.drawImage(ExtraMethods.loadImage(getClass().getResource("/img/balloon_galaxy_sprite.png")),
                super.getScreenPos().x,super.getScreenPos().y,null);
    }
    
}
