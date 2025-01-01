package bstmap;

public class testPrint {
    public static void main(String[] args) {
        BSTMap<String,String> q = new BSTMap<String,String>();
        q.put("c","a");
        q.put("b","a");
        q.put("a","a");
        q.put("d","a");
        q.put("e","a"); // a b c d e
        q.put("f","a");
        q.put("g","a");
        q.put("h","a");
        q.put("i","a");
        q.put("j","a");
        q.put("k","a");
        q.put("l","a");
        q.put("m","a");
        q.printInOrder();
    }
}
