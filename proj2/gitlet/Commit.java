package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.TreeMap;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /* INSTANCE VARIABLES */
    /** The message of this Commit. */
    private String message;
    /** The date of this Commit. */
    private Date date;

    /** The hash value of the commit */
    private String hash;

    /** The hash value of the parent commit */
    private String parentHash;

    /** a map from filenames to hash strings
     * always have the same order compared to HashMap */
    private TreeMap<String, String> fileMap;

    /* TODO: fill in the rest of this class. */


    /* FUCNTIONS */
    public Commit() {
        this.message = "";
        // this.date = 0;
    }

    public Commit(String message, Date date, String parent) {
        this.message = message;
        this.date = date;
        this.hash = null;
        this.parentHash = parent;
        this.fileMap = null;
    }

    public String getHash() {
        if (hash == null) {
            hash = computeHash(this);
        }
        return hash;
    }

    public String computeHash(Commit c) {
        return sha1(serialize(this));
    }

    public void writeToFile() {
        if (hash == null) {
            hash = computeHash(this);
        }
        String folderName = hash.substring(0, 2);
        String fileName = hash.substring(2);
        File outFile = join(Repository.COMMIT_FOLDER, folderName);

        outFile.mkdir();
        outFile = join(outFile, fileName);

        if (!outFile.exists()) {
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        writeObject(outFile, this);

        return;
    }

    public void readFromFile() {

        return;
    }

    public Commit getParent() {
        // load object based on parentHash
        return new Commit();
    }

    public static void main(String[] args) {
        File inFile = join(Repository.GITLET_DIR, "d1", "1830f1ddeff0950ab03ca22aa8c6d6436bce49");
        System.out.println(inFile);
        if (inFile.exists()) {
            Commit c = readObject(inFile, Commit.class);
            System.out.println(c.message);
            System.out.println(c.date);
        }
        return;
    }

}
