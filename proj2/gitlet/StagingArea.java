package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Utils.*;

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
        File file = join(Repository.REF_DIR, "staged_add");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writeObject(file, stagedForAddition);
        file = join(Repository.REF_DIR, "staged_rm");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writeObject(file, stagedForRemoval);
    }

    public static void add(String filename) {
        List<String> filenameList = plainFilenamesIn(Repository.CWD);
        if (!filenameList.contains(filename)) {
            throw new GitletException(
                    String.format("File does not exist."));
        }
        String contents = readContentsAsString(join(Repository.CWD, filename));
        String hash = sha1(contents);
        String oldHash, commitedFileHash;
        File file;
        Blob b;
        Commit c;

        //load();

        // remove mapping from stagedForRemoval
        stagedForRemoval.remove(filename);

        // if same as current commit, remove mapping
        c = Commit.getCommitFromHash(Repository.head);
        commitedFileHash = c.getMapping().get(filename);
        if (commitedFileHash != null && commitedFileHash.equals(hash)) {
            stagedForAddition.remove(filename);
        }

        if (!stagedForAddition.containsKey(filename)) {
            // create blob from the filename
            file = createFilePathFromHash(Repository.BLOB_DIR, hash);
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
            file = createFilePathFromHash(Repository.BLOB_DIR, hash);
            b = new Blob(hash, contents);
            writeObject(file, b);
            stagedForAddition.put(filename, hash);
            // remove old blob
            file = createFilePathFromHash(Repository.BLOB_DIR, oldHash);
            b = readObject(file, Blob.class);
            if (b.isOrphan()) { // is it necessary for the check?
                restrictedDelete(file);
            }
        }

        //record();

    }

    public static void rm(String filename) {
        TreeMap<String, String> commitMapping;
        // unstage the file if it is currently staged for addition
        stagedForAddition.remove(filename);

        // If the file is tracked in the current commit
        // stage it for removal and remove the file from the working directory
        commitMapping = Commit.getCommitFromHash(Repository.head).getMapping();
        if (commitMapping.containsKey(filename)) {
            stagedForRemoval.put(filename, commitMapping.get(filename));
            restrictedDelete(join(Repository.CWD, filename));
        }
    }

    public static void main(String[] args) {
        File inFile = join(Repository.REF_DIR, "staged_add");
        System.out.println(inFile);
        if (inFile.exists()) {
            stagedForAddition = (TreeMap<String, String>) readObject(inFile, TreeMap.class);
            System.out.println(stagedForAddition);
        }
        return;
    }
}
