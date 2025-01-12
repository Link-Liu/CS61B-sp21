package gitlet;

// 导入
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import gitlet.Repository.*;
import static gitlet.Repository.COMMIT_DIR;
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
            this.id = getSha1();
            Head.setId(getId());
            setShaSet();
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
        for (Map.Entry<String, String> entry : stage.getAddStage().entrySet()) {
            if (blobTreeMap.containsValue(entry.getValue())) { //如果包含暂存区相同文件，就删除
                String objectKey = entry.getKey();
                blobTreeMap.remove(objectKey);
            }
            blobTreeMap.putAll(stage.getAddStage());
            stage.clear();
            stage.save();
        }
        return true;
    }
    public String getLog() {
        StringBuilder log = new StringBuilder();
        log.append("===").append(System.lineSeparator());
        log.append("commit ").append(getId()).append(System.lineSeparator());
        if (have2Parent()) {
            log.append("parent ").append(getParentId(), 0 ,7).append(" ");
            log.append(getParentId2()).append(System.lineSeparator());
        }
        log.append("Date: ").append(getTimeStamp()).append(System.lineSeparator());
        log.append(getMessage()).append(System.lineSeparator());
        return log.toString();
    }
    /*以哈希值储存文件*/
    public void save() {
        writeObject(join(COMMIT_DIR, getId()), this);
    }
    /*加载文件*/
    public static Commit load(String id) {
        File file = join(COMMIT_DIR, id);
        return readObject(file, Commit.class);
    }
    /*所有的返回方法*/
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
