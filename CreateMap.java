package Lab3;

import apple.laf.JRSUIUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class CreateMap {
    private static final String directory = "/Users/jackelder/IdeaProjects/cs10/inputs/PS3/"; // where all the files are
    private Map<Character, Integer> frequencyMap;
    private PriorityQueue<BinaryTree<TreeData>> queue;

    public CreateMap() {
        frequencyMap = new TreeMap<Character, Integer>();
    }

    public Map<Character, Integer> getMap(){return frequencyMap;}

    /**
     * Loads the word->count map from the file (like UniqueWordsCounts, but from a file)
     */
    public void loadFile(String name) throws Exception {
        System.out.println(name);
        // Loop over lines in the file
        frequencyMap = new TreeMap<Character, Integer>();
        BufferedReader in = new BufferedReader(new FileReader(directory + name));
        String line;
        int n = 0; // # words
        while ((line = in.readLine()) != null) {
            char[] stringToCharArray = line.toCharArray();
            for (char c : stringToCharArray) {
                if (frequencyMap.containsKey(c)) {
                    frequencyMap.put(c, frequencyMap.get(c) + 1);
                } else {
                    // Add the new word
                    frequencyMap.put(c, 1);
                }
                n++;
            }
        }
        in.close();
    }

    public void singletonTrees() {
        Comparator<BinaryTree<TreeData>> comparator = new TreeComparator();
        queue = new PriorityQueue<>(frequencyMap.size(), comparator);
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) { // iterates through frequencyMap pulls keys and
            Character key = entry.getKey();
            Integer value = entry.getValue();
            BinaryTree<TreeData> bt = new BinaryTree<TreeData>(new TreeData(key, value)); // creates a new BinaryTree with type TreeData data
            queue.add(bt);
        }
    }

    public void makeTree(){
        while (queue.size() > 1){
            BinaryTree<TreeData> i = queue.remove();
            BinaryTree<TreeData> j = queue.remove();
            TreeData newData = new TreeData(null, i.data.getFreq() + j.data.getFreq());
            BinaryTree<TreeData> bt = new BinaryTree<TreeData>(newData, i, j);
            queue.add(bt);
        }

    }


    public static void main(String[] args) throws Exception {
        CreateMap map = new CreateMap();
        // Find all the .txt files in the directory and load their word counts
        map.loadFile("USConstitution.txt");
        map.singletonTrees();
        map.makeTree();
    }
}


