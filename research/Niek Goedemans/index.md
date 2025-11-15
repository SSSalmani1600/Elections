# Research Report: The Map Algorithm in Java

**Student:** Niek Goedemans / 500948606  
**Semester:** 3  
**Date:** 12-11-2025

---

## Research question

**Main Question:**

> How do different Java Map implementations like `HashMap`, `LinkedHashMap`, and `TreeMap` work, and how do they compare
> in performance?

**Sub-Questions:**

1. How does a `HashMap` store and find data?
2. What extra features does a `LinkedHashMap` add, and how does that affect speed?
3. How does a `TreeMap` keep keys sorted, and what is the cost of that?
4. Which implementation is the fastest for adding, finding, and removing data?

---

## Introduction

You can think of the **`Map`** interface as a blueprint that defines a set of methods and behaviors which every Map
implementation must follow. For example, every map implementation works with a set of keys. Each key must be unique but
the value of every key can be duplicates. Each implementation of `Map` also has the same methods, like `put`, `remove`,
`get` etc.

But what is the difference between the 3 different most used implementations (**`HashMap`**, **`LinkedHashMap`** and *
*`TreeMap`**)?

### HashMap

`HashMap` is one of the most commonly used implementations of the Map interface in Java. It stores key–value pairs just
like every other implementation, but uses `hashing` to achieve fast lookups.

When you insert a key, Java calls its hashCode() method and uses that hash to create an index in an internal array. Each
position in this array is called a bucket. Multiple entries can be put in the same bucket, this is called a
`hash collision`.

When the resize threshold is reached (capacity * loadingFactor), Java will then automatically make the capacity of the
internal array bigger by doubling it. For example, the standard capacity is 16 and the standard loadingFactor is 0.75,
the maximum entities of this array could have is now 12. When there are 13 entries, the capacity of the array will now
be doubled to 32. Every hashed key in the existing buckets now need to be rehashed, because the index is based off of
the maximum capacity of the internal array.

When you call get(key), Java creates the hash for this key and the bucket index. It then searches for this bucket index,
after that it looks inside the matching bucket:

If the bucket contains **only one entry**, lookup is very fast.

If **multiple entries** exist, Java checks them using `equals()` to find the matching key.

Since Java 8, if a bucket becomes too full, it is converted into a balanced tree (Red-Black Tree), improving worst-case
lookup time from O(n) to O(log n).

The average-case time complexity for put(), get() and remove() operations is O(1), which makes HashMap very efficient
for general use.

### LinkedHashMap

With the normal HashMap the data stores in a chaotic, but fast unsorted way. With the LinkedHashMap this data is stored
and sorted by either `insertion-order` or `acces-order`, depending on what you want.

This is done through a doubly linked list. This means that every node knows their "next" and "previous" node. Making the
iteration order predictable. Fortunatly this does not effect the speed drasticly, it still proceeds at the **same O(1)
speed** as the HashMap for the methods `put()`, `get()` and `remove()` with a slight overhead. This is because it needs
to add pointer events for "next" and "previous" for each node.

A LinkedHashMap can be useful if you want to implement it for LRU-caches (with acces-order), or when you just want it to
have a predictable outcome.

### TreeMap

A `TreeMap` is another common way to store key–value pairs in Java. What makes it special is that it automatically keeps
the keys in `sorted` order. The sorting can be alphabetical, numerical, or any order you choose with a `Comparator`.

Internally, Java uses a `Red-Black Tree`. That is a `self-balancing BST` (Binary Search Tree). This way of searching
ensures that the basic methods, such as put(), get() and remove() have time complexity of O(log n).

It gets this time complexity by maintaining a balance between tree height and insertion order using the following rules:

1. Each node is either red or black
2. The root always has the black color
3. A red node cannot have a red child.
4. Every path from root to end has the same amount of black nodes.

These rules prevent the tree from getting too deep or unbalanced. New nodes always get inserted as red. Whenever a new
node is inserted and it violates one of the rules (for example 2 red nodes in a row), it will perform one of these
solutions:

#### 1. Recoloring

Recoloring will appear when the **parent** and the **uncle** of the new node are both red. Then this will happen:

- Both the **parent** and **uncle** are colored to black
- **Grandparent** is colored to red
- If the **grandparent** is the root, it will immediatly be colored back to black

This will fix the red to red violation, without needing to rotate any of the nodes.

#### 2. Rotation

Rotation will occur when the **uncle** of the new inserted node is `black` or `non-existing`. Depending on the structure
of the tree, either one or two rotations are needed.

- Case 1 **(A straight line: LL or RR)**  
  A **single rotation** is applied at the grandparent node, this can be either a left or right rotation. after that, the
  parent and the grandparent colors are swapped.

- Case 2 **(A zig-zag line: LR or RL)**
  Here is a **double rotation** needed, first it needs to be changed to a straight line. After that the same rotation in
  case 1 will be used.

Both recoloring and rotation can be combined with eachother, when both of the conditions are met.