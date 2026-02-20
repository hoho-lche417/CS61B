package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeSet;

import static gitlet.Utils.*;

public class Blob implements Serializable {

    /** the hash of the file contents (not the blob itself)
     * i.e. the file name of the blob is determined by the file contents
     */
    private String hash;
    /* the file contents */
    private String contents;
    /* the set of commit hashes that refers to the blob */
    private TreeSet<String> refBy;

    public Blob(String hash, String contents) {
        this.hash = hash;
        this.contents = contents;
        refBy = new TreeSet<>();
    }

    public String getContents() {
        return contents;
    }

    public String getHash() {
        return hash;
    }

    // check to see if the blob is referenced by any commits
    public boolean isOrphan() {
        return refBy.isEmpty();
    }

    public static Blob getBlobFromHash(String hash) {
        File file = createFilePath(Repository.BLOB_DIR, hash, true);
        if (file != null) {
            Blob b = readObject(file, Blob.class);
            return b;
        } else {
            return null;
        }
    }

    public void addRef(String commitHash) {
        refBy.add(commitHash);
    }

    public void removeRef(String commitHash) {
        refBy.remove(commitHash);
    }

    /**
     * return the file contents as a String from the path indicated by the arguments
     * @param folderName
     * @param fileName
     */
    public String readFromFile(String folderName, String fileName) {
        return "";
    }

    public void writeToFile(String folderName, String fileName, String contents) {
        return;
    }
}
