# Bounding Box

## Overview

BoundingBox is a Java program that reads a 2D ASCII grid from standard input and detects 
the largest or all non-overlapping bounding boxes enclosing contiguous regions of asterisks (`*`). 
It is designed to handle large inputs efficiently and uses a Disjoint Set (Union-Find) data structure 
to identify connected components.

Each bounding box is defined by the minimum and maximum `x` and `y` coordinates 
(with 1-based indexing) that surround a connected group of `*` characters.

See detailed article: [2D Matrix Bounding Box Checker Using DSU and Union-Find in Java 23 Using TDD](https://unnsse.io/2025/05/boundingbox_05032025)

### Example Input

```txt
*--*
-**-
----
*--*
```
### Output (largest non-overlapping box)

`(1,1)(2,4)`

---

## Features

• Reads input text file containing 2D grid from `stdin`.

• Detects all connected components of `*` characters using Disjoint Set Union-Find (DSU).

• Calculates bounding box coordinates during the find-union phase.

• Computes the minimum bounding box for each component.

• Filters for the largest non-overlapping bounding box.

• Filters and compares boxes using area and coordinates.

• Optionally returns all non-overlapping boxes in sorted order.

• Returns empty string if no valid boxes found.

• Handles malformed input with a clear "Error" output.

• Efficient for large grids (10,000 × 10,000).

See [Requirements.md](Requirements.md) for more details.

---

## Machine / Target Environment

Local / target machine should have the following software installed:

* `jdk 23` specifically:

```bash
java version "23.0.2" 2025-01-21
  Java(TM) SE Runtime Environment (build 23.0.2+7-58)
  Java HotSpot(TM) 64-Bit Server VM (build 23.0.2+7-58, mixed mode, sharing)
```

* `Gradle 8.14`

* `Kotlin 2.0.21` (`build.gradle.kts` DSL relies on a Kotlin distribution configured)

---

## Build Instructions

### To build locally (will build unit tests):

`./gradlew clean build`

Locate `BoundingBox/app/build/libs/bounding-box` and invoke the following command:

`./bounding-box < groups.txt` (the data input files are located inside `BoundingBox/app/src/test/resources`)

---

### Run via GitHub Actions and download the generated Artifact file. 

1. Run via [GitHub Actions](https://github.com/unnsse/BoundingBox/actions) CI/CD

2. Once downloaded, unzip the artifact: `unzip bounding-box.zip`

---

## Usage 

```bash
Usage: ./bounding-box < input.txt
```
To return all non-overlapping bounding boxes:
```java
new BoundingBox().largestNonOverlappingBox(lines, true);

```

---

Feel free to fork and/or add to this!! :smile: :coffee: