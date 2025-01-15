package gitlet;

// 导入
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import gitlet.Repository.*;
import static gitlet.Repository.COMMIT_DIR;
import static gitlet.Repository.CWD;
import static gitlet.Utils.*;

/** 完成与commit有关的事情
 *  @author Link-Liu
 */
public class Commit implements Serializable {
    private String parentId;
    private String parentId2;
    private Date timeStamp;
    private String message;
    private TreeMap<String, String> blobTreeMap; //键是名字，值是哈希
    private String id;
    private TreeSet<String> shaSet = new TreeSet<>();
    /*构造函数*/
    public Commit() {
        this.commit();
    }
    public Commit(String idOfParent, String commitMessage) {
        this.commit(idOfParent, commitMessage);
    }
    /*用于初始化*/
    public void commit() {
        this.parentId = null;
        this.timeStamp = new Date();
        this.message = "initial commit";
        this.blobTreeMap = new TreeMap<>();
        setShaSet();
        this.id = getSha1();
        new Head(getId());
        save();
    }

    /*日常commit*/
    public void commit(String idOfParent, String commitMessage) {
        this.parentId = idOfParent;
        this.timeStamp = new Date();
        this.message = commitMessage;
        this.blobTreeMap = new TreeMap<>();
        if (buildTreeMap()) {
            setShaSet();
            this.id = getSha1();
            Head.setId(getId());
            save();
        }
    }
    /*构建TreeMap*/
    public boolean buildTreeMap() {
        Commit parientCommit;
        File parentFile = join(COMMIT_DIR, this.parentId);
        if (parentFile.exists()) { //复制所有内容
            parientCommit = readObject(parentFile, Commit.class);
            this.blobTreeMap.putAll(parientCommit.getBlobTreeMap());
        }
        Stage stage = Stage.load();
        if (stage.getAddStage().isEmpty()) {
            System.out.println("No changes added to the commit.");
            return false;
        }
        // 创建临时集合用于添加和移除
        Map<String, String> additions = new HashMap<>(stage.getAddStage());
        Set<String> keysToRemove = new HashSet<>();

        // 遍历并确定要移除的键
        for (Map.Entry<String, String> entry : additions.entrySet()) {
            // 如果 blobTreeMap 包含相同内容的文件，则记录其键以备移除
            if (blobTreeMap.containsValue(entry.getValue())) {
                keysToRemove.add(entry.getKey());
            }
        }
        // 批量移除已确定的键
        for (String key : keysToRemove) {
            blobTreeMap.remove(key);
        }
        // 批量添加新的条目
        blobTreeMap.putAll(additions);
        for (String fileToRemove : stage.getRmStages()) {
            File file = join(CWD, fileToRemove);
            if (file.exists()) {
                restrictedDelete(file);
            }
            stage.clear();
            stage.save();
        }
        return true;
    }
    public String getLog() {
        StringBuilder log = new StringBuilder();
        log.append("===\n");
        log.append("commit ").append(getId()).append("\n");
        if (have2Parent()) {
            log.append("Merge: ").append(getParentId(), 0, 7).append(" ");
            log.append(getParentId2(), 0, 7).append("\n");
        }
        log.append("Date: ").append(getTimeStamp()).append("\n");
        log.append(getMessage()).append("\n\n");
        return log.toString();
    }
    /*以哈希值储存文件*/
    public void save() {
        writeObject(join(COMMIT_DIR, getId()), this);
    }
    /*加载文件*/
    public static Commit load(String id) {
        File file = join(COMMIT_DIR, id);
        Commit commit = readObject(file, Commit.class);
        return commit;
    }
    /*所有的返回方法*/
    public static List<String> getUntrackedFileName() {
        List<String> cwdFileName = plainFilenamesIn(CWD);
        List<String> untrackedFileName = new ArrayList<>();
        Set<String> trackedFileName = getCurrentCommit().getBlobTreeMap().keySet();
        Set<String> addedFileName = Stage.load().getAddStage().keySet();
        for (String fileName : cwdFileName) {
            if (!trackedFileName.contains(fileName) && !addedFileName.contains(fileName)) {
                untrackedFileName.add(fileName);
            }
        }
        return untrackedFileName;
    }


    public static Commit getCurrentCommit() {
        String hash = Head.getCurHead();
        return load(hash);
    }
    public String getId() {
        return this.id;
    }
    public String getSha1() {
        return sha1(getShaSet().toArray());
    }
    public String getTimeStamp() {
        SimpleDateFormat ft = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return ft.format(this.timeStamp);
    }
    public String getMessage() {
        return this.message;
    }
    public TreeMap<String, String> getBlobTreeMap() {
        return this.blobTreeMap;
    }
    public String getParentId() {
        return this.parentId;
    }
    public boolean have2Parent() {
        return this.parentId2 != null;
    }
    public String getParentId2() {
        return this.parentId2;
    }
    public TreeSet<String> getShaSet() {
        return this.shaSet;
    }
    public void setShaSet() {
        if (getParentId() != null) {
            getShaSet().add(getParentId());
        }
        if (getParentId2() != null) {
            getShaSet().add(getParentId2());
        }
        getShaSet().add(getTimeStamp());
        getShaSet().add(getMessage());
        getShaSet().add(getBlobTreeMap().toString());
    }
}
