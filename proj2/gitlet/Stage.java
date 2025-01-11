package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.TreeMap;
import static gitlet.Utils.readObject;
import static gitlet.Repository.*;
import static gitlet.Utils.writeObject;

public class Stage implements Serializable {
    private static TreeMap<String, String> addStage = new TreeMap<>();
    private static HashSet<String> rmStages = new HashSet<>();

    Stage() {
        save();
    }

    public static Stage load() {
        Stage stage = readObject(STAGE_DIR, Stage.class);
        return stage;
    }
    public void clear() {
        addStage.clear();
        rmStages.clear();
    }

    public void save() {
        writeObject(STAGE_DIR, this);
    }

    static void addStage (String filename, String sha) {
        addStage.put(filename, sha);
    }

    public TreeMap<String, String> getAddStage() {return addStage;}

    public static boolean remove(String filename) {
        if (!addStage.containsKey(filename)) {
            Commit curCommit = Commit.load(Head.getCurHead());
            if (!curCommit.getBlobTreeMap().containsKey(filename)) {
                return false;
            }else {
                curCommit.getBlobTreeMap().remove(filename);
                rmStages.add(filename);
                return true;
            }
        }else {
            addStage.remove(filename);
            return true;
        }
    }

}
