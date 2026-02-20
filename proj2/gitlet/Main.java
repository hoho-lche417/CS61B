package gitlet;

import static gitlet.Repository.GITLET_DIR;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
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
                Repository.commit(args[1]);
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
                Repository.global_log();
                break;
            case "find":
                validateNumArgs(args, 2);
                Repository.find(args[1]);
                break;
            case "branch":
                validateNumArgs(args, 2);
                Repository.branch(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                Repository.status();
                break;
            case "checkout": // TO DO
                break;
            case "": // what if args is empty?
                throw new GitletException(
                        String.format("Please enter a command."));
            default:
                throw new GitletException(
                        String.format("No command with that name exists."));
        }
    }

    /** Checks the number of arguments versus the expected number,
     *  @param args Argument array from command line
     *  @param n Number of expected arguments
     */
    private static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            // System.out.println("Incorrect operands.");
            throw new GitletException(
                    String.format("Incorrect operands."));
        }
    }

}
