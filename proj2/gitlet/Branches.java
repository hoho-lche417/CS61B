package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static gitlet.Utils.*;

public class Branches {

    // a mapping from branch names to the hashes
    public static TreeMap<String, String> branches = new TreeMap<>();

    public static String head;

    // current branch name
    public static String current;

    public static void init(String hash) {
        branches.put("master", hash);
        current = "master";
        head = hash;
    }

    public static void load() {
        File file;
        file = createFilePath(Repository.REF_DIR, "head", false);
        head = readContentsAsString(file);

        file = createFilePath(Repository.REF_DIR, "current", false);
        current = readContentsAsString(file);

        file = createFilePath(Repository.REF_DIR, "branches", false);
        branches = (TreeMap<String, String>) readObject(file, TreeMap.class);
    }

    public static void record() {
        File file;
        file = createFilePath(Repository.REF_DIR, "head", false);
        writeContents(file, head);

        file = createFilePath(Repository.REF_DIR, "current", false);
        writeContents(file, current);

        file = createFilePath(Repository.REF_DIR, "branches", false);
        writeObject(file, branches);
    }

    public static void updateHead(String hash) {
        head = hash;
        branches.put(current, head);
    }

    // create a new branch with name as argument
    public static void branch(String name) {
        Branches.branches.put(name, Branches.head);
    }

    /** only means to delete the pointer associated with the branch
     *  does not mean to delete all commits that were created under the branch, or anything like that.
     */
    public static void rm_branch(String name) {
        if (!branches.containsKey(name)) {
            throw new GitletException(
                    String.format("A branch with that name does not exist."));
        }

        if (current.equals(name)) {
            throw new GitletException(
                    String.format("Cannot remove the current branch."));
        }
        
        branches.remove(name);
    }

    public static void checkout(String branch) {
        Commit c;
        Blob b;
        TreeMap<String, String> mappingCheckout, mappingCurrent;
        File file;
        String str;
        String [] fileList;

        if (!branches.keySet().contains(branch)) {
            throw new GitletException(
                    String.format("No such branch exists."));
        }

        if (current.equals(branch)) {
            throw new GitletException(
                    String.format("No need to checkout the current branch."));
        }

        // if a working file is untracked in the current branch and would be overwritten
        fileList = Repository.CWD.list();
        mappingCheckout = Commit.getCommitFromHash(branches.get(branch)).getMapping();
        mappingCurrent = Commit.getCommitFromHash(head).getMapping();

        for (String filename : fileList) {
            if (!mappingCurrent.containsKey(filename) && mappingCheckout.containsKey(filename)) {
                throw new GitletException(
                        String.format("There is an untracked file in the way; delete it, or add and commit it first."));
            }
        }

        // checkout all the files in the head the new branch with overwriting
        for (Map.Entry<String, String> entry : mappingCheckout.entrySet()) {
            file = createFilePath(Repository.CWD, entry.getKey(), false);
            str = Blob.getBlobFromHash(entry.getValue()).getContents();
            writeContents(file, str);
        }

        // delete all the tracked files that are not in the new branch
        for (String filename : mappingCurrent.keySet()) {
            if (!mappingCheckout.containsKey(filename)) {
                restrictedDelete(join(Repository.CWD, filename));
            }
        }

        // set the current branch and update the head
        current = branch;
        head = branches.get(current);

        // clear the staging area
        StagingArea.clear();
    }
}
