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

//    @Test
//    void testGroups() throws Exception {
//        BoundingBox boundingBox = new BoundingBox();
//        List<String> lines = readInput("groups.txt");
//        assertEquals("(1,1)(2,2)", boundingBox.largestNonOverlappingBox(lines, false));
//    }

    @Test
    void testSingleRow() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("single_row.txt");
        assertEquals("(1,5)(1,7)", boundingBox.largestNonOverlappingBox(lines, false));
    }

    @Test
    void testSingleGroup() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("single-group.txt");
        assertEquals("(2,2)(3,3)", boundingBox.largestNonOverlappingBox(lines, false));
    }

    @Test
    void testSingleBox() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("single-box.txt");
        assertEquals("(2,3)(2,3)", boundingBox.largestNonOverlappingBox(lines, false));
    }

    @Test
    void testNoBoxes() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("no-boxes.txt");
        assertEquals("", boundingBox.largestNonOverlappingBox(lines, false));
    }

    @Test
    void testEmptyBox() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("empty-box.txt");
        assertEquals("", boundingBox.largestNonOverlappingBox(lines, false));
    }

    @Test
    void testMultipleNonOverlapping() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("multiple-non-overlapping.txt");
        assertEquals("(1,1)(2,2)", boundingBox.largestNonOverlappingBox(lines, false));
    }

    @Test
    void testSingleColumn() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("single_col.txt");
        assertEquals("(3,1)(4,1)", boundingBox.largestNonOverlappingBox(lines, false));
    }

//    @Test
//    void testComplexOverlappingGroups() throws Exception {
//        BoundingBox boundingBox = new BoundingBox();
//        List<String> lines = readInput("complex-overlappings.txt");
//        assertEquals("(1,1)(2,2)", boundingBox.largestNonOverlappingBox(lines, false));
//    }

//    @Test
//    void testDisconnectedBoxes() throws Exception {
//        BoundingBox boundingBox = new BoundingBox();
//        List<String> lines = readInput("disconnected.txt");
//        assertEquals("(1,1)(1,1)", boundingBox.largestNonOverlappingBox(lines, false));
//    }

    @Test
    void testBadChar() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("bad_char.txt");
        assertEquals("Error", boundingBox.largestNonOverlappingBox(lines, false));
    }

    @Test
    void testBadLineLength() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("bad_line_length.txt");
        assertEquals("Error", boundingBox.largestNonOverlappingBox(lines, false));
    }

    @Test
    void testBiggestBox() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("biggest_box.txt");
        assertEquals("(5,5)(6,7)", boundingBox.largestNonOverlappingBox(lines, false));
    }

    @Test
    void testDiagonalsDontTouch() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("diagonals_dont_touch.txt");
        assertEquals("(1,1)(1,1)(1,3)(1,3)(2,2)(2,2)(3,1)(3,1)(3,3)(3,3)",
                boundingBox.largestNonOverlappingBox(lines, true));
    }

    @Test
    void testEqualSizes() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("equal_sizes.txt");
        assertEquals("(1,1)(2,2)(3,3)(4,4)",
                boundingBox.largestNonOverlappingBox(lines, true));
    }

    @Test
    void testOverlap() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("overlap.txt");
        assertEquals("", boundingBox.largestNonOverlappingBox(lines, true));
    }

    @Test
    void testValid() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("valid.txt");
        assertEquals("(5,3)(7,4)", boundingBox.largestNonOverlappingBox(lines, true));
    }

    @Test
    void testOverlap2() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("overlap2.txt");
        assertEquals("", boundingBox.largestNonOverlappingBox(lines, true));
    }

    @Test
    void testTripleOverlap() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("triple-overlap.txt");
        assertEquals("", boundingBox.largestNonOverlappingBox(lines, true));
    }

    @Test
    void testVerticalOverlap() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("vertical_overlap.txt");
        assertEquals("(2,2)(3,3)(5,2)(6,3)", boundingBox.largestNonOverlappingBox(lines, true));
    }

    @Test
    void testNoNewline() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("no_newline.txt");
        assertEquals("(5,3)(7,4)", boundingBox.largestNonOverlappingBox(lines, true));
    }

    @Test
    void testDoubleDoubleOverlap() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("double-double-overlap.txt");
        assertEquals("", boundingBox.largestNonOverlappingBox(lines, true));
    }

    @Test
    void testHorizontalOverlap() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("horizontal_overlap.txt");
        assertEquals("(2,2)(3,3)(2,6)(3,7)", boundingBox.largestNonOverlappingBox(lines, true));
    }

    @Test
    void testNested() throws Exception {
        BoundingBox boundingBox = new BoundingBox();
        List<String> lines = readInput("nested.txt");
        assertEquals("", boundingBox.largestNonOverlappingBox(lines, true));
    }
}