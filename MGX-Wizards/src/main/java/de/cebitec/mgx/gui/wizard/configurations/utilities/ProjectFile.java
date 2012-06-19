package de.cebitec.mgx.gui.wizard.configurations.utilities;


import java.io.File;
import java.io.IOException;



/**
 * Stellt eine Datei in dem Projektvezeichnis dar.
 * 
 *
 *@author belmann
 */
public class ProjectFile extends File {

    /**
     * Der Pfad der Datei im Projekt.
     */
   private String path;
   
   /**
    * Gibt an ob die Datei ein Verzeichnis oder eine Datei darstellt.
    */
   private boolean isFile;

   /**
    * Konstruktor fuer die Datei.
    * @param s Der Pfad zu der Datei.
    * @param isFile Gibt an, ob es sich um eine Datei handelt oder nicht.
    */
   public ProjectFile(String s, boolean isFile) {
	super(s);
	path = s;
	this.isFile = isFile;
   }

   /**
    * Gibt an, ob die Klasse ein Verzeichnis darstellt. 
    * @return Verzeichnis oder nicht.
    */
   @Override
   public boolean isDirectory() {
	return !isFile;
   }

   /**
    * Gibt an, ob die Klasse ein Datei darstellt.
    * @return Datei oder nicht.
    */
   @Override
   public boolean isFile() {
	return isFile;
   }

   /**
    * Gibt an, ob das Verzeichnis existiert.
    * @return existiert oder nicht.
    */
   @Override
   public boolean exists() {
	return true;
   }

   /**
    * Gibt den Pfad der Datei wieder.
    * @return Pfad.
    */
   @Override
   public String getCanonicalPath() {
	return path;
   }

   /**
    * Dateien sollen nicht beschreibbar sein.
    * @param writable
    * @return Beschreibbar oder nicht.
    */
   @Override
   public boolean setWritable(boolean writable) {
	return false;
   }

   /**
    * Die Dateien duerfen nicht umbenannt werden.
    * @param dest Ziel der Datei.
    * @return Darf umbenannt werden oder nicht.
    */
   @Override
   public boolean renameTo(File dest) {
	return false;
   }

   
   /**
    * Gibt den Pfad der Datei wieder.
    * 
    * @return Pfad der Datei.
    */
    @Override
    public String getAbsolutePath() {
        return path;
    }
   
   
    /**
     * 
     * Gibt diese Klasse wieder und nicht die Oberklasse.
     * @return ProjectFile
     * @throws IOException 
     */
   @Override
   public File getCanonicalFile() throws IOException {

	if (!isFile) {
	   return this;
	} else {
	   return super.getCanonicalFile();
	}
   }
}