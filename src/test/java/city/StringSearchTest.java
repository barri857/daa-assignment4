package city;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringSearchTest {
    private List<StringSearch> algorithms() {
        return List.of(new NaiveSearch(), new KmpSearch());
    }

    @Test
    void referenceLpsArrays() {
        KmpSearch kmp = new KmpSearch();
        assertArrayEquals(new int[]{0, 1, 2, 0, 1, 2, 3, 3}, kmp.lps("AAACAAAA"));
        assertArrayEquals(new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4}, kmp.lps("ABABCABAB"));
        assertArrayEquals(new int[]{0}, kmp.lps("A"));
    }

    @Test
    void referenceMatchesAndOverlaps() {
        for (StringSearch algorithm : algorithms()) {
            assertEquals(List.of(10), algorithm.search("ABABDABACDABABCABAB", "ABABCABAB"));
            assertEquals(List.of(0, 1, 2, 3), algorithm.search("AAAAA", "AA"));
            assertEquals(List.of(0, 2, 4), algorithm.search("ABABABA", "ABA"));
        }
    }

    @Test
    void edgeCases() {
        for (StringSearch algorithm : algorithms()) {
            assertThrows(IllegalArgumentException.class, () -> algorithm.search("abc", ""));
            assertThrows(IllegalArgumentException.class, () -> algorithm.search("", ""));
            assertEquals(List.of(), algorithm.search("ab", "abc"));
            assertEquals(List.of(), algorithm.search("", "a"));
            assertEquals(List.of(), algorithm.search("abc", "x"));
            assertEquals(List.of(0), algorithm.search("abc", "abc"));
            assertEquals(List.of(0, 1, 2), algorithm.search("aaa", "a"));
            assertEquals(List.of(2), algorithm.search("abxyz", "xyz"));
        }
        assertThrows(IllegalArgumentException.class, () -> new KmpSearch().lps(""));
    }

    @Test
    void agreeOnOneHundredRandomTexts() {
        Random random = new Random(42);
        NaiveSearch naive = new NaiveSearch();
        KmpSearch kmp = new KmpSearch();
        for (int i = 0; i < 100; i++) {
            String text = randomString(random, random.nextInt(51));
            String pattern = randomString(random, 1 + random.nextInt(10));
            List<Integer> expected = referenceSearch(text, pattern);
            assertEquals(expected, naive.search(text, pattern), text + ", " + pattern);
            assertEquals(expected, kmp.search(text, pattern), text + ", " + pattern);
        }
    }

    @Test
    void countersIncludePreprocessingAndReset() {
        NaiveSearch naive = new NaiveSearch();
        naive.search("AAAAA", "AA");
        assertEquals(8, naive.metrics().comparisons);
        naive.search("", "A");
        assertEquals(0, naive.metrics().comparisons);
        KmpSearch kmp = new KmpSearch();
        kmp.search("AAAAA", "AA");
        assertEquals(5, kmp.metrics().comparisons);
        assertEquals(1, kmp.metrics().lpsComparisons);
        kmp.search("", "A");
        assertEquals(0, kmp.metrics().comparisons);
        assertEquals(0, kmp.metrics().lpsComparisons);
    }

    @Test
    void lpsMatchesPrefixSuffixDefinition() {
        Random random = new Random(7);
        KmpSearch kmp = new KmpSearch();
        for (int sample = 0; sample < 100; sample++) {
            String pattern = randomString(random, 1 + random.nextInt(20));
            int[] actual = kmp.lps(pattern);
            for (int end = 0; end < pattern.length(); end++) {
                int expected = 0;
                for (int length = 1; length <= end; length++) {
                    if (pattern.substring(0, length).equals(pattern.substring(end + 1 - length, end + 1))) {
                        expected = length;
                    }
                }
                assertEquals(expected, actual[end], pattern + " at " + end);
                assertTrue(actual[end] <= end);
            }
        }
    }

    private List<Integer> referenceSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        int index = text.indexOf(pattern);
        while (index >= 0) {
            matches.add(index);
            index = text.indexOf(pattern, index + 1);
        }
        return matches;
    }

    private String randomString(Random random, int length) {
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < length; i++) {
            value.append((char) ('A' + random.nextInt(3)));
        }
        return value.toString();
    }
}
