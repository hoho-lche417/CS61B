package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Utils.eventMessageHandler;

/** Represents the branch mechanism
 *  essentially, it should be part of the repository class,
 *  but due to the relative size of the logic
 *  it is separated out as a standalone class
 *  @author Hoho
 */
public class Branches {

    // a mapping from branch names to the hashes
    private static TreeMap<String, String> branches = new TreeMap<>();

    private static String head;

    // current branch name
    private static String current;

    public static TreeMap<String, String> getBranches() {
        return branches;
    }

    public static String getHead() {
        return head;
    }

    public static String getCurrent() {
        return current;
    }


    public static void init(String hash) {
        branches.put("master", hash);
        current = "master";
        head = hash;
    }

    public static void load() {
        head = readContentsAsString(
                createFilePath(Repository.REF_DIR, "head", false));

        current = readContentsAsString(
                createFilePath(Repository.REF_DIR, "current", false));

        branches = (TreeMap<String, String>) readObject(createFilePath(Repository.REF_DIR,
                "branches", false), TreeMap.class);
    }

    public static void record() {
        writeContents(createFilePath(
                Repository.REF_DIR, "head", false), head);

        writeContents(createFilePath(
                Repository.REF_DIR, "current", false), current);

        writeObject(createFilePath(
                Repository.REF_DIR, "branches", false), branches);
    }

    public static void updateHead(String hash) {
        head = hash;
        branches.put(current, head);
    }

    // create a new branch with name as argument
    public static void branch(String name) {
        if (branches.containsKey(name)) {
            eventMessageHandler("A branch with that name already exists.", true);
        }
        Branches.branches.put(name, Branches.head);
    }

    /** only means to delete the pointer associated with the branch
     *  does not mean to delete all commits that were created under
     *  the branch or anything like that.
     */
    public static void rmbranch(String name) {
        if (!branches.containsKey(name)) {
            eventMessageHandler("A branch with that name does not exist.", true);
        }

        if (current.equals(name)) {
            eventMessageHandler("Cannot remove the current branch.", true);
        }

        branches.remove(name);
    }

    /**
     * @param branch branch name
     */
    public static void checkout(String branch) {

        if (!branches.keySet().contains(branch)) {
            eventMessageHandler("No such branch exists.", true);
        }

        if (current.equals(branch)) {
            eventMessageHandler("No need to checkout the current branch.", true);
        }

        // if a working file is untracked and would be overwritten
        checkUntrackedFiles(branches.get(branch));

        updateCWD(branches.get(branch));

        // set the current branch and update the head
        current = branch;
        updateHead(branches.get(current));

        // clear the staging area
        StagingArea.clear();
    }

    // helper function
    private static void checkUntrackedFiles(String commitID) {
        TreeMap<String, String> mappingCheckout, mappingCurrent;
        String [] fileList;

        // if a working file is untracked and would be overwritten
        fileList = Repository.CWD.list();
        mappingCheckout = Commit.getCommitFromHash(commitID).getMapping();
        mappingCurrent = Commit.getCommitFromHash(head).getMapping();

        for (String filename : fileList) {
            if (!mappingCurrent.containsKey(filename) && mappingCheckout.containsKey(filename)) {
                eventMessageHandler("There is an untracked file in the way; delete it, "
                        + "or add and commit it first.", true);
            }
        }
    }

    /**
     * helper function
     * checkout all the files from the commit and overwrite
     * delete all the tracked files not present in the commit
     * with NO SIDE EFFECT on the references
     * @param commitID on which the update is based
     */
    private static void updateCWD(String commitID) {
        TreeMap<String, String> mappingCheckout, mappingCurrent;
        File file;
        String str;

        mappingCheckout = Commit.getCommitFromHash(commitID).getMapping();
        mappingCurrent = Commit.getCommitFromHash(head).getMapping();

        // checkout all the files from the commit and overwrite
        for (Map.Entry<String, String> entry : mappingCheckout.entrySet()) {
            file = createFilePath(Repository.CWD, entry.getKey(), false);
            str = Blob.getBlobFromHash(entry.getValue()).getContents();
            writeContents(file, str);
        }

        // delete all the tracked files not present in the commit
        for (String filename : mappingCurrent.keySet()) {
            if (!mappingCheckout.containsKey(filename)) {
                restrictedDelete(join(Repository.CWD, filename));
            }
        }
    }

    public static void reset(String commitID) {
        if (Commit.getCommitFromHash(commitID) == null) {
            eventMessageHandler("No commit with that id exists.", true);
        }

        // if a working file is untracked and would be overwritten
        checkUntrackedFiles(commitID);

        updateCWD(commitID);
        updateHead(commitID);

        // clear the staging area
        StagingArea.clear();
    }

    /**
     * helper function originally part of the merge function
     * but factored out due to the size of the merge function
     * @param branch the name of the branch
     * returns the splitHash so that the merge function doesn't
     *               need to compute it again
     */
    private static void trivialCaseHandler(String branch,
                                           String splitHash) {
        String branchHash;
        String oldBranch;

        branchHash = branches.get(branch);

        if (branch.equals(current)) {
            eventMessageHandler("Cannot merge a branch with itself.", true);
        }

        // if split node is the same as the branch to be merged
        if (splitHash.equals(branchHash)) {
            eventMessageHandler("Given branch is an ancestor of the current branch.", true);
        }

        // if split node is the current branch
        if (splitHash.equals(head)) {
            oldBranch = current;
            checkout(branch);
            branches.put(oldBranch, head);
            current = oldBranch;
            eventMessageHandler("Current branch fast-forwarded.", true);
        }
    }

    /**
     * merge the branch given by the parameter into the current branch
     * @param branch the branch name to be merged into the current branch
     */
    public static void merge(String branch) {
        String splitHash, branchHash, log;
        List<String> conflictFiles = new ArrayList<>();
        TreeMap<String, String> mappingCurrent, mappingMerge, mappingSplit;
        File file;

        if (!StagingArea.getStagedForAddition().isEmpty()
                || !StagingArea.getStagedForRemoval().isEmpty()) {
            eventMessageHandler("You have uncommitted changes.", true);
        }

        if (!branches.containsKey(branch)) {
            eventMessageHandler("A branch with that name does not exist.", true);
        }

        checkUntrackedFiles(branches.get(branch));

        branchHash = branches.get(branch);
        splitHash = findSplitNode(branchHash, head);

        trivialCaseHandler(branch, splitHash);

        mappingSplit = Commit.getCommitFromHash(splitHash).getMapping();
        mappingCurrent = Commit.getCommitFromHash(head).getMapping();
        mappingMerge = Commit.getCommitFromHash(branchHash).getMapping();

        for (Map.Entry<String, String> entry : mappingSplit.entrySet()) {
            // both branch contains the file
            if (mappingMerge.containsKey(entry.getKey())
                    && mappingCurrent.containsKey(entry.getKey())) {
                // files modified in the given branch since the split
                // but not modified in the current branch
                // should be changed to their versions in the given branch
                if (!mappingMerge.get(entry.getKey()).equals(entry.getValue())
                        && mappingCurrent.get(entry.getKey()).equals(entry.getValue())) {
                    file = createFilePath(Repository.CWD, entry.getKey(), false);
                    writeContents(file, Blob.getBlobFromHash(
                            mappingMerge.get(entry.getKey())).getContents());
                    StagingArea.getStagedForAddition().put(entry.getKey(),
                            mappingMerge.get(entry.getKey()));
                }
            }

            // files present at the split point, unmodified in the current branch,
            // but absent in the given branch should be removed (and untracked).
            if (mappingCurrent.containsKey(entry.getKey())
                    && mappingCurrent.get(entry.getKey()).equals(entry.getValue())
                    && !mappingMerge.containsKey(entry.getKey())) {
                restrictedDelete(entry.getKey());
                // maybe the map could be replaced by a set later
                StagingArea.getStagedForRemoval().put(entry.getKey(), null);
            }
        }

        // files not present at the split point but present only in the given branch
        // should be checked out and staged
        for (String filename : mappingMerge.keySet()) {
            if (!mappingSplit.containsKey(filename)
                    && !mappingCurrent.containsKey(filename)) {
                StagingArea.record();
                Repository.checkoutFile(filename, branchHash);
                StagingArea.add(filename);
            }
        }

        conflictFiles = checkConflict(branch, mappingSplit);
        conflictHandler(conflictFiles, mappingCurrent, mappingMerge);

        StagingArea.record();
        log = String.format("Merged %s into %s.", branch, current);
        Repository.commit(log, branches.get(branch));
    }

    private static List<String> checkConflict(String branch,
                                      TreeMap<String, String> mappingSplit) {
        String branchHash;
        List<String> conflictFiles = new ArrayList<>();
        TreeMap<String, String> mappingCurrent, mappingMerge;

        branchHash = branches.get(branch);
        mappingCurrent = Commit.getCommitFromHash(head).getMapping();
        mappingMerge = Commit.getCommitFromHash(branchHash).getMapping();

        for (Map.Entry<String, String> entry : mappingSplit.entrySet()) {
            // both branch contains the file
            if (mappingMerge.containsKey(entry.getKey())
                    && mappingCurrent.containsKey(entry.getKey())) {
                // conflict if contents of both are changed and different from other
                if (!mappingMerge.get(entry.getKey()).equals(entry.getValue())
                        && !mappingCurrent.get(entry.getKey()).equals(entry.getValue())
                        && !mappingCurrent.get(entry.getKey()).equals(
                        mappingMerge.get(entry.getKey()))) {
                    conflictFiles.add(entry.getKey());
                }
            }

            // conflict if contents of one are changed and the other file is deleted
            if (mappingMerge.containsKey(entry.getKey())
                    && !mappingMerge.get(entry.getKey()).equals(entry.getValue())
                    && !mappingCurrent.containsKey(entry.getKey())) {
                conflictFiles.add(entry.getKey());
            }
            if (mappingCurrent.containsKey(entry.getKey())
                    && !mappingCurrent.get(entry.getKey()).equals(entry.getValue())
                    && !mappingMerge.containsKey(entry.getKey())) {
                conflictFiles.add(entry.getKey());
            }

        }

        // conflict if file absent at the split but has different contents in both branches
        for (Map.Entry<String, String> entry : mappingCurrent.entrySet()) {
            if (!mappingSplit.containsKey(entry.getKey())
                    && mappingMerge.containsKey(entry.getKey())
                    && !entry.getValue().equals(mappingMerge.get(entry.getKey()))) {
                conflictFiles.add(entry.getKey());
            }
        }

        return conflictFiles;
    }

    // helper function
    private static void conflictHandler(List<String> conflictFiles,
                                        TreeMap<String, String> mappingCurrent,
                                        TreeMap<String, String> mappingMerge) {
        StringBuilder sb;

        for (String filename : conflictFiles) {
            sb = new StringBuilder("<<<<<<< HEAD\n");
            if (mappingCurrent.containsKey(filename)) {
                sb.append(Blob.getBlobFromHash(mappingCurrent.get(filename)).getContents());
            }
            sb.append("=======\n");
            if (mappingMerge.containsKey(filename)) {
                sb.append(Blob.getBlobFromHash(mappingMerge.get(filename)).getContents());
            }
            sb.append(">>>>>>>\n");

            writeContents(createFilePath(Repository.CWD, filename, false), sb.toString());
            StagingArea.add(filename);

            System.out.println("Encountered a merge conflict.");
        }

    }


    /**
     * find the split node given the two commit hashes using BSF graph traversal
     * @param hash1 the hash for the first commit
     * @param hash2 the hash for the second commit
     * @return the hash for the split node found by the algorithm
     */
    private static String findSplitNode(String hash1, String hash2) {
        Map<String, Integer> dist1 = ancestorDistances(hash1);
        Map<String, Integer> dist2 = ancestorDistances(hash2);

        String best = null;
        int bestScore = Integer.MAX_VALUE;

        for (String h : dist1.keySet()) {
            if (dist2.containsKey(h)) {
                int score = dist1.get(h) + dist2.get(h);

                if (score < bestScore) {
                    bestScore = score;
                    best = h;
                }
            }
        }

        return best;   // may be null theoretically, but root guarantees one
    }

    /**
     * helper method for finding the split node
     */
    private static Map<String, Integer> ancestorDistances(String startHash) {
        Map<String, Integer> dist = new TreeMap<>();
        Queue<String> q = new LinkedList<>();

        dist.put(startHash, 0);
        q.add(startHash);

        while (!q.isEmpty()) {
            String h = q.poll();
            int d = dist.get(h);

            Commit c = Commit.getCommitFromHash(h);

            if (c.getParentHash() != null && !dist.containsKey(c.getParentHash())) {
                dist.put(c.getParentHash(), d + 1);
                q.add(c.getParentHash());
            }

            if (c.getParentHash2() != null && !dist.containsKey(c.getParentHash2())) {
                dist.put(c.getParentHash2(), d + 1);
                q.add(c.getParentHash2());
            }
        }

        return dist;
    }




}
