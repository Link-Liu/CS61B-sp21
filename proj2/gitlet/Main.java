package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                checkNumber(args, 1);
                Repository.gitInit();
                break;
            case "add":
                checkNumber(args, 2);
                Repository.checkInit();
                Repository.gitAdd(args[1]);
                break;
            case "commit":
                checkNumber(args, 2);
                Repository.checkInit();
                Repository.gitCommit(args[1]);
                break;
            case "rm":
                checkNumber(args, 2);
                Repository.checkInit();
                Repository.gitRemove(args[1]);
                break;
            case "log":
                checkNumber(args, 1);
                Repository.checkInit();
                Repository.gitLog();
                break;
            case "global-log":
                checkNumber(args, 1);
                Repository.checkInit();
                Repository.gitGlobalLog();
                break;
            case "find":
                checkNumber(args, 2);
                Repository.checkInit();
                Repository.gitFind(args[1]);
                break;
            case "status":
                checkNumber(args, 1);
                Repository.checkInit();
                Repository.gitStatus();
                break;
            case "checkout":
                int cmdLength = args.length;
                Repository.checkInit();
                switch (cmdLength) {
                    case 2:
                        Repository.gitCheckout2(args[1]);
                        break;
                    case 3:
                        if (!args[1].equals("--")) {
                            System.out.println("Incorrect operands.");
                            System.exit(0);
                        }
                        Repository.gitCheckout3(args[2]);
                        break;
                    case 4:
                        if (!args[2].equals("--")) {
                            System.out.println("Incorrect operands.");
                            System.exit(0);
                        }
                        Repository.gitCheckout4(args[1], args[3]);
                        break;
                    default:
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                }
                break;
            case "branch":
                checkNumber(args, 2);
                Repository.checkInit();
                Repository.gitBranch(args[1]);
                break;
            case "rm-branch":
                checkNumber(args, 2);
                Repository.checkInit();
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                checkNumber(args, 2);
                Repository.checkInit();
                Repository.reset(args[1]);
                break;
            case "merge":
                checkNumber(args, 2);
                Repository.checkInit();
                Repository.gitMerge(args[1]);
            default:
                System.out.println("No command with that name exists.");
        }
    }
    public static void checkNumber(String[] args, int numExcepted) {
        if (args.length != numExcepted) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
