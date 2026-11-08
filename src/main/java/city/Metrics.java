package city;

public class Metrics {
    public long calls;
    public long cells;
    public long comparisons;
    public long lpsComparisons;

    public void reset() {
        calls = 0;
        cells = 0;
        comparisons = 0;
        lpsComparisons = 0;
    }
}
