package com.example;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class BoundingBoxTest {

    private List<String> readInput(String resourceFileName) throws Exception {
        Path path = Path.of(Objects.requireNonNull(getClass().getClassLoader()
                                                             .getResource(resourceFileName))
                                                             .toURI());
        return Files.readAllLines(path)
                    .stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());
    }

    @Test
    void testGroups() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("groups.txt");
        assertEquals("(1,1)(2,2)", boundingBox.largestNonOverlappingBox(lines));
    }

    @Test
    void testSingleRow() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("single-row.txt");
        assertEquals("(1,1)(1,2)", boundingBox.largestNonOverlappingBox(lines));
    }

    @Test
    void testSingleGroup() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("single-group.txt");
        assertEquals("(2,2)(3,3)", boundingBox.largestNonOverlappingBox(lines));
    }

    @Test
    void testSingleBox() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("single-box.txt");
        assertEquals("(2,3)(2,3)", boundingBox.largestNonOverlappingBox(lines));
    }

    @Test
    void testNoBoxes() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("no-boxes.txt");
        assertEquals("", boundingBox.largestNonOverlappingBox(lines));
    }

    @Test
    void testEmptyBox() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("empty-box.txt");
        assertEquals("", boundingBox.largestNonOverlappingBox(lines));
    }

    @Test
    void testMultipleNonOverlapping() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("multiple-non-overlapping.txt");
        assertEquals("(1,1)(2,2)", boundingBox.largestNonOverlappingBox(lines));
    }

    @Test
    void testSingleColumn() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("single-column.txt");
        assertEquals("(1,1)(2,1)", boundingBox.largestNonOverlappingBox(lines));
    }

    @Test
    void testWrongLineLengths() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("wrong-line-lengths.txt");
        assertEquals("", boundingBox.largestNonOverlappingBox(lines));
    }

    @Test
    void testComplexOverlappingGroups() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("complex-overlappings.txt");
        assertEquals("(1,1)(2,2)", boundingBox.largestNonOverlappingBox(lines));
    }

    @Test
    void testLargeGridWithSingleGroup() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("single-group.txt");
        assertEquals("(2,2)(3,3)", boundingBox.largestNonOverlappingBox(lines));
    }

    @Test
    void testDisconnectedBoxes() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("disconnected.txt");
        assertEquals("(1,1)(1,1)", boundingBox.largestNonOverlappingBox(lines));
    }

    @Test
    void testBadChar() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("bad_char.txt");
        assertEquals("Error", boundingBox.largestNonOverlappingBox(lines));
    }
}