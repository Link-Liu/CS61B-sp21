package gitlet;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  完成init操作
 *  @author Link-Liu
 */
public class Repository {
    /*--CWD
     *  --.gitlet
     *    --object
     *      --blob
     *    --stage
     *    --head
     *
     *
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet"); //dir
    public static final File OBJECT_DIR = join(GITLET_DIR, "object"); //dir
    public static final File STAGE_DIR = join(GITLET_DIR, "stage");  //file
    public static final File BLOB_DIR = join(OBJECT_DIR, "blob");  //dir
    public static final File COMMIT_DIR = join(OBJECT_DIR, "commit");  //dir
    public static final File HEAD = join(GITLET_DIR, "head");  //file

    public static void gitInit() {
        if (GITLET_DIR.exists()) {
            String msg;
            msg =  "A Gitlet version-control system already exists in the current directory.";
            System.out.println(msg);
        } else {
            GITLET_DIR.mkdir();
            OBJECT_DIR.mkdir();
            Stage stage = new Stage();
            stage.save();
            BLOB_DIR.mkdir();
            COMMIT_DIR.mkdir();
            new Commit();
        }
    }
    /*把文件加入stagingAdd*/
    public static void gitAdd(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        } //错误处理
        TreeMap<String, String> curBlobMap = Commit.getCurrentCommit().getBlobTreeMap();
        if (curBlobMap.containsKey(fileName)) {
            Blob lookedBlob = Blob.load(curBlobMap.get(fileName));
            byte[] lookedContents = lookedBlob.getContents();
            File curFile = join(CWD, fileName);
            byte[] curContents = readContents(curFile);
            if (lookedContents.equals(curContents)) {
                Stage stage = Stage.load();
                TreeMap<String, String> adddtion = stage.getAddStage();
                if (adddtion.containsKey(fileName)) {
                    adddtion.remove(fileName);
                    stage.save();
                }
                return;
            }
        }
        Blob blob = new Blob(fileName);
        Stage stage = Stage.load();
        stage.getAddStage().put(blob.getFileName(), blob.getid());
        stage.save();
    }

    public static void gitCommit(String message) {
        if (message == null || message.isEmpty()) {
            System.out.println("Please enter a commit message.");
        }
        String parentID = Head.getCurHead();
        new Commit(parentID, message);
    }

    public static void gitRemove(String fileName) {
        Stage stage = Stage.load();
        if (stage.remove(fileName)) {
            return;
        } else {
            System.out.println("No reason to remove the file.");
        }
    }

    public static void gitLog() {
        String curCommitId = Head.getCurHead();
        Commit curCommit = Commit.load(curCommitId);
        StringBuilder builder = new StringBuilder();
        while (true) {
            builder.append(curCommit.getLog());
            builder.append("\n");
            curCommitId = curCommit.getParentId(); //更新id
            if (curCommitId == null) {
                break; //最后一个
            }
            curCommit = Commit.load(curCommitId);
        }
        System.out.println(builder.toString());
    }

    public static void gitGlobalLog() {
        List<String> log = Utils.plainFilenamesIn(COMMIT_DIR);
        StringBuilder builder = new StringBuilder();
        for (String s: log) {
            builder.append(s).append("\n");
        }
        System.out.println(builder.toString());
    }

    public static void gitFind(String massage) {
        List<String> log = Utils.plainFilenamesIn(COMMIT_DIR);
        StringBuilder builder = new StringBuilder();
        for (String s: log) {
            Commit curCommit = Commit.load(s);
            String curMasssage = curCommit.getMessage();
            if (curMasssage.equals(massage)) {
                builder.append(s).append("\n");
            }
        }
        if (builder == null) {
            System.out.println("Found no commit with that message.");
        } else {
            System.out.println(builder.toString());
        }
    }

    public static void gitStatus() {
        StringBuilder builder = new StringBuilder();
        builder.append(Head.printBranch());
        builder.append(Stage.printStages());
        builder.append(ModificationChecker.printCwdfiles());
        builder.append("=== Untracked Files ===").append(System.lineSeparator());
        List<String> untrackedFiles = Commit.getUntrackedFileName();
        for (String s: untrackedFiles) {
            builder.append(s);
            builder.append(System.lineSeparator());
        }
        if (untrackedFiles.isEmpty()) {
            builder.append(System.lineSeparator());
        }
        builder.append(System.lineSeparator());
        System.out.println(builder.toString());
    }

    public static void gitCheckout2(String branchName) { //branchName是一个哈希值
        if (!Head.getBranch().containsKey(branchName)) {
            System.out.println("No such branch exists.");
            return;
        }
        if (Head.getCurHead().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
        }
        String hash = Head.getBranch().get(branchName);
        setBranch(hash);
        Head.checkoutBranch(branchName);

    }
    /*从head找文件*/
    public static void gitCheckout3(String fileName) {
        String lookedId = Head.getCurHead();
        gitCheckout4(lookedId, fileName);
    }
    /*从特定commit找文件*/
    public static void gitCheckout4(String commitId, String fileName) {
        List<String> log = Utils.plainFilenamesIn(COMMIT_DIR);
        if (!log.contains(commitId)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit lookedCommit = Commit.load(commitId);
        TreeMap<String, String> referenceMap = lookedCommit.getBlobTreeMap();
        if (referenceMap.containsKey(fileName)) {
            String lookedHash = referenceMap.get(fileName);
            Blob lookedBlob = Blob.load(lookedHash);
            byte[] lookedContents = lookedBlob.getContents();
            File fileToCheckout = join(CWD, fileName);
            writeContents(fileToCheckout, (Object) lookedContents);
        } else {
            System.out.println("File does not exist.");
        }
    }

    public static void gitBranch(String branchName) {
        Head.createBranch(branchName);
    }

    public static void rmBranch(String branchName) {
        Head.removeBranch(branchName);
    }

    public static void reset(String commitId) {
        List<String> log = Utils.plainFilenamesIn(COMMIT_DIR);
        if (!log.contains(commitId)) {
            System.out.println("No commit with that id exists.");
        } else {
            setBranch(commitId);
            Stage stage = Stage.load();
            stage.clear();
            stage.save();
            Head.setId(commitId);
        }
    }

    public static void setBranch(String commitHash) {
        if (!Commit.getUntrackedFileName().isEmpty()) {
            String massage = "There is an untracked file in the way; ";
            System.out.println(massage + "delete it, or add and commit it first.");
            return;
        }
        List<String> cwdFileNames = Utils.plainFilenamesIn(CWD);
        Commit branchCommit = Commit.load(commitHash);
        TreeMap<String, String> blobMap = branchCommit.getBlobTreeMap();
        for (String fileName: cwdFileNames) {
            if (!blobMap.containsKey(fileName)) {
                restrictedDelete(join(CWD, fileName));
            }
        }
        for (Map.Entry<String, String> entry : blobMap.entrySet()) {
            String name = entry.getKey();
            String hash = entry.getValue();
            byte[] blobContents = Blob.load(hash).getContents();
            writeContents(join(CWD, name), blobContents);
        }
    }

}
