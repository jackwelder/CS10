package HMM;

import Graphs.AdjacencyMapGraph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Created by jackelder on 5/16/16.
 */
public class HMMMaps {

    private static final String directory = "/Users/jackelder/IdeaProjects/cs10/inputs/HMM/"; // where all the files are

    public static ArrayList<String> toList(String textFile) throws IOException {

        // open file in target directory
        BufferedReader inputFile = new BufferedReader(new FileReader(directory + textFile));
        ArrayList<String> list = new ArrayList<>();
        try {
            String line;
            while ((line = inputFile.readLine()) != null) {
                String[] words = line.replaceAll("\\p{P}", "").split("\\s+");
                for (String word : words) {
                    list.add(word);
                }
                list.add("#");
            }

            // print error if the input file is empty to avoid errors later on
            if (list.size() < 1) System.err.println("Input File Empty");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            inputFile.close();
        }

        return list;
    }

    public static HashMap<String, HashMap<String, Double>> makeObservationMap(ArrayList<String> wordslist, ArrayList<String> poslist) {
        HashMap<String, HashMap<String, Double>> obsMap = new HashMap<>();

        for (int i = 0; i < poslist.size(); i++) {
            if (obsMap.containsKey(poslist.get(i))) {
                HashMap<String, Double> wordMap = obsMap.get(poslist.get(i));
                if (wordMap.containsKey(wordslist.get(i))) {
                    Double prev = wordMap.get(wordslist.get(i));
                    wordMap.put(wordslist.get(i), prev + 1.0); // increment frequency
                } else {
                    wordMap.put(wordslist.get(i), 1.0); // put in map
                }
                obsMap.put(poslist.get(i), wordMap);
            }
            else {
                HashMap<String, Double> wordMap = new HashMap<>();
                wordMap.put(wordslist.get(i), 1.0); // put in map
                obsMap.put(poslist.get(i), wordMap);
            }
        }
        return obsMap;
    }

    public static HashMap<String, HashMap<String, Double>> makeTransitionMap(ArrayList<String> poslist) {
        HashMap<String, HashMap<String, Double>> transMap = new HashMap<>();

        for(int i=0; i<poslist.size()-1; i++) {
            if (poslist.get(i + 1).equals("#")){
                continue;
            }
            if (transMap.containsKey(poslist.get(i))) {
            HashMap<String, Double> numberMap = transMap.get(poslist.get(i));
            if (numberMap.containsKey(poslist.get(i + 1))) {
                Double prev = numberMap.get(poslist.get(i + 1));
                numberMap.put(poslist.get(i + 1), prev + 1.0); // increment frequency
            } else {
                numberMap.put(poslist.get(i + 1), 1.0); // put in map
            }
            transMap.put(poslist.get(i), numberMap);
        } else {
            HashMap<String, Double> numberMap = new HashMap<>();
                numberMap.put(poslist.get(i + 1), 1.0); // put in map
                transMap.put(poslist.get(i), numberMap);}
        }
        return transMap;
    }

    public static HashMap<String, HashMap<String, Double>> normalizeProb(HashMap<String, HashMap<String, Double>> map){
        for(String s: map.keySet()){
            HashMap<String, Double> in = map.get(s);
            Double sum = 0.0;

            for(String k: in.keySet()){
                sum += in.get(k);
            }

            for(String l: in.keySet()){
                in.put(l, Math.log(in.get(l)/sum));
            }
            map.put(s,in);
        }
        return map;
    }

    public static void main(String[] args){
        try{
            ArrayList<String> words = toList("simple-train-sentences.txt");
            ArrayList<String> tags = toList("simple-train-tags.txt");
            HashMap<String, HashMap<String, Double>> before = makeTransitionMap(tags);
            System.out.println(before);
            HashMap<String, HashMap<String, Double>> trans = normalizeProb(before);
            System.out.println(trans);

            HashMap<String, HashMap<String, Double>> obsbefore = makeObservationMap(words, tags);
            HashMap<String, HashMap<String, Double>> obs = normalizeProb(obsbefore);
            System.out.println(obsbefore);
            System.out.println(obs);
            System.out.println(Viterbi.viterbi(obs, trans, "he trains the dog"));
        }

        catch (IOException e){
            System.err.println("404 Exception");
        }
    }
}
