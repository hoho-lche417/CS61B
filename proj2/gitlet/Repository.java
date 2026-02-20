package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Peter
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /* INSTANCE VARAIBLES */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** Folder that commit files live in. */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");

    /** Folder that references live in. */
    public static final File REF_DIR = join(GITLET_DIR, "refs");

    /** Folder that blobs live in. */
    public static final File BLOB_DIR = join(GITLET_DIR, "blobs");

    public static final File STAGED_DIR = join(GITLET_DIR, "stage");

    public static String master;
    public static String head;

    /* FUNCTIONS */

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     *
     * .gitlet/ -- top level folder for all persistent data in your lab12 folder
     *    - blobs/ -- folder containing all of the persistent data for dogs
     *    - commits/ -- file containing the current story
     */
    public static void setupPersistence() {
        /* create .gitlet/ */
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
        }
        // create other folders
        if (!COMMIT_DIR.exists()) {
            COMMIT_DIR.mkdir();
        }
        // ref folders include information about head, master, etc.
        if (!REF_DIR.exists()) {
            REF_DIR.mkdir();
        }

        if (!BLOB_DIR.exists()) {
            BLOB_DIR.mkdir();
        }

        StagingArea.load();
    }

    public static void init() {
        Date epochDate;
        Commit c;

        // TO DO: comment the below statement maybe for temporary convenience
        validateNewRepo();
        setupPersistence();

        // datetime
        epochDate = new Date(0L);

        c = new Commit("initial commit", epochDate, null);

        // set head pointer
        master = c.getHash(); // how to deal with master and other potential branches?
        head = c.getHash();

        record();
    }

    private static void load() {
        File file;

        /** If a user inputs a command that requires being in an initialized Gitlet working
         * directory (i.e., one containing a .gitlet subdirectory), but is not in such a directory,
         * print the message Not in an initialized Gitlet directory.
         */
        if (!GITLET_DIR.exists()) {
            //
            throw new GitletException(
                    String.format("Not in an initialized Gitlet directory."));
        }

        file = join(REF_DIR, "head");
        head = readContentsAsString(file);

        StagingArea.load();
    }

    private static void record() {
        File file;
        file = join(REF_DIR, "head");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writeContents(file, head);

        StagingArea.record();
    }

    public static void add(String filename) {
        load();
        StagingArea.add(filename);
        record();
    }

    public static void rm(String filename) {
        load();
        StagingArea.rm(filename);
        record();
    }

    public static void commit(String msg) {
        Date now = new Date();
        Commit c;

        load();
        if (StagingArea.stagedForAddition.isEmpty() && StagingArea.stagedForRemoval.isEmpty()) {
            throw new GitletException(
                    String.format("No changes added to the commit."));
        }

        if (msg == null || msg.equals("")) {
            throw new GitletException(
                    String.format("Please enter a commit message."));
        }

        c = new Commit(msg, now, head);

        // set head pointer
        head = c.getHash();

        StagingArea.clear();
        record();
    }

    private static void validateNewRepo() {
        // System.exit(0);
        if (GITLET_DIR.exists()) {
            throw new GitletException(
                    String.format("A Gitlet version-control system already exists in the current directory."));
        }
        return;
    }

    public static void main(String[] args) {
        Date epochDate = new Date(0L);

        System.out.println("Epoch Date (default toString format): " + epochDate.toString());
        // System.out.println(Instant.EPOCH);
    }

}
