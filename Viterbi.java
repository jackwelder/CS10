package HMM;

/*
 * @author Jack Elder and Nick Whalley
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;

public class Viterbi {

    public static ArrayList<String> viterbi(HashMap<String, HashMap<String, Double>> obsMap,
                                                             HashMap<String, HashMap<String, Double>> transMap, String string) {

        ArrayList<String> tags = new ArrayList<>();
        ArrayList<HashMap<String, String>> backpointers = new ArrayList<>();
        String stripped = string.replaceAll("[^a-zA-Z]+", " ");
        String[] words = stripped.split(" ");

        HashSet<String> currentStates = new HashSet<>();
        currentStates.add("#");
        HashMap<String, Double> currentScores = new HashMap<>();
        currentScores.put("#", 0.0);

        for (int i = 0; i < words.length; i++) {
            HashSet<String> nextStates = new HashSet<>();
            HashMap<String, Double> nextScores = new HashMap<>();
            HashMap<String, String> backPointer = new HashMap<>();

            for (String currentState : currentStates) {
                if (!transMap.containsKey(currentState)) continue;
                HashMap<String, Double> stateTransitions = transMap.get(currentState);

                for (String nextState : stateTransitions.keySet()) {
                    nextStates.add(nextState);

                    Double cs = currentScores.get(currentState);
                    Double ts = stateTransitions.get(nextState);
                    Double os;

                    HashMap<String, Double> obs = obsMap.get(currentState);
                    if (obs.containsKey(words[i])) {
                        os = obs.get(words[i]);
                    } else {
                        os = -100.0;
                    }
                    Double nextScore = cs + ts + os;
                    //System.out.println(nextScores);

                    if (!nextScores.containsKey(nextState) || nextScore > nextScores.get(nextState)) {
                        nextScores.put(nextState, nextScore);
                        backPointer.put(nextState, currentState);
                    }
                }
            }
            backpointers.add(backPointer);
            currentStates = nextStates;
            currentScores = nextScores;
        }
        Double max = Double.NEGATIVE_INFINITY;
        String maxPos = "";
        for (String pos : currentScores.keySet()) {
            if (currentScores.get(pos) > max) {
                max = currentScores.get(pos);
                maxPos = pos;
            }
        }

        for (int i = backpointers.size()-1; i >= 0; i--) {
            tags.add(maxPos);
            HashMap<String, String> bp = backpointers.get(i);
            maxPos = bp.get(maxPos);
        }
    Collections.reverse(tags);
    return tags;
    }
}