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
    }

    static class DisjointSet {
        Map<Integer, Integer> parent = new HashMap<>();
        Map<Integer, Point> min = new HashMap<>();
        Map<Integer, Point> max = new HashMap<>();

        int find(int p) {
            if (!parent.containsKey(p)) {
                parent.put(p, p);
                return p;
            }
            if (parent.get(p) != p) {
                parent.put(p, find(parent.get(p)));
            }
            return parent.get(p);
        }

        void union(int p1, int p2) {
            int r1 = find(p1), r2 = find(p2);
            if (r1 != r2) {
                parent.put(r2, r1);
            }
        }

        void updateBounds(int root, int x, int y) {
            min.merge(root, new Point(x, y),
                    (v1, v2) -> new Point(Math.min(v1.x(), v2.x()), Math.min(v1.y(), v2.y())));
            max.merge(root, new Point(x, y),
                    (v1, v2) -> new Point(Math.max(v1.x(), v2.x()), Math.max(v1.y(), v2.y())));
        }
    }

    record Event(int x, int type, Box box) implements Comparable<Event> {
        @Override
        public int compareTo(Event other) {
            return x != other.x ? Integer.compare(x, other.x) : Integer.compare(type, other.type);
        }
    }

    public String largestNonOverlappingBox(List<String> lines) {
        if (lines == null || lines.isEmpty() || lines.getFirst().isEmpty()) {
            return "";
        }

        int rows = lines.size(), cols = lines.getFirst().length();
        if (!lines.stream().allMatch(line -> line.length() == cols)) {
            return "";
        }

        DisjointSet ds = new DisjointSet();

        // Step 1: Perform union operations for contiguous asterisks
        IntStream.range(0, rows * cols)
                .filter(p -> lines.get(p / cols).charAt(p % cols) == '*')
                .forEach(p -> unionCell(p, rows, cols, lines, ds));

        // Step 2: Compute bounds for each group
        IntStream.range(0, rows * cols)
                .filter(p -> lines.get(p / cols).charAt(p % cols) == '*')
                .forEach(p -> {
                    int i = p / cols, j = p % cols;
                    ds.updateBounds(ds.find(p), i + 1, j + 1);
                });

        // Step 3: Create minimum bounding boxes
        var boxes = ds.min.keySet().stream()
                .map(root -> new Box(ds.min.get(root), ds.max.get(root)))
                .toList();

        if (boxes.isEmpty()) {
            return "";
        }

        // Step 4: Find non-overlapping boxes using sweep line
        var events = boxes.stream()
                .flatMap(box -> Stream.of(
                        new Event(box.topLeft().x(), 1, box),
                        new Event(box.bottomRight().x() + 1, -1, box)
                ))
                .sorted()
                .toList();

        var nonOverlapping = new ArrayList<Box>();
        processEvents(events, 0, new TreeSet<>(Comparator.comparing((Box b) -> b.topLeft().y())
                .thenComparing(b -> b.topLeft().x())), nonOverlapping);

        // Step 5: Select the box with smallest top-left coordinates
        return nonOverlapping.stream()
                .min(Comparator.comparing((Box b) -> b.topLeft().x())
                        .thenComparing(b -> b.topLeft().y())
                        .thenComparingLong(Box::area))
                .map(Box::toString)
                .orElse("");
    }

    private void unionCell(int p,
                           int rows,
                           int cols,
                           List<String> lines,
                           DisjointSet ds) {
        int i = p / cols, j = p % cols;

        // Connect to adjacent asterisk cells
        if (i > 0 && lines.get(i - 1).charAt(j) == '*') {
            // Above
            ds.union(p, (i - 1) * cols + j);
        }
        if (i < rows - 1 && lines.get(i + 1).charAt(j) == '*') {
            // Below
            ds.union(p, (i + 1) * cols + j);
        }
        if (j > 0 && lines.get(i).charAt(j - 1) == '*') {
            // Left
            ds.union(p, i * cols + (j - 1));
        }
        if (j < cols - 1 && lines.get(i).charAt(j + 1) == '*') {
            // Right
            ds.union(p, i * cols + (j + 1));
        }
    }

    private void processEvents(List<Event> events,
                               int index,
                               TreeSet<Box> active,
                               List<Box> nonOverlapping) {
        if (index >= events.size()) {
            nonOverlapping.addAll(active);
            return;
        }
        var e = events.get(index);
        if (e.type() == 1) {
            // Add box to active set without overlap check to collect all potential boxes
            active.add(e.box());
        } else {
            active.remove(e.box());
            // Check if the removed box is non-overlapping with remaining active boxes
            boolean overlaps = active.stream().anyMatch(activeBox ->
                    !(e.box().bottomRight().x() < activeBox.topLeft().x() ||
                            e.box().topLeft().x() > activeBox.bottomRight().x() ||
                            e.box().bottomRight().y() < activeBox.topLeft().y() ||
                            e.box().topLeft().y() > activeBox.bottomRight().y()));
            if (!overlaps) {
                nonOverlapping.add(e.box());
            }
        }
        processEvents(events, index + 1, active, nonOverlapping);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            System.err.println("Usage: ./bounding-box < input.txt");
            System.exit(1);
        }

        List<String> lines = new BufferedReader(new InputStreamReader(System.in))
                .lines()
                .collect(Collectors.toList());

        String result = new BoundingBox().largestNonOverlappingBox(lines);
        System.out.println(result);
    }
}