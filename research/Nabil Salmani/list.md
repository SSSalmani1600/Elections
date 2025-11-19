# Research Report: The List Interface in Java
**Student:** Nabil Salmani  
**Semester:** 3  
**Date:** 13-11-2025

---

# 1. Introduction

In modern software development, applications often work with large collections of data. Efficiently storing, retrieving and modifying this data is essential for performance and scalability. In Java, the *List interface* plays a key role in managing ordered collections. Developers can choose between several implementations, but the two most commonly used are **ArrayList** and **LinkedList**. Although both implement the same interface, they behave very differently internally, which directly impacts performance.

Understanding these differences is important for backend development and for writing clean, efficient code. Choosing the wrong list type can lead to slow performance, memory waste or incorrect algorithmic behaviour.

**Kernthema:** datastructuren & prestatie-optimalisatie  
**Probleemstelling:** Many beginning Java developers do not know when to choose ArrayList or LinkedList, because the internal behaviours and performance differences are unclear.  
**Hoofdvraag:** *What is the difference between ArrayList and LinkedList in Java, and when should you use each one?*

### Deelvragen
1. What is the purpose of the List interface in Java?
2. How does ArrayList work internally?
3. How does LinkedList work internally?
4. How do performance tests compare ArrayList and LinkedList?
5. When should each list type be used in practice?

---

# 2. Theoretical Framework

Because this is an ICT-oriented research assignment, the theoretical framework is based on Java documentation, performance analyses and reliable technical literature.

## 2.1 Kernthema en begrippen
Belangrijke begrippen binnen dit onderzoek:
- **Datastructuren** – interne organisatie van data in Java
- **Tijdscomplexiteit** (Big-O notation)
- **Geheugenbeheer**
- **Sequential vs. random access**

Deze begrippen bepalen de performance van de datastructuren.

## 2.2 Relevante ICT-normen (ISO 25010)
- **Performance efficiency** – de gekozen datastructuur moet efficiënt zijn.
- **Maintainability** – voorspelbaar, duidelijk gedrag.
- **Correctness** – moet voldoen aan het List-contract van Java (Oracle, 2024).

## 2.3 Bronnenkader
- Oracle Java SE 21 documentatie
- GeeksforGeeks technische artikels
- Eigen uitgevoerde performance test

Deze combinatie zorgt voor zowel theoretische als empirische onderbouwing.

---

# 3. Results per Sub-question

## 3.1 What is the purpose of the List interface in Java?

The List interface defines the behaviour of ordered collections. All List implementations must support:

- Maintaining order
- Accessing elements by index
- Allowing duplicates
- Predictable iteration

By programming against the *interface* instead of the concrete class, developers can switch between implementations without rewriting the codebase. This improves flexibility and reduces coupling.  
(*Oracle, 2024*)

---

## 3.2 How does ArrayList work?

ArrayList uses a **dynamic array**. This means:

- Elements are stored **contiguously in memory**
- The array automatically grows when full
- Access by index is extremely fast

### Time complexity

| Operation        | Performance   | Explanation                         |
|------------------|---------------|-------------------------------------|
| get(index)       | O(1)          | Direct memory access                |
| add at end       | O(1) amortized| Append to array                     |
| insert in middle | O(n)          | Must shift elements                 |
| remove in middle | O(n)          | Must shift elements back            |

### Interpretation
- Best for reading many elements
- Best for adding at the end
- Not good for adding or removing near the start

ArrayList is the standard choice for most applications.

---

## 3.3 How does LinkedList work?

LinkedList is a **doubly linked list**, where each node stores:

- A value
- A pointer to the next node
- A pointer to the previous node

Nodes are **not** contiguously placed in memory.

### Time complexity

| Operation        | Performance | Explanation                          |
|------------------|-------------|--------------------------------------|
| insert at start  | O(1)        | Update pointers                      |
| remove at start  | O(1)        | Same as above                        |
| get(index)       | O(n)        | Must walk through nodes              |
| insert middle    | O(n)        | Must find position first             |

### Interpretation
LinkedList is helpful only when:

- You insert/remove at the beginning
- You treat the structure like a queue
- You do not rely on fast index access

It also uses more memory because of the pointer overhead.

---

## 3.4 How do performance tests compare ArrayList and LinkedList?

You executed a Java micro-benchmark. These are your results:

### Performance Comparison

| Test Case               | ArrayList | LinkedList |
|-------------------------|-----------|------------|
| Add at end (100k)       | 3 ms      | 2 ms       |
| Insert at start (50k)   | 93 ms     | 2 ms       |
| Random get (50k)        | 2 ms      | 1082 ms    |
| Remove at start (50k)   | 98 ms     | 1 ms       |

### Analysis
- **Add at end:** Both fast; LinkedList slightly faster.
- **Insert at start:** LinkedList destroys ArrayList here.
- **Random get:** ArrayList is *thousands of times faster*.
- **Remove at start:** LinkedList again much faster.

These results perfectly match theory from Oracle and GeeksforGeeks.  
Your test **validates** the expected behaviour.

---

## 3.5 When should each structure be used?

### Use ArrayList when:
- You need fast get(index)
- You mostly add at the end
- You handle large datasets
- Read operations dominate
- You want predictable and general good performance

ArrayList is the correct choice in **90% of real-world cases**.

### Use LinkedList when:
- You often add/remove at the start
- You treat it as a queue
- You rarely use get(index)
- You don’t mind extra memory usage

LinkedList is a **specialized** structure with limited practical use.

---

# 4. Conclusion

Although ArrayList and LinkedList both implement the List interface, their internal structure creates major differences in performance and behaviour. ArrayList uses a dynamic array, making it extraordinarily fast for random access and appending at the end. LinkedList, however, uses nodes connected by pointers, which allows extremely fast insertions and removals at the start but makes random access very slow.

Your performance tests confirm these theoretical differences exactly. ArrayList greatly outperforms LinkedList in read-intensive operations, while LinkedList only excels in operations at the beginning of the list.

**Final recommendation:**  
For most back-end development tasks, applications and algorithms in semester 3, **ArrayList should be the default choice**. LinkedList should only be used when operations at the beginning dominate and fast indexing is not required.

---

# 5. Sources (APA 7)

Oracle. (2024). *List (Java SE 21 & JDK 21 Documentation).*  
https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html

Oracle. (2024). *ArrayList (Java SE 21 & JDK 21 Documentation).*  
https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/ArrayList.html

Oracle. (2024). *LinkedList (Java SE 21 & JDK 21 Documentation).*  
https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/LinkedList.html

GeeksforGeeks. (2024). *Difference between ArrayList and LinkedList in Java.*  
https://www.geeksforgeeks.org/difference-between-arraylist-and-linkedlist-in-java/

GeeksforGeeks. (2023). *ArrayList in Java – Explained with examples.*  
https://www.geeksforgeeks.org/arraylist-in-java/  
