# Research Report: The List Interface in Java
**Student:** Nabil Salmani  
**Semester:** 3  
**Date:** 17-11-2025

---

## Research Question

**Main Question:**

> What is the difference between `ArrayList` and `LinkedList` in Java, and when should you use each one?

---

## Sub-question 1
### *What is the purpose of the List interface in Java?*

The **List** interface is a basic idea in Java for working with lists of items.  
A List:

- keeps items in order
- lets you have the same item more than one time
- lets you get items by number (index)

Java has different types of Lists, like **ArrayList** and **LinkedList**, but they all follow the rules of the List interface.

This is good because you can write your code using `List`, and later choose the best type of list without changing your whole program.

(Source: Oracle, 2024)

---

## Sub-question 2
### *How does ArrayList work?*

`ArrayList` uses a **dynamic array** inside.  
This means all items are stored next to each other in memory.

When the array is full, Java makes a bigger one and copies everything inside.

Here are the time costs (Oracle, GfG):

| Operation        | Speed      | Why |
|------------------|------------|-----|
| `get(index)`     | Very fast  | Direct access in array |
| Add at end       | Fast       | Only put item at end |
| Insert in middle | Slow       | Must move many items |
| Remove in middle | Slow       | Must move items back |

### Easy explanation
- ArrayList is best when you **read** items a lot
- ArrayList is best when you **add at the end**
- Not good when you add/remove at the **start or middle**

---

## Sub-question 3
### *How does LinkedList work?*

`LinkedList` uses **nodes**.  
Each node has:

- a value
- a pointer to the next node
- a pointer to the previous node

The nodes are **not** next to each other in memory.

Here are the time costs:

| Operation        | Speed         | Why |
|------------------|---------------|-----|
| Insert at start  | Very fast     | Only update pointers |
| Remove at start  | Very fast     | Same as above |
| `get(index)`     | Slow          | Must walk through nodes |
| Insert middle    | Slow          | Must find position first |

### Easy explanation
LinkedList is only good when:

- You add/remove at the **start**
- You use it like a **queue**
- You do **not** need fast `get(index)`

---

## Sub-question 4
### *How do my tests compare ArrayList and LinkedList?*

Here is the table for your test results:

| Test Case               | ArrayList | LinkedList |
|-------------------------|-----------|------------|
| Add at end (100k)       | 3 ms      | 2 ms       |
| Insert at start (50k)   | 93 ms     | 2 ms       |
| Random get (50k)        | 2 ms      | 1082 ms    |
| Remove at start (50k)   | 98 ms     | 1 ms       |


### What we expect:
- ArrayList = **faster** for reading items and adding at end
- LinkedList = **faster** for adding/removing at start
- LinkedList = **much slower** for getting items by index

You can fill in your numbers later.

---

## Sub-question 5
### *Which one should you use?*

### Use **ArrayList** when:
- You need fast `get(index)`
- You mostly add at the end
- You work with many items
- You want good performance in general

### Use **LinkedList** when:
- You add/remove at the **start** very often
- You don’t need fast access by index
- You use it like a queue

In most programs, **ArrayList is the best choice**.

LinkedList is only good in special cases.

(Source: Oracle & GeeksforGeeks)

---

## Conclusion

`ArrayList` and `LinkedList` are both Lists, but they work in very different ways.

- **ArrayList** is faster for most things, especially reading and adding at the end.
- **LinkedList** is only faster when adding or removing at the start.

Because most apps need fast access by index and simple adding at the end, **ArrayList is usually the better choice**.

---

## Sources (APA 7)

Oracle. (2024). *List (Java SE 21 & JDK 21 Documentation).* https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html

Oracle. (2024). *ArrayList (Java SE 21 & JDK 21 Documentation).* https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/ArrayList.html

Oracle. (2024). *LinkedList (Java SE 21 & JDK 21 Documentation).* https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/LinkedList.html

GeeksforGeeks. (2024). *Difference between ArrayList and LinkedList in Java.* https://www.geeksforgeeks.org/difference-between-arraylist-and-linkedlist-in-java/

GeeksforGeeks. (2023). *ArrayList in Java – Explained with examples.* https://www.geeksforgeeks.org/arraylist-in-java/  
