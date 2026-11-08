package city;

import java.util.ArrayList;
import java.util.List;

public class KmpSearch implements StringSearch {
    private final Metrics metrics = new Metrics();

    public int[] lps(String pattern) {
        metrics.reset();
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be empty");
        }
        return buildLps(pattern);
    }

    private int[] buildLps(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        int i = 1;
        while (i < pattern.length()) {
            metrics.lpsComparisons++;
            if (pattern.charAt(i) == pattern.charAt(length)) {
                lps[i] = ++length;
                i++;
            } else if (length > 0) {
                length = lps[length - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    public List<Integer> search(String text, String pattern) {
        metrics.reset();
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be empty");
        }
        List<Integer> matches = new ArrayList<>();
        if (pattern.length() > text.length()) {
            return matches;
        }
        int[] lps = buildLps(pattern);
        int i = 0;
        int j = 0;
        while (i < text.length()) {
            metrics.comparisons++;
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) {
                    matches.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return matches;
    }

    public Metrics metrics() {
        return metrics;
    }

    public String name() {
        return "kmp";
    }
}
