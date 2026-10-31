/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ics3u_summative_tower_defense;


/** Extra methods class
 *
 * @author Logesh Mahedran
 */

// Imports
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import javax.imageio.*;

public class ExtraMethods {
    
    // Round method
    public static double round(double number, int digits) {
        
        if (digits > 6) {
            return number;
        }
        else {
            double newNum;
            boolean roundUp;
            boolean isNegative;

            roundUp = false;
            isNegative = false;
            if (number < 0) {
                number = Math.abs(number);
                isNegative = true;
            }
            if (((int)(number*Math.pow(10,digits+1) % 10) >= 5)) {
                roundUp = true;
            }
            newNum = number*Math.pow(10,digits);
            newNum = (int)newNum;
            if (roundUp) {
                newNum += 1;
            }
            newNum = (double)(newNum/Math.pow(10,digits));
            if (isNegative) {
                newNum *= -1;
            }
            return newNum;
        }
    }
    
    // Read line method (reads through the URL's stream so it also works inside a jar)
    public static String readLine(URL filePath,int line) {
        String word = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(filePath.openStream()));
            for (int i = 0; i < line; i++) {
                word = br.readLine();
            }
            br.close();
            return word == null ? "" : word;
        } catch (Exception e) {
            return "";
        }

    }

    // Read line method for files on disk (save files)
    public static String readLine(File file,int line) {
        try {
            return readLine(file.toURI().toURL(),line);
        } catch (Exception e) {
            return "";
        }
    }

    // Save file location (saves live in the user's home folder, since the jar itself can't be written to)
    public static File getSaveFile(String gameCode) {
        File saveFolder = new File(System.getProperty("user.home"),".space_td" + File.separator + "saves");
        saveFolder.mkdirs();
        return new File(saveFolder,gameCode + ".txt");
    }

    // Write method
    public static void write(File file,String text,boolean append) {
        try {
            FileWriter fw = new FileWriter(file,append);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(text);
            pw.close();
        } catch (Exception e) {
        }
    }
    
    // Load an image method
    public static BufferedImage loadImage(URL filePath) {
        try {
            BufferedImage image = ImageIO.read(filePath);
            return image;
        } catch (Exception e) {}
        return null;
    }
    
    // Distance method
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
    }
    
}
