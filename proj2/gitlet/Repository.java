package gitlet;

import java.io.File;
import java.util.*;

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
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet"); //dir
    public static final File OBJECT_DIR = join(GITLET_DIR, "object"); //dir
    public static final File STAGE_DIR = join(GITLET_DIR, "stage");  //file
    public static final File BLOB_DIR = join(OBJECT_DIR, "blob");  //dir
    public static final File COMMIT_DIR = join(OBJECT_DIR, "commit");  //dir
    public static final File HEAD = join(GITLET_DIR, "head");  //file

    private static final int MINCOMMITSIZE = 40;

    public static void gitInit() {
        if (GITLET_DIR.exists()) {
            String msg;
            msg = "A Gitlet version-control system already exists in the current directory.";
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
        File curFile = join(CWD, fileName);
        byte[] curContents = readContents(curFile);
        if (curBlobMap.containsKey(fileName)) {
            Blob lookedBlob = Blob.load(curBlobMap.get(fileName));
            byte[] lookedContents = lookedBlob.getContents();
            if (Arrays.equals(lookedContents, curContents)) {
                Stage stage = Stage.load();
                TreeMap<String, String> adddtion = stage.getAddStage();
                if (adddtion.containsKey(fileName)) {
                    adddtion.remove(fileName);
                    stage.save();
                }
                return;
            }
        }
        Stage stage = Stage.load();
        if (stage.getRmStages().containsKey(fileName)) {
            if (sha1(curContents).equals(stage.getRmStages().get(fileName))) {
                stage.getRmStages().remove(fileName);
                stage.save();
                Blob blob = new Blob(fileName);
                Commit commit = Commit.getCurrentCommit();
                commit.getBlobTreeMap().put(blob.getFileName(), blob.getid());
                commit.save();
                return;
            }
        }
        Blob blob = new Blob(fileName);
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
            curCommitId = curCommit.getParentId(); //更新id
            if (curCommitId == null) {
                break; //最后一个
            }
            curCommit = Commit.load(curCommitId);
        }
        System.out.println(builder.toString());
    }

    public static void gitGlobalLog() {
        List<String> commitList = plainFilenamesIn(COMMIT_DIR);
        Commit commit;
        for (String id : commitList) {
            commit = Commit.load(id);
            if (commit.have2Parent()) {
                printMergeCommit(commit);
            } else {
                printCommit(commit);
            }
        }
    }

    public static void gitFind(String massage) {
        List<String> log = Utils.plainFilenamesIn(COMMIT_DIR);
        StringBuilder builder = new StringBuilder();
        for (String s : log) {
            Commit curCommit = Commit.load(s);
            String curMasssage = curCommit.getMessage();
            if (curMasssage.equals(massage)) {
                builder.append(s).append("\n");
            }
        }
        if (builder.length() == 0) {
            System.out.println("Found no commit with that message.");
        } else {
            System.out.print(builder.toString());
        }
    }

    public static void gitStatus() {
        StringBuilder builder = new StringBuilder();
        builder.append(Head.printBranch());
        builder.append(Stage.printStages());
        builder.append(ModificationChecker.printCwdfiles());
        builder.append("=== Untracked Files ===").append(System.lineSeparator());
        List<String> untrackedFiles = Commit.getUntrackedFileName();
        for (String s : untrackedFiles) {
            builder.append(s);
            builder.append(System.lineSeparator());
        }
        System.out.println(builder.toString());
    }

    public static void gitCheckout2(String branchName) { //branchName是一个名字
        TreeMap<String, String> branchs = readObject(HEAD, Head.class).getBranch();
        if (!branchs.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            return;
        }
        if (Head.getCurHead().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        String hash = branchs.get(branchName);
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
        if (commitId.length() < MINCOMMITSIZE) {
            commitId = findAllHash(commitId);
        }
        List<String> log = plainFilenamesIn(COMMIT_DIR);
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
            System.out.println("File does not exist in that commit.");
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
        for (String fileName : cwdFileNames) {
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

    public static void checkInit() {
        if (!(GITLET_DIR.exists() && GITLET_DIR.isDirectory())) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static String findAllHash(String shortHash) {
        if (shortHash.length() < 6) {
            System.out.println("Short hash is too short to find hash.");
            return shortHash;
        } else {
            List<String> allCommitIds = plainFilenamesIn(COMMIT_DIR);
            int count = 0;
            String hash = shortHash;
            for (String commitId : allCommitIds) {
                if (commitId.startsWith(shortHash)) {
                    count++;
                    hash = commitId;
                }
            }
            if (count == 1) {
                return hash;
            } else {
                System.out.println("More than one commit with that id exists.");
                System.exit(0);
            }
        }
        return shortHash;
    }

    private static void printCommit(Commit currCommmit) {
        System.out.println("===");
        printCommitID(currCommmit);
        printCommitDate(currCommmit);
        printCommitMessage(currCommmit);
    }

    private static void printMergeCommit(Commit currCommmit) {
        System.out.println("===");
        printCommitID(currCommmit);
        printMergeMark(currCommmit);
        printCommitDate(currCommmit);
        printCommitMessage(currCommmit);
    }

    private static void printCommitID(Commit currCommmit) {
        System.out.println("commit " + currCommmit.getId());
    }

    private static void printMergeMark(Commit currCommmit) {
        String parent1 = currCommmit.getParentId();
        String parent2 = currCommmit.getParentId2();
        System.out.println("Merge: " + parent1.substring(0, 7) + " " + parent2.substring(0, 7));
    }

    private static void printCommitDate(Commit currCommmit) {
        System.out.println("Date: " + currCommmit.getTimeStamp());
    }

    private static void printCommitMessage(Commit currCommmit) {
        System.out.println(currCommmit.getMessage() + "\n");
    }

    public static void gitMerge(String branchName) {
        Stage stage = Stage.load();
        checkStageClean(stage);
        Head head = Head.load();
        checkBranchNameExist(branchName, head);
        checkBranchNotCur(branchName, head);
        checkUntracked();
        String headCommitId = Head.getCurHead();
        String branchCommitId = head.getBranch().get(branchName);
        String splitPointCommitId = findSplitPointId(headCommitId, branchCommitId);
        if (splitPointCommitId.equals(headCommitId)) {
            gitCheckout2(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        if (splitPointCommitId.equals(branchCommitId)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        boolean hasConflicts = mergeHelper(stage, headCommitId, branchCommitId, splitPointCommitId);
        String commitMessage =  "Merged" + " " + branchName + " ";
        commitMessage += "into" + " " + head.getCurBranch() + ".";
        new Commit(headCommitId, branchCommitId, commitMessage);
        if (hasConflicts) {
            message("Encountered a merge conflict.");
        }
    }

    private static String findSplitPointId(String curCommitId, String branchCommitId) {
        Queue<String> q = new LinkedList<>();
        HashSet<String> record = new HashSet<>();
        q.add(curCommitId);
        while (!q.isEmpty()) {
            String commitId = q.poll();
            Commit curCommit = Commit.load(commitId);
            for (String parentId : curCommit.getParentIdList()) {
                if (parentId != null) {
                    q.add(parentId);
                    record.add(parentId);
                }
            }
        }
        q.clear();
        q.add(branchCommitId);
        while (!q.isEmpty()) {
            String commitId = q.poll();
            Commit curCommit = Commit.load(commitId);
            if (record.contains(commitId)) {
                return commitId;
            }
            for (String parentId : curCommit.getParentIdList()) {
                if (parentId != null) {
                    q.add(parentId);
                }
            }
        }
        return branchCommitId;
    }
    private static String getConflictContent(String currentBlobId, String targetBlobId) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("<<<<<<< HEAD").append("\n");
        if (currentBlobId != null) {
            Blob currentBlob = Blob.load(currentBlobId);
            contentBuilder.append(currentBlob.getContentAsString());
        }
        contentBuilder.append("=======").append("\n");
        if (targetBlobId != null) {
            Blob targetBlob = Blob.load(targetBlobId);
            contentBuilder.append(targetBlob.getContentAsString());
        }
        contentBuilder.append(">>>>>>>");
        return contentBuilder.toString();
    }
    private static void checkBranchNameExist(String branchName, Head head) {
        if (!head.getBranch().containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
    }
    private static void checkStageClean(Stage stage) {
        if (!stage.getAddStage().isEmpty() || !stage.getRmStages().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
    }
    private static void checkBranchNotCur(String branchName, Head head) {
        if (head.getCurBranch().equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }
    private static void checkUntracked() {
        if (!Commit.getUntrackedFileName().isEmpty()) {
            String massage = "There is an untracked file in the way; ";
            System.out.println(massage + "delete it, or add and commit it first.");
            System.exit(0);
        }
    }

    private static boolean mergeHelper(Stage stage, String head, String branch, String split) {
        TreeMap<String, String> headFiles = Commit.load(head).getBlobTreeMap();
        TreeMap<String, String> branchFiles = Commit.load(branch).getBlobTreeMap();
        TreeMap<String, String> splitFiles = Commit.load(split).getBlobTreeMap();
        boolean hasConflicts = false;
        for (Map.Entry<String, String> entry : splitFiles.entrySet()) {
            String name = entry.getKey();
            String blobhashInSpilt = entry.getValue();
            String blobhashInHead = headFiles.get(name);
            String blobhashInBranch = branchFiles.get(name);
            if (blobhashInBranch != null) { // exist in branch
                if (!blobhashInBranch.equals(blobhashInSpilt)) { // modified in branch
                    if (blobhashInHead != null) { // exist in head
                        if (blobhashInHead.equals(blobhashInSpilt)) { // Not modified in head
                            byte[] contentsToWrite = Blob.load(blobhashInBranch).getContents();
                            writeContents(join(CWD, name), (Object) contentsToWrite);
                            stage.getAddStage().put(name, blobhashInBranch);
                            stage.save(); // case 1
                        } else { // modified in head
                            if (!blobhashInHead.equals(blobhashInBranch)) { // case 8
                                hasConflicts = true; // modified in different way
                                String conflict;
                                conflict = getConflictContent(blobhashInHead, blobhashInBranch);
                                writeContents(join(CWD, name), (Object) conflict);
                                Blob blob = new Blob(name);
                                stage.getAddStage().put(blob.getFileName(), blob.getid());
                                stage.save();
                            } // both in the same way // case 3
                        }
                    } else { //delete in head // case 8
                        hasConflicts = true;
                        String conflictConnent = getConflictContent(null, blobhashInBranch);
                        writeContents(join(CWD, name), (Object) conflictConnent);
                        Blob blob = new Blob(name);
                        stage.getAddStage().put(blob.getFileName(), blob.getid());
                        stage.save();
                    }
                } //not modified in beanch // case 2 and 7
            } else { //delete in branch
                if (blobhashInHead != null) {
                    if (blobhashInHead.equals(blobhashInSpilt)) { // Not modified in head // case 6
                        stage.remove(name);
                    } else { // modified in head // case 8
                        hasConflicts = true;
                        String conflictConnent = getConflictContent(blobhashInHead, null);
                        writeContents(join(CWD, name), (Object) conflictConnent);
                        Blob blob = new Blob(name);
                        stage.getAddStage().put(blob.getFileName(), blob.getid());
                        stage.save();
                    }
                } //delete in head // case 3
            }
            headFiles.remove(name);
            branchFiles.remove(name);
        }
        for (Map.Entry<String, String> entry : branchFiles.entrySet()) {
            String name = entry.getKey();
            String blobhashBranch = entry.getValue();
            String blobhashHead = headFiles.get(name);
            if (blobhashHead != null) { // add in both
                if (!blobhashBranch.equals(blobhashHead)) { // modified in different way // case 8
                    hasConflicts = true;
                    String conflictConnent = getConflictContent(blobhashHead, blobhashBranch);
                    writeContents(join(CWD, name), (Object) conflictConnent);
                    Blob blob = new Blob(name);
                    stage.getAddStage().put(blob.getFileName(), blob.getid());
                    stage.save();
                } // both in the same way // case 3
            } else { // only add in branch // case 5
                byte[] contentsToWrite = Blob.load(blobhashBranch).getContents();
                writeContents(join(CWD, name), (Object) contentsToWrite);
                stage.getAddStage().put(name, blobhashBranch);
                stage.save();
            }
        }
        return hasConflicts;
    }
}
