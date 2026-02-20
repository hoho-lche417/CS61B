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

    /** If a user inputs a command that requires being in an initialized Gitlet working
     * directory (i.e., one containing a .gitlet subdirectory), but is not in such a directory,
     * print the message Not in an initialized Gitlet directory.
     */
    private static void validateInitialised() {
        // System.exit(0);

        return;
    }

}
