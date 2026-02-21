package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Utils.*;

/** Represents the staging area mechanism
 *  essentially, it should be part of the repository class, but due to the relative size of the logic
 *  it is separated out as a standalone class
 *  @author Hoho
 */
public class StagingArea {

    /** a map from filenames to hash strings (representing blobs)
     * always have the same order compared to HashMap */
    public static TreeMap<String, String> stagedForAddition;

    public static TreeMap<String, String> stagedForRemoval;

    public static void load() {
        File file = join(Repository.REF_DIR, "staged_add");
        if (file.exists()) {
            stagedForAddition = (TreeMap<String, String>) readObject(file, TreeMap.class);
        } else {
            stagedForAddition = new TreeMap<>();
        }
        file = join(Repository.REF_DIR, "staged_rm");
        if (file.exists()) {
            stagedForRemoval = (TreeMap<String, String>) readObject(file, TreeMap.class);
        } else {
            stagedForRemoval = new TreeMap<>();
        }
    }

    public static void clear() {
        stagedForAddition.clear();
        stagedForRemoval.clear();
    }

    public static void record() {
        File file = createFilePath(Repository.REF_DIR, "staged_add", false);
        writeObject(file, stagedForAddition);
        file = createFilePath(Repository.REF_DIR, "staged_rm", false);
        writeObject(file, stagedForRemoval);
    }

    public static void add(String filename) {
        List<String> filenameList = plainFilenamesIn(Repository.CWD);

        if (!filenameList.contains(filename)) {
            errorHandler("File does not exist.", true);
        }

        String contents = readContentsAsString(join(Repository.CWD, filename));
        String hash = sha1(contents);
        String oldHash, commitedFileHash;
        File file;
        Blob b;
        Commit c;

        // remove mapping from stagedForRemoval
        stagedForRemoval.remove(filename);

        // if same as current commit, remove mapping
        c = Commit.getCommitFromHash(Branches.head);
        commitedFileHash = c.getMapping().get(filename);
        if (commitedFileHash != null && commitedFileHash.equals(hash)) {
            stagedForAddition.remove(filename);
            return;
        }

        if (!stagedForAddition.containsKey(filename)) {
            // create blob from the filename
            file = createFilePath(Repository.BLOB_DIR, hash, false);
            b = new Blob(hash, contents);
            writeObject(file, b);
            // add a new mapping from filename to hash
            stagedForAddition.put(filename, hash);
        }

        // TO DO: simply logic?
        // if (stagedForAddition.containsKey(filename)) {
        // compare new hash with the old hash
        // if same do nothing
        // if different, overwrite old blob, update the mapping
        oldHash = stagedForAddition.get(filename);
        if (!oldHash.equals(hash)) {
            file = createFilePath(Repository.BLOB_DIR, hash, false);
            b = new Blob(hash, contents);
            writeObject(file, b);
            stagedForAddition.put(filename, hash);
            // remove old blob
            file = createFilePath(Repository.BLOB_DIR, oldHash, false);
            b = readObject(file, Blob.class);
            if (b.isOrphan()) { // is it necessary for the check?
                restrictedDelete(file);
            }
        }

    }

    public static void rm(String filename) {
        TreeMap<String, String> commitMapping;

        commitMapping = Commit.getCommitFromHash(Branches.head).getMapping();

        // If the file is neither staged nor tracked by the head commit
        if (!stagedForAddition.containsKey(filename) &&
                !commitMapping.containsKey(filename)) {
            errorHandler("No reason to remove the file", true);
        }

        // unstage the file if it is currently staged for addition
        stagedForAddition.remove(filename);

        // If the file is tracked in the current commit
        // stage it for removal and remove the file from the working directory
        if (commitMapping.containsKey(filename)) {
            stagedForRemoval.put(filename, commitMapping.get(filename));
            restrictedDelete(join(Repository.CWD, filename));
        }
    }

}
