# ThreadCheckArray

A Java console program that searches for a subset of integer input values whose
sum equals a user-supplied target. The recursive search is run by two worker
threads, and a boolean result array identifies the values in the first
solution found.

## Requirements

- Java Development Kit (JDK) with `javac` and `java` available on `PATH`

## Build and run

From the project root in Windows PowerShell:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out src\*.java
java -cp out TestThreadCheckArray
```

Enter the number of values, the values themselves, and then the target sum when
prompted. If a solution is found, the program prints the target, input values,
and a `0`/`1` selection row. A `1` at an index means that value is part of the
reported solution; `0` means it is not.

## Recursive subset-sum search

For each input value, a subset either includes it or excludes it. The
`ThreadCheckArray.rec(n, remaining)` method explores these alternatives
recursively for the first `n` positions:

1. Recurse with the current value subtracted from the remaining target
   (include it).
2. If no worker has already reported success, recurse without changing the
   remaining target (exclude it).
3. At the base case, compare the remaining target with the value at the first
   position. A successful branch marks the worker as a winner.

The two workers partition the initial search around the final input value.
`thread1` starts with that value included (subtracting it from the target);
`thread2` starts with it excluded. The branches below each starting choice are
searched recursively. The first worker to find a solution sets the shared
success flag, which tells the other worker to stop when it next checks that
flag. Once a worker has a result, it publishes its boolean selection through
`SharedData`.

### Synchronization

The shared `SharedData` object is used as the monitor for reading and updating
the shared success flag, for obtaining the initial input and target in the
worker constructor, and for publishing the winning selection. This coordinates
access to the shared search state and provides the other worker a stop signal.
The per-worker search flag and selection array are local to each
`ThreadCheckArray` instance. The console program waits for both workers with
`join()` before reading and printing the result.

The exhaustive search has exponential worst-case time complexity,
`O(2^n)`, and recursion depth up to `O(n)`, where `n` is the input size.
Parallel execution can divide the work, but does not change the worst-case
complexity. The current implementation expects at least one input value.

## Data structures

The sequence field named `array` in `SharedData` and `ThreadCheckArray` is
stored as an {@code ArrayList<Integer>}. `SharedData` copies the constructor's
`int[]` into its list, and each worker creates its own list copy from the
shared input. Its `getArray()` method returns an `int[]` snapshot to preserve
the existing interface used by the console driver.

The other arrays have not been converted: input and compatibility snapshots
remain `int[]`, and solution selections remain `boolean[]`. The solution
arrays are initialized with the list's `size()` so there is one selection
indicator per input value.

## Code documentation

The core classes and their fields, constructors, and methods include Javadoc
describing their roles, parameters, return values, and search behavior.
Generic type names in documentation are wrapped with `{@code ...}` or written
as valid Javadoc links, such as `{@link ArrayList}`, to avoid accidental
unknown-tag errors. Generate API documentation from the project root with:

```powershell
javadoc -d docs src\*.java
```

## Authors and Git collaboration

- **Project author / contributor:** Noga Feldman. The repository Git history
  identifies Noga Feldman as the author of the project commits.
- **Reviewers and additional contributors:** None are identified in the
  repository history at the time this README was written.

For collaboration, contributors can work on separate feature branches, submit
changes for review before merging, and keep commits focused on a single change.
Reviewers should verify the recursive search behavior, shared-state
synchronization, and that only the intended array types are changed.

## License

This project is designated for the **Braude College Academic License**.
The license text and its specific terms are not included in this repository;
refer to the applicable Braude College/course license terms for permissions,
conditions, and restrictions.
