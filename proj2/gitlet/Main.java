package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                Repository.gitInit();
                break;
            case "add":
                Repository.gitAdd(args[1]);
                break;
            case "commit":
                Repository.gitCommit(args[1]);
                break;
            case "rm":
                Repository.gitRemove(args[1]);
                break;
            case "log":
                Repository.gitLog();
                break;
            case "global-log":
                Repository.gitGlobalLog();
                break;
            case "find":
                Repository.gitFind(args[1]);
                break;
            case "status":
                Repository.gitStatus();
                break;
            case "checkout":
                int cmdLength = args.length;
                switch (cmdLength) {
                    case 2:
                        Repository.gitCheckout2(args[1]);
                        break;
                    case 3:
                        Repository.gitCheckout3(args[2]);
                        break;
                    case 4:
                        Repository.gitCheckout4(args[1], args[3]);
                        break;
                }
                break;
            case "branch":
                Repository.gitBranch(args[1]);
                break;
            case "rm-branch":
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                Repository.reset(args[1]);
                break;
        }
    }
}
