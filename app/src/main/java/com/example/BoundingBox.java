package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.*;

public class BoundingBox {

    record Point(int x, int y) {
        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    record Box(Point topLeft, Point bottomRight) {
        long area() {
            return (long)(bottomRight.x() - topLeft.x() + 1) * (bottomRight.y() - topLeft.y() + 1);
        }

        @Override
        public String toString() {
            return topLeft.toString() + bottomRight.toString();
        }

        boolean overlaps(Box other) {
            return !(bottomRight.x() < other.topLeft.x() ||
                    topLeft.x() > other.bottomRight.x() ||
                    bottomRight.y() < other.topLeft.y() ||
                    topLeft.y() > other.bottomRight.y());
        }
    }

    static class DisjointSet {
        Map<Integer, Integer> parent = new HashMap<>();
        Map<Integer, Point> min = new HashMap<>();
        Map<Integer, Point> max = new HashMap<>();

        int find(int p) {
            parent.putIfAbsent(p, p);
            if (parent.get(p) != p) {
                parent.put(p, find(parent.get(p)));
            }
            return parent.get(p);
        }

        void union(int p1, int p2) {
            int r1 = find(p1), r2 = find(p2);
            if (r1 != r2) parent.put(r2, r1);
        }

        void updateBounds(int root, int x, int y) {
            min.merge(root, new Point(x, y),
                    (v1, v2) -> new Point(Math.min(v1.x(), v2.x()), Math.min(v1.y(), v2.y())));
            max.merge(root, new Point(x, y),
                    (v1, v2) -> new Point(Math.max(v1.x(), v2.x()), Math.max(v1.y(), v2.y())));
        }
    }

    public String largestNonOverlappingBox(List<String> lines, boolean returnAllBoxes) {
        if (lines == null || lines.isEmpty() || lines.get(0).isEmpty()) return "";

        int rows = lines.size(), cols = lines.get(0).length();

        // Validate that all lines have the same length and only contain valid characters (* and -)
        if (!lines.stream().allMatch(l -> l.length() == cols && l.matches("[*-]+"))) {
            return "Error";
        }

        DisjointSet ds = new DisjointSet();

        IntStream.range(0, rows * cols)
                .filter(p -> lines.get(p / cols).charAt(p % cols) == '*')
                .forEach(p -> unionCell(p, rows, cols, lines, ds));

        IntStream.range(0, rows * cols)
                .boxed()
                .sorted(Comparator.naturalOrder())
                .filter(p -> lines.get(p / cols).charAt(p % cols) == '*')
                .forEachOrdered(p -> {
                    int root = ds.find(p);
                    int x = p / cols, y = p % cols;
                    ds.updateBounds(root, x + 1, y + 1); // 1-based indexing
                });

        var boxes = ds.min.keySet().stream()
                .map(root -> new Box(ds.min.get(root), ds.max.get(root)))
                .toList();

        if (boxes.isEmpty()) return "";

        var nonOverlapping = findNonOverlappingBoxes(boxes);

        if (returnAllBoxes) {
            // Return all non-overlapping boxes, or empty string if any overlap exists
            if (nonOverlapping.size() < boxes.size()) return ""; // Overlaps detected
            return nonOverlapping.stream()
                    .sorted(Comparator.comparing((Box b) -> b.topLeft().x())
                            .thenComparing(b -> b.topLeft().y()))
                    .map(Box::toString)
                    .collect(Collectors.joining(""));
        } else {
            // Return the largest non-overlapping box
            return nonOverlapping.stream()
                    .max(Comparator.comparingLong(Box::area)
                            .thenComparing((Box b) -> b.topLeft().x())
                            .thenComparing(b -> b.topLeft().y()))
                    .map(Box::toString)
                    .orElse("");
        }
    }

    private void unionCell(int p, int rows, int cols, List<String> lines, DisjointSet ds) {
        int i = p / cols, j = p % cols;
        if (i > 0 && lines.get(i - 1).charAt(j) == '*') ds.union(p, (i - 1) * cols + j);
        if (i < rows - 1 && lines.get(i + 1).charAt(j) == '*') ds.union(p, (i + 1) * cols + j);
        if (j > 0 && lines.get(i).charAt(j - 1) == '*') ds.union(p, i * cols + (j - 1));
        if (j < cols - 1 && lines.get(i).charAt(j + 1) == '*') ds.union(p, i * cols + (j + 1));
    }

    private List<Box> findNonOverlappingBoxes(List<Box> boxes) {
        // Sort boxes by area (descending) to prioritize larger boxes, then by coordinates
        var sortedBoxes = boxes.stream()
                .sorted(Comparator.comparingLong(Box::area).reversed()
                        .thenComparing((Box b) -> b.topLeft().x())
                        .thenComparing(b -> b.topLeft().y()))
                .toList();

        var nonOverlapping = new ArrayList<Box>();
        var used = new boolean[boxes.size()];

        IntStream.range(0, sortedBoxes.size())
                .filter(i -> !used[i])
                .forEach(i -> {
                    nonOverlapping.add(sortedBoxes.get(i));
                    used[i] = true;
                    // Mark overlapping boxes as used
                    IntStream.range(i + 1, sortedBoxes.size())
                            .filter(j -> !used[j] && sortedBoxes.get(i).overlaps(sortedBoxes.get(j)))
                            .forEach(j -> used[j] = true);
                });

        return nonOverlapping;
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            System.err.println("Usage: ./bounding-box < input.txt");
            System.exit(1);
        }

        List<String> lines = new BufferedReader(new InputStreamReader(System.in))
                .lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());

        String result = new BoundingBox().largestNonOverlappingBox(lines, false); // Default: largest box
        System.out.println(result);
        System.exit(result.equals("Error") ? 1 : 0);
    }
}