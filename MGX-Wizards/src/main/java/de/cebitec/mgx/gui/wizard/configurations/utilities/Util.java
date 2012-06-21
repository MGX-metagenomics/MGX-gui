package de.cebitec.mgx.gui.wizard.configurations.utilities;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.util.Exceptions;

/**
 * Nuetzliche Methoden.
 *
 * @author pbelmann
 */
public class Util {

    /**
     * Prueft ob buchstaben in der Zeichenkette vorhanden sind.
     *
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
            if (!checkForLetters(versionText)) {
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
     *
     * @param path Pfad zu der Datei.
     * @return Datei als String
     */
    public static String readFile(String path) throws IOException {
        StringBuilder content = new StringBuilder();

        try {
            FileInputStream fis = new FileInputStream(path);
            try (DataInputStream in = new DataInputStream(fis)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    content.append(strLine);
                }
            }
        } catch (IOException e) {
            throw e;
        }

        return content.toString();
    }
}
