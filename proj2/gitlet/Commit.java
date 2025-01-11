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
    private String timeStamp;
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
        this.timeStamp = getTimeStamp();
        this.message = "initial commit";
        this.blobTreeMap = new TreeMap<>();
        getShaSet();
        this.id = getSha1();
        new Head(getId());
        save();
    }

    /*日常commit*/
    public void commit(String idOfParent, String commitMessage) {
        this.parentId = idOfParent;
        this.timeStamp = getTimeStamp();
        this.message = commitMessage;
        this.blobTreeMap = new TreeMap<>();
        getShaSet();
        Head.setId(getId());
        if (builbTreeMap()) {
            save();
        }
    }
    /*构建TreeMap*/
    public boolean builbTreeMap() {
        Commit parientCommit;
        File parentFile = join(COMMIT_DIR, this.parentId);
        if (parentFile.exists()) { //复制所有内容
            parientCommit = readObject(parentFile, Commit.class);
            this.blobTreeMap.putAll(parientCommit.getBlobTreeMap());
        }
        Stage stage = Stage.load();
        if (stage.getAddStage() == null) {
            System.out.println("No changes added to the commit.");
            return false;
        }
        for (Map.Entry<String, String> entry : stage.getAddStage().entrySet()) {
            if (blobTreeMap.containsValue(entry.getValue())) { //如果包含暂存区的同名文件，就删除
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
        SimpleDateFormat f = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z");
        log.append("Date: ").append(f.format(getTimeStamp())).append(System.lineSeparator());
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
    /*获取当前时间*/
    public String getTime() {
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss zzz, E, d MMMM yyyy");
        // 设置时区为UTC
        ft.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (this.timeStamp == null) {
            return ft.format(new Date());
        } else {
            Date d = new Date();
            return ft.format(d);
        }
    }
    /*所有的返回方法*/
    public String getId() {
        return this.id;
    }
    public String getSha1() {
        return sha1(getShaSet().toArray());
    }
    public String getTimeStamp() {
        if (this.timeStamp == null) {
            this.timeStamp = getTime();
        }
        return this.timeStamp;
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
        getShaSet().add(getParentId());
        getShaSet().add(getParentId2());
        getShaSet().add(getTimeStamp());
        getShaSet().add(getMessage());
        getShaSet().add(getBlobTreeMap().toString());
    }
}
