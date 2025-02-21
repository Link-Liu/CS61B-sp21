package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Stage implements Serializable {
    private TreeMap<String, String> addStage;
    private TreeMap<String, String> rmStages;

    Stage() {
        this.addStage = new TreeMap<>();
        this.rmStages = new TreeMap<>();
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

    public TreeMap<String, String> getRmStages() {
        return rmStages;
    }

    public boolean remove(String filename) {
        if (!this.getAddStage().containsKey(filename)) {
            Commit curCommit = Commit.load(Head.getCurHead());
            if (!curCommit.getBlobTreeMap().containsKey(filename)) {
                return false;
            } else {
                curCommit.getBlobTreeMap().remove(filename);
                curCommit.save();
                File file = join(CWD, filename);
                if (file.exists()) {
                    byte[] content = readContents(file);
                    restrictedDelete(file);
                    this.getRmStages().put(filename, sha1((Object) content));
                    this.save();
                    return true;
                } else {
                    getRmStages().put(filename, "0");
                    this.save();
                    return true;
                }
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
        Set<String> removeStages = stage.getRmStages().keySet();
        StringBuilder sb = new StringBuilder();
        sb.append("=== Staged Files ===").append(System.lineSeparator());
        for (String filename : addStages) {
            sb.append(filename).append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append("=== Removed Files ===").append(System.lineSeparator());
        for (String filename : removeStages) {
            sb.append(filename).append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }

}
