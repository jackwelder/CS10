package HMM;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by jackelder on 5/17/16.
 */

public class Tags {
    private static String character = "c";
    private static String inputString = "";
    public static ArrayList<String> words;
    public static ArrayList<String> tags;
    public static HashMap<String, HashMap<String, Double>> obs;
    public static HashMap<String, HashMap<String, Double>> trans;

    private static final String directory = "/Users/jackelder/IdeaProjects/cs10/inputs/HMM/"; // where all the files are


    public static void numberCorrect(String correctFile, String testFile) throws IOException {
        int numberCorrect = 0;

        ArrayList<String> masterList = new ArrayList<>();
        ArrayList<String> testList = new ArrayList<>();

        // open file in target directory
        BufferedReader master = new BufferedReader(new FileReader(directory + correctFile));
        BufferedReader test = new BufferedReader(new FileReader(directory + testFile));

        try {
            String masterLine;
            while ((masterLine = master.readLine()) != null) {
                String[] words = masterLine.replaceAll("\\p{P}", "").split("\\s+");
                for (String word : words) {
                    masterList.add(word);
                }
            }

            String testLine;
            while ((testLine = test.readLine()) != null) {
                String[] words = testLine.replaceAll("\\p{P}", "").split("\\s+");
                for (String word : words) {
                    testList.add(word);
                }
            }

            for(int i=0; i<masterList.size(); i++){
                if (masterList.get(i).equals(testList.get(i))) numberCorrect++;
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        finally {
            master.close();
            test.close();
        }

        System.out.println(numberCorrect + " out of " + masterList.size() + " tags correctly guessed");
    }

    public static void newTagFile(HashMap<String, HashMap<String, Double>> obsMap,
                                  HashMap<String, HashMap<String, Double>> transMap, String originalFileName) throws IOException {
        BufferedWriter bw = null;

        try {

            // creates a "_predicted_tags" suffix to the output file name for clarity
            String compressedFilePath = directory + originalFileName.substring(0, originalFileName.lastIndexOf(".")) +
                "_predicted_tags.txt";

            BufferedReader inputFile = new BufferedReader(new FileReader(directory + originalFileName));

            File file = new File(compressedFilePath);
            FileWriter fw = new FileWriter(file);
            bw = new BufferedWriter(fw);

            ArrayList<String> lineTags;

            String line;
            while ((line = inputFile.readLine()) != null) {
                lineTags = Viterbi.viterbi(obsMap, transMap, line.replaceAll("[^a-zA-Z]+", " "));
                for (String tag : lineTags) {
                    bw.write(tag);
                    bw.write(" ");
                }
                bw.write(".");
                bw.newLine();
            }
            bw.close();
            System.out.println("File written Successfully");
        }

        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            words = HMMMaps.toList("simple-train-sentences.txt");
            tags = HMMMaps.toList("simple-train-tags.txt");
            trans = HMMMaps.normalizeProb(HMMMaps.makeTransitionMap(tags));
            obs = HMMMaps.normalizeProb(HMMMaps.makeObservationMap(words, tags));

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Commands:");
            System.out.println("c: <Text File (tags)> print number of correct tags in this file");
            System.out.println("t: <String> print most likely tags of this string input to console");
            System.out.println("p: <Text File (words)> create new text file with most likely tags of input text file");

            // continue game until quit via "q" key press
            while (!character.equals("q")) {
                String s = br.readLine();
                // read line of console and determine action based on input
                // String s = br.readLine();
                // read first char to determine method, then rest of substring is actor's name
                if (s.length() > 0) character = s.substring(0, 1);
                if (s.length() > 1){
                    inputString = s.substring(1).trim();
                }

                if (character.equals("c")) {
                    numberCorrect("simple-test-tags.txt", inputString);
                }
                if (character.equals("t")){
                    System.out.println(Viterbi.viterbi(obs, trans, inputString));
                }
                if (character.equals("p")){
                    newTagFile(obs, trans, inputString);
                }
            }


        } catch (IOException e) {
            System.err.println("File Not Found Exception");
        }
    }

}
