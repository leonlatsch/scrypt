package de.leonl.scrypt.ui.cmd;

import java.io.File;

import de.leonl.scrypt.crypt.Crypter;
import de.leonl.scrypt.data.Mode;
import de.leonl.scrypt.data.StreamObject;
import de.leonl.scrypt.err.TerminalException;

public class Terminal {

    private String[] args;

    private String path;

    private File inFile;
    private File outFile;

    private Mode mode;

    private Crypter crypter;

    private static final String FILE_TYPE = ".crypt";

    public Terminal(String[] args) {
        this.args = args;
        this.path = args[1];
        init();
    }

    public void init() {
        crypter = new Crypter();
        try {
            mapMode();
            mapPaths();
        } catch (TerminalException e) {
            println("Invalid mode!");
            System.exit(0);
        }
    }

    private void doInfo() {
        br();
        println("SCrypt File Encrypter");
        br();
        println("Note: //////");
        br();
        println("");
    }

    public void run() {
        doInfo();
        println("Input: " + inFile.getAbsolutePath());
        println("Output: " + outFile.getAbsolutePath());
        br();
        String passwd = "";
        while (passwd.length() < 4) {
            passwd = readPassword("Enter a password:");
            if ("q".equalsIgnoreCase(passwd)) {
                println("Exiting...");
                return;
            }
        }

        br();

        switch (mode) {
            case ENCRYPT:
                println("Encrypting " + inFile.getName() + " ...");
                try {
                    encryptTask(passwd);
                } catch (Exception e) {
                    println("Something went wrong");
                }
                break;
            case DECRYPT:
                println("Decrypting " + inFile.getName() + " ...");
                try {
                    decryptTask(passwd);
                } catch (Exception e) {
                    println("Something went wrong");
                }
                break;
            default:
                throw new RuntimeException("FATAL ERROR");
        }
        br();
        println("DONE");

    }

    private void print(String str) {
        System.out.print("# " + str);
    }

    private void br() {
        System.out.println("#");
    }

    private void println(String str) {
        System.out.println("# " + str);
    }

    /*
     * private String read() { return console.readLine(); } private String read(String str) { println(str); print("--> "); return
     * console.readLine(); }
     */
    private String readPassword(String str) {
        println(str);
        print("--> ");
        return System.console().readPassword().toString();
    }

    /**
     * @return true of 2 args are present
     */
    public static boolean validTerminalArgs(String[] args) {
        return args.length == 2;
    }

    private void mapMode() throws TerminalException {
        String arg1 = args[0];
        if (arg1.equalsIgnoreCase("--encrypt") || arg1.equalsIgnoreCase("-e")) {
            mode = Mode.ENCRYPT;
        } else if (arg1.equalsIgnoreCase("--decrypt") || arg1.equalsIgnoreCase("-d")) {
            mode = Mode.DECRYPT;
        } else {
            throw new TerminalException("ERROR: Invalid mode");
        }
    }

    private void mapPaths() throws TerminalException {
        File in = new File(this.path);
        File out = null;

        switch (mode) {
            case ENCRYPT:
                out = new File(path + FILE_TYPE);
                break;
            case DECRYPT:
                out = new File(path.replace(FILE_TYPE, ""));
                break;
            default:
                throw new RuntimeException("FATAL ERROR");

        }
        if (in.isDirectory()) {
            throw new TerminalException("Input must not be a directory!");
        }
        inFile = in;
        outFile = out;
    }

    private void encryptTask(String passwd) throws Exception {
        StreamObject streams = crypter.encrypt(inFile, outFile, crypter.genkey(passwd));
        try {
            int i;
            byte[] b = new byte[1024];

            while ((i = streams.getIn().read(b)) != -1) {
                streams.getOut().write(b, 0, i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            streams.close();
        }
    }

    private void decryptTask(String passwd) throws Exception {
        StreamObject streams = crypter.decrypt(inFile, outFile, crypter.genkey(passwd));
        try {
            int i;
            byte[] b = new byte[1024];

            while ((i = streams.getIn().read(b)) != -1) {
                streams.getOut().write(b, 0, i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            streams.close();
        }
    }

}
