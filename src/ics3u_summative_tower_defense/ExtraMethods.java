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
    
    // Read line method
    public static String readLine(URL filePath,int line) {
        String word = "";
        try {
            System.out.println(filePath.getPath());
            FileReader fr = new FileReader(filePath.getFile());
            BufferedReader br = new BufferedReader(fr);
            for (int i = 0; i < line; i++) {
                word = br.readLine();
            }
            br.close();
            return word;
        } catch (Exception e) {
            return "";
        }
        
    }
    
    // Write method
    public static void write(URL filePath,String text,boolean append) {
        try {
            File file = new File(filePath.getPath());
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
