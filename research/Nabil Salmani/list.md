# Research Report: The List Interface in Java
**Student:** Nabil Salmani  
**Semester:** 3  
**Date:** 17-11-2025

---
## Research Question

**Main Question:**

> How do the Java `ArrayList` and `LinkedList` implementations of the `List` interface work, and how do they compare in performance and use cases?

---

## Sub-question 1
### *What is the purpose of the `List` interface in Java?*

The `List` interface is part of the Java Collections Framework and represents an **ordered collection** of elements.  
A List:

- keeps elements in the same order you insert them
- allows duplicates
- allows access by index

According to Oracle (2024), List defines methods like:  
`add()`, `get()`, `set()`, `remove()`, and iteration methods.

The `List` interface is important because it lets developers program **against the interface**, not the implementation.  
This means you can switch between `ArrayList` or `LinkedList` without changing most of your code.

_Source: Oracle Docs, 2024; GeeksForGeeks, 2023_

---

## Sub-question 2
### *How does `ArrayList` work internally, and what is its time complexity?*

`ArrayList` uses a **dynamic array** internally.  
This means that elements are stored in a continuous block of memory.

When the internal array becomes full, `ArrayList` creates a **new bigger array** and copies all elements into it.

Time complexities (Oracle, 2024; GeeksForGeeks, 2024):

| Operation               | Complexity | Explanation |
|------------------------|------------|-------------|
| `get(index)`           | O(1)       | Direct array access |
| Add at end             | O(1)*      | Amortized constant time |
| Insert in middle       | O(n)       | Elements must shift right |
| Remove in middle       | O(n)       | Elements must shift left |

This makes `ArrayList` very good for:

- fast random reads
- iterating
- adding elements at the end

But not ideal for:

- inserting/removing at the start
- heavy shifting operations

---

## Sub-question 3
### *How does `LinkedList` work internally, and what is its time complexity?*

`LinkedList` is implemented as a **doubly linked list**.  
Each element is inside a “node” containing:

- the value
- a pointer to the next node
- a pointer to the previous node

This means elements do **not** sit next to each other in memory.

Time complexities (Oracle, 2024; GfG, 2023):

| Operation               | Complexity | Explanation |
|------------------------|------------|-------------|
| Insert at start        | O(1)       | Update pointers only |
| Remove at start        | O(1)       | Same as above |
| Insert in middle       | O(n)       | Need to walk the list first |
| `get(index)`           | O(n)       | No direct access |

LinkedList is useful when:

- You add/remove at the **beginning**
- You treat the list like a **queue**
- You don't need random access

But it's slower when accessing elements by index.

---

## Sub-question 4
### *How do ArrayList and LinkedList compare in performance in my own tests?*

Below is the benchmark table (example layout):

| Test Case                        | ArrayList (ms) | LinkedList (ms) |
|---------------------------------|----------------|------------------|
| Add at end (100k)              |                |                  |
| Insert at start (100k)         |                |                  |
| Random get() (50k operations)  |                |                  |
| Remove at start (50k)          |                |                  |

**Interpretation (what we expect):**

- `ArrayList` will be **much faster** for random access and appending.
- `LinkedList` will be **faster** for inserts/removes at the start.
- `LinkedList` will be **much slower** for random access.

You can fill in your results when your benchmark is ready.

---

## Sub-question 5
### *Which implementation is better for which use case?*

**Use ArrayList when:**

- You read elements often
- You need fast `get(index)`
- You mostly add at the end
- Memory should stay compact

**Use LinkedList when:**

- You add/remove a lot at the start
- You don't need index access
- You implement queues or deques

GeeksForGeeks (2024) confirms that ArrayList is the most common choice, and LinkedList is only optimal in “specific insertion-heavy situations”.

---

## Conclusion

`ArrayList` and `LinkedList` both implement the `List` interface, but they behave very differently.

- `ArrayList` is usually faster for most operations and is the default choice.
- `LinkedList` is only better in cases with many start insertions/removals and little random access.

Based on theory (Oracle, GfG) and expected benchmark results, `ArrayList` is the recommended implementation for most real-world use cases.

---

## Sources (APA 7)

Oracle. (2024). *Java Platform SE Documentation: List, ArrayList, LinkedList*.  
GeeksForGeeks. (2023–2024). *ArrayList vs LinkedList in Java*.  
