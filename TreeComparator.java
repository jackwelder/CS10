package Lab3;

import apple.laf.JRSUIUtils;

import java.util.Comparator;
import java.util.Map;

/**
 * Comparator for (word,count) map entries
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 */
public class TreeComparator implements Comparator<BinaryTree<TreeData>> {
    public int compare(BinaryTree<TreeData> e1, BinaryTree<TreeData> e2) {
        if (e1.data.getFreq() < e2.data.getFreq()) return -1;
        else if (e1.data.getFreq() > e2.data.getFreq()) return 1;
        else return 0;
    }
}