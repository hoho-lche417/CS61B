package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

    //public static String head;

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
    }

    public static void init() {
        Date epochDate;
        Commit c;

        validateNewRepo();
        setupPersistence();
        StagingArea.load();

        // datetime
        epochDate = new Date(0L);

        c = new Commit("initial commit", epochDate, null);

        Branches.init(c.getHash());

        record();
    }

    private static void load() {
        /** If a user inputs a command that requires being in an initialized Gitlet working
         * directory (i.e., one containing a .gitlet subdirectory), but is not in such a directory,
         * print the message Not in an initialized Gitlet directory.
         */
        if (!GITLET_DIR.exists()) {
            //
            throw new GitletException(
                    String.format("Not in an initialized Gitlet directory."));
        }

        Branches.load();
        StagingArea.load();
    }

    private static void record() {
        Branches.record();
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

        c = new Commit(msg, now, Branches.head);

        // set head pointer
        Branches.updateHead(c.getHash());
        //head = c.getHash();

        StagingArea.clear();
        record();
    }

    public static void log() {
        String ptrCommit;
        Commit c;

        load();

        ptrCommit = Branches.head;
        while (ptrCommit != null) {
            c = Commit.getCommitFromHash(ptrCommit);
            c.printCommit();
            System.out.println();
            ptrCommit = c.getParentHash();
        }

        // no need to record()
    }

    public static void global_log() {
        String [] fileList;

        load();

        // TO DO: review after getting clear about the order and branches
        fileList = Repository.COMMIT_DIR.list();
        Arrays.sort(fileList);

        for (String hash : fileList) {
            Commit.getCommitFromHash(hash).printCommit();
            System.out.println();
        }

        // no need to record()
    }

    public static void find(String msg) {
        String [] fileList;
        Commit c;

        load();

        // TO DO: review after getting clear about the order and branches
        fileList = Repository.COMMIT_DIR.list();
        Arrays.sort(fileList);

        for (String hash : fileList) {
            c = Commit.getCommitFromHash(hash);
            if (c.getMessage().equals(msg)) {
                c.printCommit();
                System.out.println();
            }
        }

        // no need to record()
    }

    public static void branch(String name) {
        load();
        Branches.branch(name);
        record();
    }

    public static void checkoutFile(String filename, String commitID) {
        Commit c;
        TreeMap<String, String> mapping;
        String [] fileList;
        Blob b;
        File file;

        load();

        if (commitID == null) {
            c = Commit.getCommitFromHash(Branches.head);
        } else {
            // TO DO: make commit ID to six digits only?
            c = Commit.getCommitFromHash(commitID);
        }

        mapping = c.getMapping();
        if (mapping.containsKey(filename)) {
            // Takes the version of the file in the head commit and puts it in the working directory
            file = createFilePath(Repository.CWD, filename, false);
            b = Blob.getBlobFromHash(mapping.get(filename));
            writeContents(file, b.getContents());
        } else {
            throw new GitletException(
                    String.format("File does not exist in that commit."));
        }

        record();
    }

    public static void checkoutBranch(String branch) {
        load();
        Branches.checkout(branch);
        record();
    }

    public static void status() {
        load();

        System.out.println("=== Branches ===");
        for (String branch : Branches.branches.keySet()) {
            System.out.println(((Branches.current.equals(branch)) ? "*" : "") + branch);
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        for (String staged : StagingArea.stagedForAddition.keySet()) {
            System.out.println(staged);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String staged : StagingArea.stagedForRemoval.keySet()) {
            System.out.println(staged);
        }
        System.out.println();

        // TO DO: extra credits

        // no need to record()
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
