package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Peter
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

    /** a map from filenames to hash strings
     * always have the same order compared to HashMap */
    private TreeMap<String, String> fileMap;


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
        this.fileMap = new TreeMap<>();

        if (parent != null) {
            // inherit from parent's committed snapshot of files by default
            this.fileMap = getCommitFromHash(parent).getMapping();
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
        File outFile = createFilePathFromHash(Repository.COMMIT_DIR, hash);
        writeObject(outFile, this);

        return;
    }

    private void update() {
        for (Map.Entry<String, String> staged : StagingArea.stagedForAddition.entrySet()) {
            // track new files based on staged area for adding
            // update based on staged area for adding
            fileMap.put(staged.getKey(), staged.getValue());
        }

        // TO DO: untrack files based on staged area for removal
        /** files tracked in the current commit may be untracked in the new commit
         *  as a result being staged for removal by the rm command
         */

        return;
    }

    public static Commit getCommitFromHash(String hash) {
        File inFile = createFilePathFromHash(Repository.COMMIT_DIR, hash);
        if (inFile.exists()) {
            Commit c = readObject(inFile, Commit.class);
            return c;
        }
        return null;
    }

    public Commit getParent() {
        // load object based on parentHash
        return getCommitFromHash(parentHash);
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
        File inFile = join(Repository.COMMIT_DIR, "2f", "7cc991cd4c764f71d709625f9449200f91e313");
        System.out.println(inFile);
        if (inFile.exists()) {
            //Commit c = readObject(inFile, Commit.class);
            Commit c = Commit.getCommitFromHash("a8ac55b1b14cec8c5b65719b2448cf00ad1664e6");
            c.debugPrint();
            System.out.println(c);
        }

        return;
    }

}
