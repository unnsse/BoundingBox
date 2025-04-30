# Bounding Box

## Requirements
This console app takes input from stdin with the following properties:
- Input is split into lines delimited by newline characters.
- Every line has the same length.
- Every line consists of an arbitrary sequence of hyphens ("-") and asterisks ("\*").
- The final line of input is terminated by a newline character.

Each character in the input will have coordinates defined by `(line number, character number)`, starting at the top and left. So the first character on the first line will have the coordinates `(1,1)` 
and the fifth character on line 3 will have the coordinates `(3,5)`.

The program should find a box (or boxes) in the input with the following properties:
- The box must be defined by two pairs of coordinates corresponding to its top left and bottom right corners.
- It must be the **minimum bounding box** for some contiguous group of asterisks, with each asterisk in the 
group being horizontally or vertically (but not diagonally) adjacent to each other. A single, detached asterisk 
is considered to be a valid box.
The box should not _strictly_ bound the group, so the coordinates for the box in the following input 
should be `(2,2)(3,3)` not `(1,1)(4,4)`.
    ```
    ----
    -**-
    -**-
    ----
    ```
- It should not overlap (i.e. share any characters with) any other minimum bounding boxes.
- Of all the non-overlapping, minimum bounding boxes in the input, _return the largest by area_.

If any boxes satisfying the conditions can be found in the input, the program should return an exit code
of 0 and, for each box, print a line to stdout with the two pairs of coordinates.

So, given the file “groups.txt” with the following content:
```
**-------***
-*--**--***-
-----***--**
-------***--
```

Running this program manually:
```
> ./bounding-box < groups.txt
```
Outputs:

```
(1,1)(2,2)
```

This is because the larger groups on the right of the input have overlapping bounding boxes, 
so the returned coordinates bound the smaller group on the top left.

---

## Design/Implementation

Chose to use [DSU (Disjoint Set Union) w/ Union Find](https://en.wikipedia.org/wiki/Disjoint_sets) and [Sweep Line](https://en.wikipedia.org/wiki/Sweep_line_algorithm) algorithms,
instead of, the [Depth-first Search (DFS)](https://en.wikipedia.org/wiki/Depth-first_search) due to:

1. `Efficiency in Grouping`
  - DSU's near-constant time per operation _(O(a(N)))_ makes it highly efficient for grouping cells, especially in sparse grids where many cells are not asterisks.
  - DFS, while _O(R⋅C)_, requires explicit traversal and can be slower due to recursive overhead or iterative queue management.

2. `Modularity`
  - DSU separates the grouping phase (union operations) from the bounds computation (updating min and max), making the code more modular and easier to maintain.
  - DFS combines exploration and bounds tracking, which can make the code less clean and harder to modify.

3. `Dynamic Updates`
  - DSU's merge operations for bounds (min and max) are efficient and straightforward, allowing easy tracking of bounding box coordinates.
  - DFS requires tracking min/max coordinates during traversal, which adds complexity and may require additional data structures.

4. `Scalability`
  - DSU scales well for large grids due to its amortized constant-time operations and lack of recursive overhead.
  - DFS may face stack overflow for very large grids (in recursive implementations) or require careful management in iterative versions.

5. `Code Simplicity`
  - DSU's iterative nature and use of maps make it concise for this problem, especially with the merge method for bounds.
  - DFS requires explicit traversal logic, which can be more verbose and error-prone when tracking additional properties like bounds.

In conclusion, `BoundingBox` code efficiently solves the problem using DSU to group contiguous asterisks and a sweep line algorithm 
to find non-overlapping bounding boxes. The time complexity is approximately _O(R⋅C + K^2)_
, and the space complexity is _O(R⋅C)_. DSU is preferred over DFS due to its efficiency, modularity, and 
ease of tracking bounding box coordinates, making it a better fit for this problem's requirements. Sweep Line algorithm finds non-overlapping bounding boxes by processing boxes in order of their x-coordinates.

---

## Technical Requirements

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

Locate `BoundingBox/app/build/libs/bounding-jar` and invoke the following command:

`./bounding-box < groups.txt` (the data input files are located inside `BoundingBox/app/src/main/resources`)

### Run via GitHub Actions and download the generated artifact file. 

1. Run via [GitHub Actions](https://github.com/unnsse/BoundingBox/actions) CI/CD

2. Once downloaded, unzip the artifact:

```bash
unzip bounding-box.zip
```

3. Test data using `stdin`

```
./bounding-box < groups.txt
(1,1)(2,2)
```

Here's the link to the first successful [run](https://github.com/unnsse/BoundingBox/actions/runs/14742728397).