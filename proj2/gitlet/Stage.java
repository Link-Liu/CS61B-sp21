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
    private TreeMap<String, String> addStage;
    private TreeSet<String> rmStages;

    Stage() {
        this.addStage = new TreeMap<>();
        this.rmStages = new TreeSet<>();
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

    public TreeMap<String, String> getAddStage() {return addStage;}

    public boolean remove(String filename) {
        if (!getAddStage().containsKey(filename)) {
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
