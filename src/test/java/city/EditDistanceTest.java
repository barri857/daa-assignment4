package city;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EditDistanceTest {
    private List<EditDistance> algorithms() {
        return List.of(new RecursiveEditDistance(), new TableEditDistance(), new TwoRowEditDistance());
    }

    @Test
    void referenceValues() {
        for (EditDistance algorithm : algorithms()) {
            assertEquals(3, algorithm.distance("kitten", "sitting"), algorithm.name());
            assertEquals(2, algorithm.distance("flaw", "lawn"), algorithm.name());
            assertEquals(5, algorithm.distance("intention", "execution"), algorithm.name());
            assertEquals(3, algorithm.distance("", "abc"), algorithm.name());
            assertEquals(0, algorithm.distance("abc", "abc"), algorithm.name());
        }
    }

    @Test
    void emptyStringsAndSingleCharacters() {
        for (EditDistance algorithm : algorithms()) {
            assertEquals(0, algorithm.distance("", ""), algorithm.name());
            assertEquals(3, algorithm.distance("abc", ""), algorithm.name());
            assertEquals(0, algorithm.distance("a", "a"), algorithm.name());
            assertEquals(1, algorithm.distance("a", "b"), algorithm.name());
            assertEquals(1, algorithm.distance("a", "ab"), algorithm.name());
            assertEquals(1, algorithm.distance("ab", "a"), algorithm.name());
        }
    }

    @Test
    void agreeOnOneHundredRandomPairs() {
        Random random = new Random(42);
        List<EditDistance> algorithms = algorithms();
        for (int i = 0; i < 100; i++) {
            String a = randomString(random, random.nextInt(7));
            String b = randomString(random, random.nextInt(7));
            int expected = algorithms.get(0).distance(a, b);
            for (EditDistance algorithm : algorithms) {
                assertEquals(expected, algorithm.distance(a, b), algorithm.name() + ": " + a + ", " + b);
            }
        }
    }

    @Test
    void countersMeasureWorkAndReset() {
        RecursiveEditDistance recursive = new RecursiveEditDistance();
        recursive.distance("a", "b");
        assertEquals(4, recursive.metrics().calls);
        recursive.distance("", "");
        assertEquals(1, recursive.metrics().calls);
        for (EditDistance algorithm : List.of(new TableEditDistance(), new TwoRowEditDistance())) {
            algorithm.distance("abc", "de");
            assertEquals(6, algorithm.metrics().cells);
            algorithm.distance("", "abc");
            assertEquals(0, algorithm.metrics().cells);
        }
    }

    private String randomString(Random random, int length) {
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < length; i++) {
            value.append((char) ('a' + random.nextInt(3)));
        }
        return value.toString();
    }
}
