# In which situations is HashSet or TreeSet the most suitable Set implementation in Java?

**Date:** 20-11-2025  
**Name:** Afnaan Makhloufi  
**Student number:** 500944485

---

## Inhoudsopgave

1. [Need analysis](#1-need-analysis)
2. [Research questions](#2-research-questions)
3. [How do HashSet and TreeSet differ in their internal data structures and ordering behavior?](#3-how-do-hashset-and-treeset-differ-in-their-internal-data-structures-and-ordering-behavior)
    - [3.1 What is a HashSet?](#31-what-is-a-hashset)
    - [3.2 What is a TreeSet?](#32-what-is-a-treeset)
4. [How do these implementations compare in performance during operations such as insertion, lookup, and deletion?](#4-how-do-these-implementations-compare-in-performance-during-operations-such-as-insertion-lookup-and-deletion)
5. [How does the memory usage of HashSet and TreeSet differ, and what explains this difference?](#5-how-does-the-memory-usage-of-hashset-and-treeset-differ-and-what-explains-this-difference)
6. [Conclusion](#6-conclusion)
7. [Sources](#7-sources)

---

## 1. Need analysis

In software development, choosing the right data structure is important for building applications that are maintainable and efficient in behavior. Although Java provides a unified Set interface for storing unique elements like HashSet and TreeSet. They work in different ways. These differences are not always clear to beginning developers, and choosing an unfit implementation can cause unnecessary performance costs, uncertain ordering or higher memory usage.

As applications grow in complexity, developers rely on Sets for tasks such as storing unique identifiers, managing caches or processing user input. Understanding how each Set implementation works internally is therefor important, because their mechanisms such as hash tables, linked lists and balanced trees directly impact execution speed, memory consumption and the way elements are ordered. Without the knowledge, developers might make design decisions that negatively impact the efficiency of their applications.

For this reason, it is beneficial to check when each implementation is most effective. Gaining understanding into differences helps developers make substantial decisions and create software that is both performant and aligned with the requirements of the problem they need to solve.

---

## 2. Research questions

**Main research question**  
In which situations is HashSet or TreeSet the most suitable Set implementation in Java?

**Sub-questions**

1. How do HashSet and TreeSet differ in their internal data structures and ordering behavior?
2. How do these implementations compare in performance during operations such as insertion, lookup, and deletion?
3. How does the memory usage of HashSet and TreeSet differ, and what explains this difference?
4. Conclusion

---

## 3. How do HashSet and TreeSet differ in their internal data structures and ordering behavior?

Set interface is part of the Java Collections Framework and is used to store a collection of unique elements, meaning it does not allow duplicate values. The main purpose of a Set is to ensure that every item appears only once, which makes it better for tasks such as storing IDs, filtering duplicates, or representing collections where uniqueness is important. Java provides several implementations of this interface, each with its own structure and behavior. The two most commonly used are HashSet and TreeSet, which differ in how they store, order and process elements.

### 3.1 What is a HashSet?

HashSet is a frequently used implementation of the Set interface in Java. It stores elements in a way that makes adding, removing and searching values quick. The key difference of a HashSet is that is uses a hash table internally. Each element is converted into a number called a hash code, which determines the bucket in which the element is stored. Because this process allows almost direct access to the element’s location, a HashSet can usually preform its operations in constant time on average.

HashSet does not maintain order, so the sequence in which elements appear may seem random and can change. For situations where predictable ordering is needed, java provides LinkedHashSet, a variant of HashSet that keeps elements in their insertion order by maintaining a linked structure alongside the hash table.

HashSet is most useful when performance is more important than ordering and when duplicates should be avoided. For example, when you’re storing unique IDs, tracking visited items or removing duplicates from a list. Due to its simplicity and speed, HashSet is often the default choice when developers need a Set in Java.

### 3.2 What is a TreeSet?

A TreeSet only stores unique elements, but unlike HashSset, keep them in a sorted order. TreeSet implements the sortedSet and internally it uses a Red-Black Tree, which is a type of self-balancing binary search tree. This structure ensures that the elements are always kept in ascending order, based on their natural ordering or a custom comparator.

Because TreeSet must maintain sorting at all times, operations like adding, searching and removing elements take logarithmic time, which is slower than the average contact time of a HashSet. The advantage of a TreeSet is that it provides a reliable ordering and supports additional navigation methods, like retrieving the smallest or biggest element.

TreeSet is used when sorted data is important, such as managing and ordered lists, generating alphabetical or numerical outputs, or when the ability t quickly find elements near a given value is useful. Even though it’s slower than HashSet, TreeSet is the preferred choice in scenarios when ordering is a must.

---

## 4. How do these implementations compare in performance during operations such as insertion, lookup, and deletion?

To compare the performance of HashSet en TreeSet, a benchmark was conducted in which one million integer elements were inserted, searched, and removed. The results clearly show that the internal structure of each implementation has a strong influence on execution speed.

**Insertion performance**  
HashSet completed the insertion of one million elements noticeably faster than TreeSet. This is consistent with the theory: HashSet uses a hash table and can place elements in constant time on average, because it can directly jump to the correct bucket using the hash code. TreeSet, on the other hand, relies on a Red-Black Tree. Every time an element is inserted, the tree must be updated and possibly rebalanced to maintain its sorted structure. This takes logarithmic time per operation and leads to a higher total insertion time.

**Lookup performance**  
For lookup operations, both implementations performed very well in the benchmark, with times in the microsecond range per test. HashSet was slightly faster on average, which matches its expected constant-time lookup in the average case. TreeSet was somewhat slower, because it has to traverse the tree from the root down to the correct node. However, the difference in lookup time was relatively small compared to the difference in insertion time. At this scale, factors such as CPU caching and JVM optimisations also play a role, which means that the theoretical advantage of HashSet does not always translate into a very large visible gap for individual lookups.

**Deletion performance**  
When removing elements, HashSet again performed faster overall than TreeSet in the benchmark. Removing an element from a hash table is usually a constant-time operation, because the implementation only needs to find the correct bucket and adjust its internal data. In a TreeSet, removal requires a search through the tree followed by structural changes to keep the tree balanced. This also leads to logarithmic time per operation. In practice, this means that TreeSet pays an additional performance cost to maintain its sorted structure, while HashSet focuses purely on fast membership and update operations.

Overall, the measurements confirm the theoretical expectations: HashSet offers better raw performance for insertion, lookup and deletion, while TreeSet trades speed for ordering guarantees.

---

## 5. How does the memory usage of HashSet and TreeSet differ, and what explains this difference?

In the same benchmark, the memory usage of HashSet and TreeSet was measured by checking the used heap space before and after inserting one million elements. The results showed that HashSet uses more memory than TreeSet for the same data.

This difference can be explained by their internal structure. HashSet is based on a hash table, which relies on an underlying array of buckets that is often larger than the number of stored elements. This over-allocation, combined with extra objects for handling collisions, increases memory usage. TreeSet, on the other hand, stores elements as nodes in a Red-Black Tree. Each node contains a value and a few references, but there is no large backing array. As a result, the memory footprint of TreeSet grows more directly with the number of elements, which made it more memory-efficient than HashSet in this test.

---

## 6. Conclusion

HashSet is the most suitable choice when an application is in need of high performance for adding, searching and removing elements, and when maintaining the order is not necessary. It is ideal for sets of unique identifiers and any situation where speed is a must.

TreeSet is the most suitable choice when an application requires sorted data and benefits from operations such as retrieving the smallest or largest element or working with ranges. In these scenarios the extra cost in execution time is justified by the built-in ordering and the ability to navigate through the set.

---

## 7. Sources

Baeldung, & Baeldung. (2024, January 8). HashSet and TreeSet Comparison \| Baeldung. *Baeldung on Kotlin*.  
https://www.baeldung.com/java-hashset-vs-treeset

Easy, S. (2025, October 16). Collection hierarchy in Java. *Scientech Easy*.  
https://www.scientecheasy.com/2020/09/collection-hierarchy-in-java.html/

GeeksforGeeks. (2025, November 18). Set in Java. *GeeksforGeeks*.  
https://www.geeksforgeeks.org/java/set-in-java/

Set (Java Platform SE 8 ). (2025, October 20).  
https://docs.oracle.com/javase/8/docs/api/java/util/Set.html

www.naukri.com. (n.d.-a). Code 360 by Coding Ninjas. 2024 Naukri.com.  
https://www.naukri.com/code360/library/what-is-the-difference-between-hashset-and-treeset  
