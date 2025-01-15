package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Repository.HEAD;

public class Head implements Serializable {
    private String id;
    private String curBranch;
    private TreeMap<String, String> branch;
    Head(String id) {
        this.id = id;
        this.branch = new TreeMap<>();
        this.curBranch = "master";
        branch.put("master", id);
        Utils.writeObject(HEAD, this);
    }

    public static void setId(String id) {
        Head head = Utils.readObject(HEAD, Head.class);
        head.id = id;
        getBranch().put(head.getCurBranch(), id);
        save(head);
    }

    public static void checkoutBranch(String branchToCheckout) {
        Head head = Utils.readObject(HEAD, Head.class);
        head.curBranch = branchToCheckout;
        setId(getBranch().get(branchToCheckout));
        save(head);
    }

    public static void createBranch(String branchToCreate) {
        Head head = Utils.readObject(HEAD, Head.class);
        if (getBranch().containsKey(branchToCreate)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        getBranch().put(branchToCreate, getCurHead());
        save(head);
    }

    public static void removeBranch(String branchToRemove) {
        Head head = Utils.readObject(HEAD, Head.class);
        getBranch().remove(branchToRemove);
        save(head);
    }

    public static void save(Head head) {
        Utils.writeObject(HEAD, head);
    }

    public static String getCurHead() {
        Head head = Utils.readObject(HEAD, Head.class);
        return head.id;
    }

    public static TreeMap<String, String> getBranch() {
        Head head = Utils.readObject(HEAD, Head.class);
        return head.branch;
    }

    public String getCurBranch() {
        return this.curBranch;
    }

    public static String printBranch() {
        Head head = Utils.readObject(HEAD, Head.class);
        StringBuilder sb = new StringBuilder();
        sb.append("=== Branches ===").append(System.lineSeparator());
        for (String branchName : getBranch().keySet()) {
            if (branchName.equals(head.getCurBranch())) {
                sb.append("*" + branchName);
            } else {
                sb.append(branchName);
            }
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append("=== Heads ===").append(System.lineSeparator());
        return sb.toString();
    }



}
