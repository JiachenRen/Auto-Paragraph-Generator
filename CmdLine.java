import components.Extractor;
import components.Generator;
import components.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static components.ColoredPrinters.*;


/**
 * Created by Jiachen on 2/25/18.
 * Commandline Application: Automatic Paragraph Generator.
 */
public class CmdLine {
    private static Scanner scan = new Scanner(System.in);
    private static int minSentences = 2;
    private static int maxSentences = 7;
    private static boolean swapVerbs = true;
    private static boolean allowNounAsVerbs;
    private static boolean ignoreInfinitive;
    private static boolean shuffleSentences;
    private static double swapRatio = 0.6;
    private static Generator gen;

    public static void main(String args[]) throws IOException, InterruptedException {
        normal.clear();
        boldGreen.println("Welcome to Auto Paragraph components.Generator by Jiachen Ren");
        cyan.println("Do you wish to customize the paragraph generator? [Y/N]");
        if (bool()) customize();
        boldGreen.println("Initializing automatic paragraph generator...");
        gen = new Generator(swapVerbs, swapRatio);
        gen.setAllowNounAsVerbs(allowNounAsVerbs);
        gen.setIgnoreInfinitive(ignoreInfinitive);
        gen.setShuffleSentences(shuffleSentences);
        cyan.println("Please enter topics (the program will accept any designated delimiters); enter [done] to finish.");
        String topicsRaw = "";
        while (true) {
            String line = scan.nextLine();
            if (line.toLowerCase().equals("done")) {
                break;
            }
            topicsRaw += line + "\n";
        }
        cyan.println("Subject area of concern (Simply press [ENTER] if you wish to do a generic search)? [US History/English... ect.]");
        String postfix = " " + scan.nextLine();
        cyan.println("Please enter delimiter: ");
        String delimiter = scan.nextLine();
        cyan.println("Please enter delimiter between topic and generated paragraph: ");
        String outputDelimiter = scan.nextLine();
        boldGreen.println("Working...");
        String topics[], output = "";
        if (!delimiter.equals("")) topics = topicsRaw.split(delimiter);
        else topics = new String[]{topicsRaw};
        for (String topic : topics) {
            if (topic.endsWith("\n")) topic = topic.substring(0, topic.length() - 1);
            String rawContent = Extractor.search(topic + (postfix.equals(" ") ? "" : postfix));
            ArrayList<Item> items = Item.extractItemsFrom(rawContent);
            int numSentences = (int) (minSentences + Math.random() * (maxSentences - minSentences + 1));
            String paragraph = gen.generateSimpleParagraph(items, numSentences);
            boldYellow.println(topic + outputDelimiter);
            magenta.println(paragraph);
            l("\n");
            output += topic + outputDelimiter + "\n" + paragraph + "\n";
        }
        boldGreen.println("Writing files...");
        Extractor.write("output", "generated", output);
        magenta.println("Done.");
        boldGreen.println("Generated document directory: " + Extractor.pathTo("output").replace(" ", "\\ ") + "generated.txt");
    }


    private static void customize() {
        boldBlack.println("Max number of sentences [1 - 20]: ");
        maxSentences = num(1, 20);
        boldBlack.println("Min number of sentences [1 - 20]: ");
        minSentences = num(1, 20);
        boldBlack.println("Do you wish to shuffle the sentences?");
        shuffleSentences = bool();
        boldBlack.println("Do you wish to swap out some of the verbs? [Y/N]");
        if (bool()) {
            swapVerbs = true;
            boldBlack.println("Do you wish to allow the generator to swap out verbs that are potentially nouns? [Y/N]");
            allowNounAsVerbs = bool();
            boldBlack.println("Do you wish to ignore all verbs in infinitive form when swapping? [Y/N]");
            ignoreInfinitive = bool();
            boldBlack.println("Ratio of verbs to be swapped out [0.0 - 1.0]: ");
            swapRatio = decimal(0, 1);
        }
    }

    private static boolean bool() {
        normal.print("");
        while (true) {
            String response = scan.nextLine().toLowerCase();
            switch (response) {
                case "y":
                    return true;
                case "yes":
                    return true;
                case "n":
                    return false;
                case "no":
                    return false;
            }
            boldRed.println("Please enter \"y\" for yes, \"no\" for no.");
        }
    }

    private static void l(String s) {
        System.out.println(s);
    }

    private static int num(int min, int max) {
        normal.print("");
        while (true) {
            String response = scan.nextLine().toLowerCase();
            try {
                int num = Integer.valueOf(response);
                if (num <= max && num >= min) return num;
                else boldRed.println("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                boldRed.println("Please enter a valid number.");
            }
        }
    }

    private static double decimal(double min, double max) {
        normal.print("");
        while (true) {
            String response = scan.nextLine().toLowerCase();
            try {
                double num = Double.valueOf(response);
                if (num <= max && num >= min) return num;
                else boldRed.println("Please enter a decimal number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                boldRed.println("Please enter a valid decimal number.");
            }
        }
    }

}