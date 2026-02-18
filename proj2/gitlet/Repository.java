package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
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
    public static final File COMMIT_FOLDER = join(GITLET_DIR, "commits");

    /** Folder that references live in. */
    public static final File REF_FOLDER = join(GITLET_DIR, "refs");

    /** Folder that blobs live in. */
    public static final File BLOB_FOLDER = join(GITLET_DIR, "blobs");

    public static String master;
    public static String head;

    /* TODO: fill in the rest of this class. */

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
        if (!COMMIT_FOLDER.exists()) {
            COMMIT_FOLDER.mkdir();
        }
        // ref folders include information about head, master, etc.
        if (!REF_FOLDER.exists()) {
            REF_FOLDER.mkdir();
        }

        if (!BLOB_FOLDER.exists()) {
            BLOB_FOLDER.mkdir();
        }


    }

    public static void init() {
        Date epochDate;
        Commit c;
        File inFile;

        validateNewRepo();
        Repository.setupPersistence();

        // datetime
        epochDate = new Date(0L);

        c = new Commit("initial commit", epochDate, null);
        c.writeToFile();

        // set head pointer
        master = c.getHash(); // how to deal with master and other potential branches?
        head = c.getHash();

        inFile = join(REF_FOLDER, "head");
        if (!inFile.exists()) {
            try {
                inFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writeContents(inFile, head);
    }

    private static void validateNewRepo() {
        // System.exit(0);
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        return;
    }

    public static void main(String[] args) {
        Date epochDate = new Date(0L);

        System.out.println("Epoch Date (default toString format): " + epochDate.toString());
        // System.out.println(Instant.EPOCH);
    }

}
