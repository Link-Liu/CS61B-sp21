package gitlet;

import java.io.Serializable;
import java.util.*;

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
        getAddStage().clear();
        getRmStages().clear();
    }

    public void save() {
        writeObject(STAGE_DIR, this);
    }

    public TreeMap<String, String> getAddStage() {
        return addStage;
    }

    public TreeSet<String> getRmStages() {
        return rmStages;
    }

    public boolean remove(String filename) {
        if (!this.getAddStage().containsKey(filename)) {
            Commit curCommit = Commit.load(Head.getCurHead());
            if (!curCommit.getBlobTreeMap().containsKey(filename)) {
                return false;
            } else {
                curCommit.getBlobTreeMap().remove(filename);
                this.rmStages.add(filename);
                return true;
            }
        } else {
            this.getAddStage().remove(filename);
            this.save();
            return true;
        }
    }

    public static String printStages() {
        Stage stage = Stage.load();
        Set<String> addStages = stage.getAddStage().keySet();
        Set<String> removeStages = stage.getRmStages();
        StringBuilder sb = new StringBuilder();
        sb.append("=== Staged Files ===").append(System.lineSeparator());
        for (String filename : addStages) {
            sb.append(filename).append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append("=== Removed Files ===");
        for (String filename : removeStages) {
            sb.append(filename).append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        return sb.toString();
    }

}
