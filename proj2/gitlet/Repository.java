package gitlet;

import java.io.File;
import java.util.List;
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
            System.out.println("A Gitlet version-control system already exists in the current directory.");
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
        Blob blob =new Blob(fileName);
        Stage stage = Stage.load();
        stage.getAddStage().put(blob.getFileName(), blob.getid());
        stage.save();
    }

    public static void gitCommit(String message) {
        Stage stage1 = Stage.load();
        System.out.println(stage1.getAddStage());
        if (message == null) {
            System.out.println("Please enter a commit message.");
        }
        String parentID = Head.getCurHead();
        new Commit(parentID, message);
    }

    public static void gitRemove(String fileName) {
        if (Stage.remove(fileName)) {
            return;
        }else {
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
        if (builder.isEmpty()) {
            System.out.println("Found no commit with that message.");
        }else {
            System.out.println(builder.toString());
        }
    }

    public static void gitStatus(){

    }
    /*从head找文件*/
    public static void gitCheckout3(String fileName) {
        String lookedId = Head.getCurHead();
        Commit lookedCommit = Commit.load(lookedId);
        TreeMap<String, String> referenceMap = lookedCommit.getBlobTreeMap();
        if (referenceMap.containsKey(fileName)) {
            String lookedHash = referenceMap.get(fileName);
            File lookedFile = join(BLOB_DIR, lookedHash);
            byte[] lookedContents = readContents(lookedFile);
            File fileToCheckout = join(CWD, fileName);
            writeContents(fileToCheckout, (Object) lookedContents);
        }else {
            System.out.println("File does not exist.");
        }
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
        }else {
            System.out.println("File does not exist.");
        }
    }

}
