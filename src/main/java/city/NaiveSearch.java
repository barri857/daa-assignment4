package city;

import java.util.ArrayList;
import java.util.List;

public class NaiveSearch implements StringSearch {
    private final Metrics metrics = new Metrics();

    public List<Integer> search(String text, String pattern) {
        metrics.reset();
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be empty");
        }
        List<Integer> matches = new ArrayList<>();
        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            int j = 0;
            while (j < pattern.length()) {
                metrics.comparisons++;
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }
                j++;
            }
            if (j == pattern.length()) {
                matches.add(i);
            }
        }
        return matches;
    }

    public Metrics metrics() {
        return metrics;
    }

    public String name() {
        return "naive";
    }
}
