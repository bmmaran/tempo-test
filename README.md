# Tempo Test

This submission for take-home coding assignment from Tempo. Assignment instructions are here:
https://gist.github.com/hdamoo/d2a6c4026ebe0deaf7b4ceaa3bc5c6c1

## Task 1
Task Files:
1. `app\src\main\java\tempo\test\HierarchyFilter.java` - the implementation of the filter method
2. `app\src\test\java\tempo\test\HierarchyFilterTest.java` - unit tests for the above class

Other stuff:
* Supporting classes are in `app\src\main\java\tempo\test` package
* Created additional test classes in `app\src\test\java\tempo\test\` to have 100% line/branch code coverage.
* It is a Gradle project. The project tempates are auto-created by VS Code. The tests runs within VS Code.

**Assumptions Made**
* I am not expected to implement any other implementation of the `Hierarchy` interface.
* I am not expected to make any other changes to the code than implement the `filter()` method and write unit tests for it.

**Translating the Condition**

Given: `A node is present in the filtered hierarchy iff its node ID passes the predicate and all of its ancestors pass it as well.`

Translated: `if a node ID doesn't pass the predicate, the subtree starting from that node should be pruned`


## Task 2
Task file: `SimpleCache.md` (at the root directory)

**Assumptions Made**
* I am not expected to fix the code, only review it
* I am only expected to identify the issues, not expected to provide a detailed idea on how to fix them
* I am not expected to propose a new solution replacing the currently used `ConcurrentHashMap`
