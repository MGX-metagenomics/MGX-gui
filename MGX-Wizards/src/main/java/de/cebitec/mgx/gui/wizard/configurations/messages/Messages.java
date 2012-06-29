package de.cebitec.mgx.gui.wizard.configurations.messages;

/**
 *
 * In dieser Klasse sind sämtliche Nachrichten an den Benutzer enthalten.
 *
 *
 * @author belmann
 */
public class Messages {

    /**
     * ConfigByte.
     */
    public static final int Byte = 1;
    /**
     * ConfigDouble.
     */
    public static final int Double = 3;
    /**
     * ConfigEnumeration.
     */
    public static final int Enumeration = 9;
    /**
     * ConfigFile.
     */
    public static final int File = 6;
    /**
     * ConfigInteger.
     */
    public static final int Integer = 4;
    /**
     * ConfigLong.
     */
    public static final int Long = 2;
    /**
     * ConfigSByte.
     */
    public static final int SByte = 5;
    /**
     * ConfigSelection.
     */
    public static final int Selection = 8;
    /**
     * ConfigString.
     */
    public static final int String = 7;
    /**
     * ConfigULong.
     */
    public static final int ULong = 0;
    /**
     * ConfigBoolean.
     */
    public static final int Boolean = 10;
    /**
     * ConfigFill.
     */
    public static final int Fill = 11;
    public static final int ToolChoose = 12;
    public static final int ToolName = 13;
    public static final int ToolAuthor = 14;
    public static final int ToolDescription = 15;
    public static final int ToolVersion = 16;
    public static final int ToolNameExists = 17;

    /**
     * Gibt die an den Benutzer gerichtete Nachricht wieder.
     *
     * @param lType ConfigItem Typ
     * @return Nachricht
     */
    static public String getInformation(int lType) {

        switch (lType) {
            case ULong: {
                return "Enter an positive integer.";
            }
            case Byte: {
                return "Enter an integer between 0 and 255.";
            }
            case Long: {
                return "Enter an integer.";
            }
            case Double: {
                return "Enter a decimal number and use \".\" as decimal separator.";
            }
            case Integer: {
                return "Enter an integer between " + java.lang.Integer.MIN_VALUE + " and " + java.lang.Integer.MAX_VALUE + ".";
            }
            case SByte: {
                return "Enter an integer between −128 and 127.";
            }
            case File: {
                return "Click on \"Open\" and choose a file.";
            }
            case Enumeration: {
                return "Click on the box to choose an item.";
            }
            case String: {
                return "Enter any text.";
            }
            case ToolChoose: {
                return "Please choose a tool.";
            }
            case ToolName: {
                return "Please fill out name field";
            }
            case ToolAuthor: {
                return "Please fill out author field";
            }
            case ToolDescription: {
                return "Please fill out description field";
            }
            case ToolVersion: {
                return "Please enter a "
                        + "decimal number and use \".\" as decimal separator.";
            }
            case ToolNameExists: {
                return "The tool name already exists.";
            }
            default: {
                return "";
            }
        }
    }

    /**
     * Gibt die an den Benutzer gerichtete Warnung wieder.
     *
     * @param lType Typ des Configs
     * @param lconfigNumber Die Nummer des ConfigItems
     * @param lNodeNumber Die Nummer des Nodes.
     * @return Warnung
     */
    static public String getWarning(int lType, String lconfigNumber, String lNodeNumber) {
        double f = 4.;
        switch (lType) {
            case ULong:
                return "Please enter in field number " + lNodeNumber + "." + lconfigNumber
                        + " only a whole number between 0 and <br>18.446.744.073.709.551.615.";
            case Byte:
                return "Please enter in field number " + lNodeNumber + "." + lconfigNumber
                        + " only a whole number between 0 and 255.";
            case Long:
                return "Please enter in field number " + lNodeNumber + "." + lconfigNumber
                        + " only a whole number between −9.223.372.036.854.775.808 and <br>9.223.372.036.854.775.807.";
            case Double:
                return "Please enter in field number " + lNodeNumber + "." + lconfigNumber
                        + " a decimal number between <br>- 1.79769313486231570 *10<sup>308</sup> and 1.79769313486231570*10<sup>308</sup>. "
                        + "<br>Use \".\" as decimal separator.";
            case Integer:
                return "Please enter in field number " + lNodeNumber + "." + lconfigNumber
                        + " only a whole number between " + java.lang.Integer.MIN_VALUE + " and <br>" + java.lang.Integer.MAX_VALUE + ".";
            case SByte:
                return "Please enter in field number " + lNodeNumber + "." + lconfigNumber
                        + " only a whole number between −128 and 127.";
            case Enumeration:
                return "Please select an item in field number " + lNodeNumber + "." + lconfigNumber
                        + ".";
            case Selection:
                return "Please select an item in field number " + lNodeNumber + "." + lconfigNumber
                        + ".";
            case File:
                return "Please select a file in field number " + lNodeNumber + "." + lconfigNumber
                        + ".";
            case Fill:
                return "Please fill out the field number "
                        + lNodeNumber + "." + lconfigNumber + ".";
            case Boolean:
                return "Please select \"Yes\" or \"No\" in field number "
                        + lNodeNumber + "." + lconfigNumber + ".";
            default:
                return "";
        }
    }
}
