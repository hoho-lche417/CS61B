package gitlet;

import java.io.File;
import java.io.IOException;
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
        file = createFilePath(Repository.REF_DIR, "head");
        head = readContentsAsString(file);

        file = createFilePath(Repository.REF_DIR, "current");
        current = readContentsAsString(file);

        file = createFilePath(Repository.REF_DIR, "branches");
        branches = (TreeMap<String, String>) readObject(file, TreeMap.class);
    }

    public static void record() {
        File file;
        file = createFilePath(Repository.REF_DIR, "head");
        writeContents(file, head);

        file = createFilePath(Repository.REF_DIR, "current");
        writeContents(file, current);

        file = createFilePath(Repository.REF_DIR, "branches");
        writeObject(file, branches);
    }

    public static void updateHead(String hash) {
        head = hash;
        branches.put(current, head);
    }
}
