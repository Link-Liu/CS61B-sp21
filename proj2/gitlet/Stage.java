package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import static gitlet.Utils.readObject;
import static gitlet.Repository.*;
import static gitlet.Utils.writeObject;

public class Stage implements Serializable {
    private static TreeMap<String, String> addStage;
    private static TreeSet<String> rmStages;

    Stage() {
        addStage = new TreeMap<>();
        rmStages = new TreeSet<>();
    }

    public static Stage load() {
        return readObject(STAGE_DIR, Stage.class);
    }

    public void clear() {
        addStage.clear();
        rmStages.clear();
    }

    public void save() {
        writeObject(STAGE_DIR, this);
    }

    public void add (String filename, String sha) {
        Stage stage = load();
        stage.getAddStage().put(filename, sha);
        save();
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
