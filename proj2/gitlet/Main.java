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
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.init();
                validateNumArgs(args, 1);
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateNumArgs(args, 2);
                break;
            // TODO: FILL THE REST IN


            case "": // what if args is empty?
                System.out.println("Please enter a command.");
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }

    /** Checks the number of arguments versus the expected number,
     *  @param args Argument array from command line
     *  @param n Number of expected arguments
     */
    private static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            // System.out.println("Incorrect operands.");
            throw new RuntimeException(
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
