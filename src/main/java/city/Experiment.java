package city;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Experiment {
    private static final int WARMUPS = 3;
    private static final int RUNS = 5;
    private static volatile long checksum;

    public static void main(String[] args) throws IOException {
        Path output = Path.of(args.length == 0 ? "results/results.csv" : args[0]);
        Path directory = output.toAbsolutePath().getParent();
        Files.createDirectories(directory);
        writeEnvironment(directory.resolve("environment.txt"));
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(output, StandardCharsets.UTF_8))) {
            writer.println("algorithm,input,n,m,run,time_ms,calls,cells,comparisons,lps_comparisons,result,seed");
            for (String type : Arrays.asList("random", "disjoint")) {
                for (int n : new int[]{2, 4, 6, 8, 10, 12}) {
                    runEdit(writer, InputGenerator.edit(n, type), type, n,
                            List.of(new RecursiveEditDistance()));
                }
                for (int n : new int[]{128, 256, 512, 1024, 2048}) {
                    runEdit(writer, InputGenerator.edit(n, type), type, n,
                            List.of(new TableEditDistance(), new TwoRowEditDistance()));
                }
            }
            for (String type : Arrays.asList("random", "adversarial")) {
                for (int n : new int[]{4096, 8192, 16384, 32768, 65536, 131072}) {
                    runSearch(writer, InputGenerator.search(n, 64, type), type, n);
                }
            }
            if (writer.checkError()) {
                throw new IOException("Could not write results");
            }
        }
        System.out.println("Wrote " + output + "; checksum=" + checksum);
    }

    private static void runEdit(PrintWriter writer, String[] input, String type, int n,
                                List<EditDistance> algorithms) {
        int expected = new TableEditDistance().distance(input[0], input[1]);
        check(new TwoRowEditDistance().distance(input[0], input[1]) == expected);
        for (EditDistance algorithm : algorithms) {
            for (int warmup = 0; warmup < WARMUPS; warmup++) {
                check(algorithm.distance(input[0], input[1]) == expected);
            }
            for (int run = 1; run <= RUNS; run++) {
                long start = System.nanoTime();
                int result = algorithm.distance(input[0], input[1]);
                long elapsed = System.nanoTime() - start;
                checksum += result;
                check(result == expected);
                row(writer, algorithm.name(), type, n, input[1].length(), run,
                        elapsed, algorithm.metrics(), result);
            }
        }
    }

    private static void runSearch(PrintWriter writer, String[] input, String type, int n) {
        List<Integer> expected = new NaiveSearch().search(input[0], input[1]);
        for (StringSearch algorithm : List.of(new NaiveSearch(), new KmpSearch())) {
            for (int warmup = 0; warmup < WARMUPS; warmup++) {
                check(algorithm.search(input[0], input[1]).equals(expected));
            }
            for (int run = 1; run <= RUNS; run++) {
                long start = System.nanoTime();
                List<Integer> result = algorithm.search(input[0], input[1]);
                long elapsed = System.nanoTime() - start;
                checksum += result.size();
                check(result.equals(expected));
                row(writer, algorithm.name(), type, n, input[1].length(), run,
                        elapsed, algorithm.metrics(), result.size());
            }
        }
    }

    private static void row(PrintWriter writer, String algorithm, String type, int n, int m,
                            int run, long elapsed, Metrics metrics, int result) {
        writer.printf(Locale.ROOT, "%s,%s,%d,%d,%d,%.9f,%d,%d,%d,%d,%d,%d%n",
                algorithm, type, n, m, run, elapsed / 1_000_000.0, metrics.calls,
                metrics.cells, metrics.comparisons, metrics.lpsComparisons, result,
                InputGenerator.SEED);
    }

    private static void check(boolean condition) {
        if (!condition) {
            throw new IllegalStateException("Algorithms disagree");
        }
    }

    private static void writeEnvironment(Path output) throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(output, StandardCharsets.UTF_8))) {
            writer.println("Recorded at: " + Instant.now());
            writer.println("Java version: " + System.getProperty("java.version"));
            writer.println("Java VM: " + System.getProperty("java.vm.name"));
            writer.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
            writer.println("Architecture: " + System.getProperty("os.arch"));
            writer.println("CPU: " + System.getenv().getOrDefault("PROCESSOR_IDENTIFIER", "unavailable"));
            writer.println("Available processors: " + Runtime.getRuntime().availableProcessors());
            writer.println("Seed: " + InputGenerator.SEED);
            writer.println("Warmups per case: " + WARMUPS);
            writer.println("Measured runs per case: " + RUNS);
            writer.println("Timer: System.nanoTime; time_ms is elapsed nanoseconds / 1000000.0");
            writer.println("Timing includes algorithm counters, KMP preprocessing and match list construction.");
            writer.println("Input generation, validation, checksum updates and CSV writing are outside timing.");
            writer.println("Each algorithm receives identical inputs; counters reset on every public call.");
            writer.println("Random words use alphabet ABCD and java.util.Random with seed derived from n and m.");
            writer.println("Recursive sizes: 2,4,6,8,10,12; DP sizes: 128,256,512,1024,2048.");
            writer.println("Search sizes: 4096,8192,16384,32768,65536,131072; pattern length: 64.");
            writer.println("Edit inputs: random, disjoint a^n/b^n; search inputs: random, adversarial a^n/a^63b.");
            writer.println("Inputs, answers and operation counts are reproducible; elapsed times vary by run.");
            if (writer.checkError()) {
                throw new IOException("Could not write environment");
            }
        }
    }
}
