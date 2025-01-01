package gitlet;

// TODO: any imports you need here

import java.util.Date; // TODO: You'll likely use this in this class

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    private String id;
    private String author;
    private String parentId;
    private String timeStamp;

    /** The message of this Commit. */
    private String message;

    /* TODO: fill in the rest of this class. */
    public String getId() {return this.id;}
    public String getAuthor() {return this.author;}
    public String getParentId() {return this.parentId;}
    public String getTimeStamp() {return this.timeStamp;}
    public String getMessage() {return this.message;}

    public Commit(String id, String author, String parentId, String timeStamp, String message) {

    }
}
