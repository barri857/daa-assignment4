package city;

public class TableEditDistance implements EditDistance {
    private final Metrics metrics = new Metrics();

    // State: dp[i][j] is the distance between the first i and j characters of a and b.
    // Transition: equal characters use dp[i-1][j-1]; otherwise 1 + min(top, left, diagonal).
    // Base case: dp[0][j]=j and dp[i][0]=i.
    public int distance(String a, String b) {
        metrics.reset();
        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                metrics.cells++;
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j],
                            Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
                }
            }
        }
        return dp[n][m];
    }

    public Metrics metrics() {
        return metrics;
    }

    public String name() {
        return "table";
    }
}
