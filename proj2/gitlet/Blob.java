package gitlet;

import java.io.Serializable;

public class Blob implements Serializable {

    private String folderName; // the first two digits of the hash
    private String fileName; // the remaining digits of the hash
    private String contents;

    public Blob(String folderName, String fileName) {
        this.folderName = folderName;
        this.fileName = fileName;
    }

    /**
     * return the file contents as a String from the path indicated by the arguments
     * @param folderName
     * @param fileName
     */
    public String readFromFile (String folderName, String fileName) {
        return "";
    }

    public void writeToFile (String folderName, String fileName, String contents) {
        return;
    }
}
