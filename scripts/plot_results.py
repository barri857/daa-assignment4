import argparse
import csv
from pathlib import Path
from statistics import median

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt


LABELS = {
    "recursive": "Recursive",
    "table": "Full table",
    "two_rows": "Two rows",
    "naive": "Naive",
    "kmp": "KMP",
}
COLORS = {
    "recursive": "#2454a4",
    "table": "#2454a4",
    "two_rows": "#bc4a18",
    "naive": "#bc4a18",
    "kmp": "#2454a4",
}
STYLES = {"random": "--", "disjoint": "-", "adversarial": "-"}
MARKERS = {"random": "s", "disjoint": "o", "adversarial": "o"}


def load_results(path):
    groups = {}
    with path.open(newline="", encoding="utf-8") as source:
        for row in csv.DictReader(source):
            key = row["algorithm"], row["input"], int(row["n"])
            groups.setdefault(key, []).append(row)
    if not groups:
        raise ValueError("The results file is empty.")
    summary = {}
    for key, runs in groups.items():
        if len(runs) != 5:
            raise ValueError(f"Expected five measured runs for {key}.")
        summary[key] = {
            "time_ms": median(float(row["time_ms"]) for row in runs),
            "calls": median(int(row["calls"]) for row in runs),
            "cells": median(int(row["cells"]) for row in runs),
            "total_comparisons": median(
                int(row["comparisons"]) + int(row["lps_comparisons"])
                for row in runs
            ),
        }
    return summary


def draw_series(axis, summary, algorithms, inputs, metric):
    for algorithm in algorithms:
        for input_type in inputs:
            sizes = sorted(
                n for name, kind, n in summary
                if name == algorithm and kind == input_type
            )
            if not sizes:
                raise ValueError(f"Missing results for {algorithm}/{input_type}.")
            axis.plot(
                sizes,
                [summary[algorithm, input_type, n][metric] for n in sizes],
                label=f"{LABELS[algorithm]}, {input_type}",
                color=COLORS[algorithm],
                linestyle=STYLES[input_type],
                marker=MARKERS[input_type],
                markersize=4,
                linewidth=1.6,
            )
    axis.set_xlabel("Input length n (characters)")
    axis.grid(True, which="major", alpha=0.25)
    axis.legend(fontsize=8, frameon=True)


def finish(figure, destination):
    figure.tight_layout(rect=(0, 0, 1, 0.93), w_pad=2)
    figure.savefig(destination, dpi=200, facecolor="white")
    plt.close(figure)


def create_plots(summary, output):
    output.mkdir(parents=True, exist_ok=True)
    plt.rcParams.update({"font.size": 9, "axes.titlesize": 10, "axes.labelsize": 9})

    figure, axes = plt.subplots(1, 2, figsize=(10.4, 3.25))
    figure.suptitle("Recursive edit distance: repeated work grows rapidly", fontsize=12)
    draw_series(axes[0], summary, ["recursive"], ["disjoint", "random"], "calls")
    axes[0].set_title("Measured recursive calls")
    axes[0].set_ylabel("Recursive entries (calls, logarithmic scale)")
    axes[0].set_yscale("log")
    draw_series(axes[1], summary, ["recursive"], ["disjoint", "random"], "time_ms")
    axes[1].set_title("Median of five runs")
    axes[1].set_ylabel("Elapsed time (ms, logarithmic scale)")
    axes[1].set_yscale("log")
    finish(figure, output / "recursive.png")

    figure, axes = plt.subplots(1, 2, figsize=(10.4, 3.25))
    figure.suptitle("Bottom-up edit distance: full table and two rows", fontsize=12)
    sizes = sorted(n for name, kind, n in summary if name == "table" and kind == "disjoint")
    cell_counts = []
    for n in sizes:
        counts = {
            summary[algorithm, input_type, n]["cells"]
            for algorithm in ["table", "two_rows"]
            for input_type in ["disjoint", "random"]
        }
        if len(counts) != 1:
            raise ValueError("DP update counts differ between algorithms or inputs.")
        cell_counts.append(counts.pop())
    axes[0].plot(sizes, cell_counts, "o-", color="#2454a4", markersize=4,
                 label="Both algorithms, both input types")
    axes[0].set_title("All four count series coincide")
    axes[0].set_xlabel("Input length n (characters)")
    axes[0].set_ylabel("Interior table updates (cells)")
    axes[0].set_xscale("log", base=2)
    axes[0].set_yscale("log")
    axes[0].grid(True, alpha=0.25)
    axes[0].legend(fontsize=8)
    draw_series(axes[1], summary, ["table", "two_rows"], ["disjoint", "random"], "time_ms")
    axes[1].set_title("Median of five runs")
    axes[1].set_ylabel("Elapsed time (ms)")
    axes[1].set_xscale("log", base=2)
    axes[1].set_yscale("log")
    finish(figure, output / "dynamic_programming.png")

    figure, axes = plt.subplots(1, 2, figsize=(10.4, 3.25))
    figure.suptitle("String search: random and repeated-prefix inputs (pattern length 64)", fontsize=12)
    draw_series(axes[0], summary, ["naive", "kmp"], ["adversarial", "random"], "total_comparisons")
    axes[0].set_title("KMP includes LPS construction")
    axes[0].set_ylabel("Character comparisons (count, logarithmic scale)")
    axes[0].set_yscale("log")
    draw_series(axes[1], summary, ["naive", "kmp"], ["adversarial", "random"], "time_ms")
    axes[1].set_title("Median of five runs")
    axes[1].set_ylabel("Elapsed time (ms, logarithmic scale)")
    axes[1].set_yscale("log")
    finish(figure, output / "string_search.png")


def main():
    project = Path(__file__).resolve().parents[1]
    parser = argparse.ArgumentParser(description="Draw the assignment's three experiment charts.")
    parser.add_argument("csv", nargs="?", type=Path, default=project / "results" / "results.csv")
    parser.add_argument("--output", type=Path, default=project / "results" / "plots")
    args = parser.parse_args()
    create_plots(load_results(args.csv), args.output)
    print(f"Saved three charts to {args.output}")


if __name__ == "__main__":
    main()
