# Assignment 4: Dynamic Programming and String Algorithms

**Student:** Temirgaliyev Nurassyl  
**Group:** SE-2523  
**GitHub:** https://github.com/barri857/daa-assignment4  
**Branch:** main  
**Tag:** v1.0

A simple Java 17+ Maven project with recursive, full-table and two-row edit distance, naive search and KMP. Source code contains no comments. DP state, transition and base cases are explained in REPORT.md and REPORT.pdf instead of the source comments requested by the assignment, following the user's explicit preference.

## Build and test

Install a JDK 17 or newer and Maven 3.9+. Make sure JAVA_HOME points to the JDK, and run from this project folder:

```text
mvn package
```

This compiles the project, runs the JUnit 5 tests and creates the JAR. To run only the tests:

```text
mvn test
```

Verified with OpenJDK 25: 10 tests passed, zero failures, errors or skipped tests. Maven downloads dependencies on the first build.

## Run the experiment

After building:

```text
java -cp target/classes city.Experiment
```

It writes results/results.csv and results/environment.txt. The seed is 20261108. Every case uses three warmups and five recorded runs, for 280 CSV rows. The experiment finishes in seconds on the measured machine; times vary by computer. An optional argument chooses another CSV path:

```text
java -cp target/classes city.Experiment results/repeat.csv
```

The same command recreates the same inputs, answers and counters. Wall-clock times and the environment timestamp will vary. Run the charts again after new measurements; the supplied report describes the supplied CSV.

## Generate the three plots

Python 3.11+ and matplotlib are needed only to recreate the PNG charts:

```text
python -m pip install -r requirements.txt
python scripts/plot_results.py
```

Charts show operation counts and median times. KMP counts include preprocessing in addition to search comparisons. All counters are increased inside the algorithms, reset on every public call, and recorded as zero when unused. The CSV columns are algorithm, input, n, m, run, time_ms, calls, cells, comparisons, lps_comparisons, result, seed; result is an edit distance or the number of matches.

## Files

| Path | Contents |
| --- | --- |
| src/main/java/city | Algorithms, shared interfaces, metrics, generator and experiment |
| src/test/java/city | JUnit 5 reference, random and edge-case checks |
| results/results.csv | All 280 individual measured runs |
| results/environment.txt | Machine, Java and experiment settings |
| results/plots | Three PNG charts |
| REPORT.md / REPORT.pdf | Design card, DP explanation, plots and conclusions; PDF is four pages |

All search indices start at zero. An empty pattern throws IllegalArgumentException, a longer pattern returns an empty list, and overlaps are included. KmpSearch.lps(String pattern) is public.

## Git and submission

The local repository uses main and has incremental commits, including the experiment card before the first measurement. The final version is tagged v1.0. The ZIP includes .git so the local history and tag are preserved.

Submission archive: DAA_Assignment4_Nurassyl_Temirgaliyev_SE-2523.zip, using the required name_surname_group order. Upload this archive to Moodle and include the GitHub link above. Moodle upload is not performed by this project.

## AI-use acknowledgement

OpenAI Codex generated the implementation, tests, experiment runner, plotting script and report. The code was checked with JUnit 5, and the reported times and counters came from actual executions. AI assistance was used throughout the preparation of this assignment.
