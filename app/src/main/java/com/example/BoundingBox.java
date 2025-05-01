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

    record Event(int x, int type, Box box) implements Comparable<Event> {
        @Override
        public int compareTo(Event other) {
            return x != other.x ? Integer.compare(x, other.x) : Integer.compare(type, other.type);
        }
    }

    public String largestNonOverlappingBox(List<String> lines) {
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
                    ds.updateBounds(root, x + 1, y + 1);
                });

        var boxes = ds.min.keySet().stream()
                .map(root -> new Box(ds.min.get(root), ds.max.get(root)))
                .toList();

        if (boxes.isEmpty()) return "";

        var events = boxes.stream()
                .flatMap(box -> Stream.of(
                        new Event(box.topLeft().x(), 1, box),
                        new Event(box.bottomRight().x() + 1, -1, box)))
                .sorted()
                .toList();

        var nonOverlapping = new ArrayList<Box>();

        processEvents(events, 0, new TreeSet<>(Comparator
                .comparing((Box b) -> b.topLeft().y())
                .thenComparing(b -> b.topLeft().x())), nonOverlapping);

        return nonOverlapping.stream()
                .min(Comparator.comparing((Box b) -> b.topLeft().x())
                        .thenComparing(b -> b.topLeft().y())
                        .thenComparingLong(Box::area))
                .map(Box::toString)
                .orElse("");
    }

    private void unionCell(int p, int rows, int cols, List<String> lines, DisjointSet ds) {
        int i = p / cols, j = p % cols;
        if (i > 0 && lines.get(i - 1).charAt(j) == '*') ds.union(p, (i - 1) * cols + j);
        if (i < rows - 1 && lines.get(i + 1).charAt(j) == '*') ds.union(p, (i + 1) * cols + j);
        if (j > 0 && lines.get(i).charAt(j - 1) == '*') ds.union(p, i * cols + (j - 1));
        if (j < cols - 1 && lines.get(i).charAt(j + 1) == '*') ds.union(p, i * cols + (j + 1));
    }

    private void processEvents(List<Event> events, int index,
                               TreeSet<Box> active, List<Box> nonOverlapping) {
        if (index >= events.size()) {
            nonOverlapping.addAll(active);
            return;
        }
        var e = events.get(index);
        if (e.type() == 1) {
            active.add(e.box());
        } else {
            active.remove(e.box());
            boolean overlaps = active.stream().anyMatch(b ->
                    !(e.box().bottomRight().x() < b.topLeft().x() ||
                            e.box().topLeft().x() > b.bottomRight().x() ||
                            e.box().bottomRight().y() < b.topLeft().y() ||
                            e.box().topLeft().y() > b.bottomRight().y()));
            if (!overlaps) nonOverlapping.add(e.box());
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
                                    .map(String::trim)
                                    .filter(line -> !line.isEmpty())
                                    .collect(Collectors.toList());

        String result = new BoundingBox().largestNonOverlappingBox(lines);
        System.out.println(result);
        System.exit(result.equals("Error") ? 1 : 0);
    }
}