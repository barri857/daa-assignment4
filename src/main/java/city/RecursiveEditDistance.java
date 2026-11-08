package city;

public class RecursiveEditDistance implements EditDistance {
    private final Metrics metrics = new Metrics();

    public int distance(String a, String b) {
        metrics.reset();
        return solve(a, b, a.length(), b.length());
    }

    private int solve(String a, String b, int i, int j) {
        metrics.calls++;
        if (i == 0) {
            return j;
        }
        if (j == 0) {
            return i;
        }
        if (a.charAt(i - 1) == b.charAt(j - 1)) {
            return solve(a, b, i - 1, j - 1);
        }
        int delete = solve(a, b, i - 1, j);
        int insert = solve(a, b, i, j - 1);
        int replace = solve(a, b, i - 1, j - 1);
        return 1 + Math.min(delete, Math.min(insert, replace));
    }

    public Metrics metrics() {
        return metrics;
    }

    public String name() {
        return "recursive";
    }
}
