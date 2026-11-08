# Experiment Design Card

Recorded on 8 November 2026 before the first experiment run. Seed: 20261108.

| Field | Plan |
| --- | --- |
| Questions | Edit distance: how quickly does repeated recursion grow, and do two rows keep the full table's answers and work while reducing memory? Search: how much work does KMP save on repeated near-matches? |
| Sizes | Recursive edit distance: n = 2, 4, 6, 8, 10, 12. Full table and two rows: n = 128, 256, 512, 1024, 2048. Both edit strings have length n. Search text: n = 4096, 8192, 16384, 32768, 65536, 131072; pattern length m = 64. Each range covers at least a factor of four; recursion is capped because of exponential work. |
| Edit inputs | Random: independent strings over ABCD, to include matches and mismatches. Disjoint: A repeated n times versus B repeated n times, to force all three recursive branches at every non-base call. |
| Search inputs | Random: independent ABCD text and pattern, to show ordinary early mismatches. Adversarial: A repeated n times versus A repeated 63 times followed by B, to force 64 comparisons at each naive candidate. |
| Metrics | Five individual times in milliseconds, with their median plotted. Count recursive calls including base calls, updated interior DP cells, search character comparisons, and LPS character comparisons separately. Search plots add search and LPS comparisons to include KMP preprocessing. Unused counters are zero. |
| Prediction | Disjoint recursion grows exponentially and random recursion usually costs less. DP cell updates grow as n squared; two rows reduce memory, but both versions have the same cell count. Random searches use roughly linear work; adversarial naive search has a large constant of about 64 comparisons per start, while KMP uses linear work with a much smaller constant. With m fixed, both curves are linear in n; this experiment does not establish quadratic growth for naive search. |
| Plots | (1) Recursive calls and median time versus n, using log y axes. (2) DP cell updates and median time versus n, using log axes. (3) Total search comparisons and median time versus text length for both inputs and both algorithms. Each chart will include a one-sentence takeaway in the report. |
| Procedure | Generate inputs outside timing with a custom generator using fixed-seed java.util.Random, a separate seed per size and input type, and shared strings for competing algorithms. Perform three unmeasured warmups per case, then five measured calls. Time the whole public call with System.nanoTime, including counters, KMP preprocessing and match-list creation. Exclude generation, validation and file writing. Validate answers outside timing. Save one CSV row per measured call. |
| Reproduction | The same command reproduces inputs, results and operation counts exactly. Wall-clock times vary with the computer and JVM; they cannot be identical across runs. No design changes have been made. |

Results, explanations and conclusions will be added after measurement.
