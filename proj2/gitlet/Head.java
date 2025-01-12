package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Repository.HEAD;

public class Head implements Serializable {
    private String id;
    Head(String id) {
        this.id = id;
        save();
    }

    public static void setId(String id) {
        Head head = Utils.readObject(HEAD, Head.class);
        head.id = id;
        head.save();
    }

    public void save() {
        Utils.writeObject(HEAD, this);
    }

    public static String getCurHead() {
        Head head = Utils.readObject(HEAD, Head.class);
        return head.id;
    }

}
