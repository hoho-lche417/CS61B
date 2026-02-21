package gitlet;

import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Hoho
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            errorHandler("Please enter a command.", true);
        }
        String firstArg = args[0]; // assume that the cmd is always provided?
        switch(firstArg) {
            case "init":
                validateNumArgs(args, 1);
                Repository.init();
                break;
            case "add":
                //In gitlet, only one file may be added at a time.
                validateNumArgs(args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                validateNumArgs(args, 2);
                Repository.commit(args[1], null);
                break;
            case "rm":
                validateNumArgs(args, 2);
                Repository.rm(args[1]);
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.log();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                Repository.globallog();
                break;
            case "find":
                validateNumArgs(args, 2);
                Repository.find(args[1]);
                break;
            case "branch":
                validateNumArgs(args, 2);
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                Repository.rm_branch(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                Repository.status();
                break;
            case "checkout":
                checkoutHandler(args);
                break;
            case "reset":
                validateNumArgs(args, 2);
                Repository.reset(args[1]);
                break;
            case "merge":
                validateNumArgs(args, 2);
                Repository.merge(args[1]);
                break;
            default:
                errorHandler("No command with that name exists.", true);
        }
    }

    /** Checks the number of arguments versus the expected number,
     *  @param args Argument array from command line
     *  @param n Number of expected arguments
     */
    private static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            errorHandler("Incorrect operands.", true);
        }
    }

    // special case since the arguments are more complex compared to other commands
    private static void checkoutHandler(String[] args) {
        if (args.length == 3 && args[1].equals("--")) {
            Repository.checkoutFile(args[2], null);
        } else if (args.length == 4 && args[2].equals("--")) {
            Repository.checkoutFile(args[3], args[1]);
        } else if (args.length == 2){
            Repository.checkoutBranch(args[1]);
        } else {
            errorHandler("Incorrect operands.", true);
        }
    }

}
