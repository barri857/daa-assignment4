package city;

public class TwoRowEditDistance implements EditDistance {
    private final Metrics metrics = new Metrics();

    public int distance(String a, String b) {
        metrics.reset();
        int n = a.length();
        int m = b.length();
        int[] previous = new int[m + 1];
        int[] current = new int[m + 1];
        for (int j = 0; j <= m; j++) {
            previous[j] = j;
        }
        for (int i = 1; i <= n; i++) {
            current[0] = i;
            for (int j = 1; j <= m; j++) {
                metrics.cells++;
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    current[j] = previous[j - 1];
                } else {
                    current[j] = 1 + Math.min(previous[j],
                            Math.min(current[j - 1], previous[j - 1]));
                }
            }
            int[] temporary = previous;
            previous = current;
            current = temporary;
        }
        return previous[m];
    }

    public Metrics metrics() {
        return metrics;
    }

    public String name() {
        return "two_rows";
    }
}
