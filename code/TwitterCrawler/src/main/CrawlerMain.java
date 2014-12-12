package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import mysql.AccessData;

/**
 * main class to collect data from twitter over the twitter stream api with
 * multiple threads
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class CrawlerMain {
    private static Controller cntrl;
    private final static String ERROR = "ERROR: A big error has occuered!"
            + "The program will be shut down. Please check your input:"
            + "'<timeout> <database-name> <database-password>'.";

    /**
     * starts a crawler, that collects data from twitter
     * 
     * @param args
     *            1. Argument: The run-time in seconds that the crawler should
     *            run (0 for infinity); 2.Argument: The hostname of the database
     *            to use; 3.Argument: The port of the database; 4.Argument: The
     *            name of the database to use; 5.Argument: The user of the
     *            database to connect with; 6. Argument: The password for the
     *            root user of the database twitter; no more arguments are
     *            required
     */
    public static void main(String[] args) {

        // only numbers from 0-9
        if (args.length > 5 && args[0].matches("[0-9]+")
                && args[2].matches("[0-9]+") && args[1].length() > 0
                && args[3].length() > 0 && args[4].length() > 0
                && args[5].length() > 0) {
            try {
                cntrl = new Controller(Integer.parseInt(args[0]),
                        new AccessData(args[1], args[2], args[3], args[4],
                                args[5]));
            } catch (NumberFormatException | SecurityException | IOException e) {
                System.out.println(ERROR);
                return;
            }
            cntrl.start();
            userInput();
        } else {
            System.out
                    .println(" Error. Wrong argument. Using: crawler [runtime] [databaseName] [password]");
            System.out
                    .println("  runtime:\t The run-time in seconds that the crawler should run.");
            System.out
                    .println("  password:\t The password for the root user of the database twitter.");
        }
    }

    private static void userInput() {
        BufferedReader console = new BufferedReader(new InputStreamReader(
                System.in));
        String command = "";
        while (!command.equals("exit")) {
            System.out.print("crawler> ");
            try {
                command = console.readLine();
            } catch (IOException e) {
                System.out.println(" Error, problem with reading user input.");
            }
            switch (command) {
            case "status":
                System.out.println(cntrl.toString());
                break;
            case "":
                break;
            case "exit":
                break;
            default:
                System.out.println(" Error, could not find command '" + command
                        + "'.");
                ;
            }
        }
        cntrl.shutdown();
        System.exit(0);
    }
}
