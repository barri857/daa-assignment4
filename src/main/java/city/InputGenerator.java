package city;

import java.util.Random;

public final class InputGenerator {
    public static final long SEED = 20261108L;

    public static String[] edit(int n, String type) {
        if (type.equals("disjoint")) {
            return new String[]{"a".repeat(n), "b".repeat(n)};
        }
        Random random = new Random(SEED + 31L * n + 1);
        return new String[]{word(n, random), word(n, random)};
    }

    public static String[] search(int n, int m, String type) {
        if (type.equals("adversarial")) {
            return new String[]{"a".repeat(n), "a".repeat(m - 1) + "b"};
        }
        Random random = new Random(SEED + 31L * n + 17L * m + 2);
        return new String[]{word(n, random), word(m, random)};
    }

    private static String word(int n, Random random) {
        StringBuilder value = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            value.append("ABCD".charAt(random.nextInt(4)));
        }
        return value.toString();
    }
}
