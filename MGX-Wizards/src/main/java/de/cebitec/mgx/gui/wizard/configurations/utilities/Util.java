/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.configurations.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *Nuetzliche Methoden. 
 * 
 * @author pbelmann
 */
public class Util {

    /**
     * Prueft ob buchstaben in der Zeichenkette vorhanden sind.
     * @param lInput
     * @return vorhanden oder nicht.
     */
    public static boolean checkForLetters(String lInput) {
        String regex = "(^\\d+$)|(^\\d+\\.\\d+$)|(^\\d+\\.$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(lInput.trim());
        return matcher.find();
    }

    /**
     * 
     * Ueberprueft, ob die Zahl im Float Format vorliegt.
     * 
     * @param versionText
     * @return Float oder nicht.
     */
    public static boolean checkFloatFormat(String versionText) {
        try {
            if (!Util.checkForLetters(versionText)) {
                NumberFormatException exception = new NumberFormatException();
                throw exception;
            }
            Float.parseFloat(versionText.trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    
     /**
     * liest die XML Datei ein und gibt den Inhalt als String wieder.
     * @param path Pfad zu der Datei.
     * @return Datei als String
     */
    public static String readFile(String path) {

        // Erzeuge ein File-Objekt
        File file = new File(path);
        String content = "";
        try {
            FileReader fr = new FileReader(file);
            char[] temp = new char[(int) file.length()];
            fr.read(temp);
            content = new String(temp);
            fr.close();
        } catch (FileNotFoundException e1) {
            System.err.println("File not Found: "
                    + file);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return content;
    }
}
