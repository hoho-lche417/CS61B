package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  @author Hoho
 */
public class Commit implements Serializable {
    /**
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
    private String parentHash2; // for merging only

    /** a map from filenames to hash strings
     * always have the same order compared to HashMap */
    private TreeMap<String, String> fileMap;


    /* FUCNTIONS */
    public Commit() {
        this.message = "";
        // this.date = 0;
    }

    public Commit(String message, Date date, String parent, String parent2) {
        this.message = message;
        this.date = date;
        hash = null;
        parentHash = parent;
        parentHash2 = parent2;
        fileMap = new TreeMap<>();

        if (parent != null) {
            // inherit from parent's committed snapshot of files by default
            fileMap = getCommitFromHash(parent).getMapping();
            update();
        }

        record();
    }

    public String getHash() {
        if (hash == null) {
            hash = computeHash(this);
        }
        return hash;
    }

    public TreeMap<String, String> getMapping() {
        return fileMap;
    }

    public String computeHash(Commit c) {
        return sha1(serialize(this));
    }

    private void record() {
        if (hash == null) {
            hash = computeHash(this);
        }
        File outFile = createFilePath(Repository.COMMIT_DIR, hash, false);
        writeObject(outFile, this);

        return;
    }

    private void update() {
        for (Map.Entry<String, String> staged : StagingArea.stagedForAddition.entrySet()) {
            // track new files and update files based on staged area for adding
            fileMap.put(staged.getKey(), staged.getValue());
        }

        for (Map.Entry<String, String> staged : StagingArea.stagedForRemoval.entrySet()) {
            // files tracked in the current commit will be untracked in the new commit
            fileMap.remove(staged.getKey());
        }
    }

    public String getParentHash() {
        return parentHash;
    }

    public String getParentHash2() {
        return parentHash2;
    }

    public String getMessage() {
        return message;
    }

    public void printCommit() {
        // formatting target example: Thu Nov 9 20:00:05 2017 -0800
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);

        System.out.println("===");
        System.out.println("commit " + hash);

        // TO DO: need to double check after implementing merge
        if (parentHash2 != null) {
            System.out.println("Merge: " + parentHash.substring(0, 7) + " " + parentHash2.substring(0, 7));
        }

        System.out.println("Date: " + sdf.format(date));
        System.out.println(message);
    }

    public static Commit getCommitFromHash(String hash) {
        File inFile = createFilePath(Repository.COMMIT_DIR, hash, true);
        // bug: create an empty file
        if (inFile != null) {
            Commit c = readObject(inFile, Commit.class);
            return c;
        }
        return null;
    }

    // for debug only
    private void debugPrint() {
        System.out.println(this.message);
        System.out.println(this.date);
        System.out.println(this.hash);
        System.out.println(this.parentHash);
        System.out.println(this.fileMap);
    }

    public static void main(String[] args) {
        File inFile = join(Repository.COMMIT_DIR, "81910db269b64314ce663b6a34a388b0123b4035");
        System.out.println(inFile);
        if (inFile.exists()) {
            //Commit c = readObject(inFile, Commit.class);
            Commit c = Commit.getCommitFromHash("81910db269b64314ce663b6a34a388b0123b4035");
            c.printCommit();
            c.debugPrint();
        }
    }

}
