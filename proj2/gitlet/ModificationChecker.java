package gitlet;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static gitlet.Repository.CWD;

public class ModificationChecker {
    private List<String> deletedFileNames;
    private List<String> modefiedFileNames;

    ModificationChecker() {
        this.modefiedFileNames = new LinkedList<String>();
        this.deletedFileNames = new LinkedList<String>();
    }

    public void check() {
        List<String> cwdFileNames = Utils.plainFilenamesIn(CWD);
        Set<String> trackedFileNames = Commit.getCurrentCommit().getBlobTreeMap().keySet();
        for (String fileName : trackedFileNames) {
            if (!Stage.load().getRmStages().containsKey(fileName)) {
                if (!cwdFileNames.contains(fileName)) {
                    deletedFileNames.add(fileName);
                    continue;
                }
                File cwdFile = Utils.join(CWD, fileName);
                String blobHash = Commit.getCurrentCommit().getBlobTreeMap().get(fileName);
                byte[] blobContent = Blob.load(blobHash).getContents();
                boolean modified = !Arrays.equals(blobContent, Utils.readContents(cwdFile));
                boolean added = !Stage.load().getAddStage().containsKey(fileName);
                if (modified && added) {
                    modefiedFileNames.add(fileName);
                }
            }
        }
    }
    public List<String> getDeletedFileNames() {
        return deletedFileNames;
    }
    public List<String> getModefiedFileNames() {
        return modefiedFileNames;
    }
    public static String printCwdfiles() {
        ModificationChecker checker = new ModificationChecker();
        checker.check();
        StringBuilder sb = new StringBuilder();
        sb.append("=== Modifications Not Staged For Commit ===");
        sb.append(System.lineSeparator());
        for (String fileName : checker.getDeletedFileNames()) {
            sb.append(fileName).append(" (deleted)").append(System.lineSeparator());
        }
        for (String fileName : checker.getModefiedFileNames()) {
            sb.append(fileName).append(" (modefied)").append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }
}
