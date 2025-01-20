package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static gitlet.Repository.BLOB_DIR;
import static gitlet.Repository.CWD;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    private String fileName;
    private byte[] contents;
    private String id;
    /*将Blog以SHA为名字保存*/
    Blob(String fileName) {
        this.fileName = fileName;
        File file = join(CWD, fileName);
        this.contents = readContents(file);
        save();
    }
    public void save() {
        this.id = getSHA();
        File file = join(BLOB_DIR, id);
        writeObject(file, this);
    }
    /*将Blob提取出来*/
    public static Blob load(String fileSHA) {
        File file = join(BLOB_DIR, fileSHA);
        Blob blob = readObject(file, Blob.class);
        return blob;
    }

    private String getSHA() {
        return sha1(fileName, contents);
    }

    public String getid() {
        return this.id;
    }

    public String getFileName() {
        return this.fileName;
    }

    public byte[] getContents() {
        return this.contents;
    }

    public String getContentAsString(){
        return new String(contents, StandardCharsets.UTF_8);
    }
}
