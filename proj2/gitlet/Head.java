package gitlet;

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
        head.getBranch().put(head.getCurBranch(), id);
        save(head);
    }

    public static void checkoutBranch(String branchToCheckout) {
        Head head = Utils.readObject(HEAD, Head.class);
        if (head.getCurBranch().equals(branchToCheckout)) {
            System.out.println("No need to checkout the current branch.");
        }
        head.curBranch = branchToCheckout;
        save(head);
        setId(head.getBranch().get(branchToCheckout));
    }

    public static void createBranch(String branchToCreate) {
        Head head = Utils.readObject(HEAD, Head.class);
        if (head.getBranch().containsKey(branchToCreate)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        head.getBranch().put(branchToCreate, getCurHead());
        save(head);
    }

    public static void removeBranch(String branchToRemove) {
        Head head = Utils.readObject(HEAD, Head.class);
        if (head.getCurBranch().equals(branchToRemove)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        if (!head.getBranch().containsKey(branchToRemove)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        head.getBranch().remove(branchToRemove);
        save(head);
    }

    public static void save(Head head) {
        Utils.writeObject(HEAD, head);
    }

    /*return curCommitId*/
    public static String getCurHead() {
        Head head = Utils.readObject(HEAD, Head.class);
        return head.id;
    }

    public  TreeMap<String, String> getBranch() {
        return branch;
    }

    public String getCurBranch() {
        return this.curBranch;
    }

    public static String printBranch() {
        Head head = Utils.readObject(HEAD, Head.class);
        StringBuilder sb = new StringBuilder();
        sb.append("=== Branches ===").append(System.lineSeparator());
        for (String branchName : head.getBranch().keySet()) {
            if (branchName.equals(head.getCurBranch())) {
                sb.append("*" + branchName);
            } else {
                sb.append(branchName);
            }
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    public static Head load() {
        return Utils.readObject(HEAD, Head.class);
    }
}
