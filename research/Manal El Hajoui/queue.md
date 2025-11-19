## Interface Queue with implementation classes LinkedList, ArrayDeque

### Problem or Need Analysis:

The data in software development often needs to be processed, in a specific order to maintain consistency and efficiency. Systems such as printers, task schedulers and webservers handle multiple requests at a time. Without a proper data structure, the tasks could overlap, be lost or even be executed in the wrong order, leading to performance issues and unreliable results.

The queue interface is important for solving this problem by organizing the elements according to the First In, First Out (FIFO) or Last In, First Out (LIFO) principles. It provides a structured and predictable way of processing data, to ensure that operations occur in the correct sequence (Oracle, 2024).
Understanding the Queue interface and its main implementations, LinkedList and ArrayDeque, is important because these structures are frequently used in many applications. As a developer, it is essential to understand the differences between them to choose the most suitable option for each specific situation. Each of these has different performance characteristics and memory requirements. This research focuses on comparing these two implementations to determine their strengths and weaknesses (JavaGuides, 2023).
To better understand how different Queue implementations behave and perform, this research is guided by a main question and several sub-questions. With these questions were aiming to explore the theoretical and practical aspects of the Queue Interface in Java.

**Main research question:**

How do the LinkedList, ArrayDeque and priority queue implementations of the Java Queue Interface differ in terms of performance, memory usage, and suitability across various use cases?

**Sub-Questions:**

1. What is the structure of the Queue Interface in the Java Collection Framework
----
2.  How does the LinkedList class implement the Queue Interface, and in which situations is it most effective to use it?
----
3. How does the ArrayDeque class implement the Queue Interface, and in which situations is it most effective to use it?
----
4. How does PriorityQueue implement the Queue Interface, and in which situations is priority-based ordering more effective?
----
5. How do the results from my own Queue implementation compare to existing Java documentation and performance benchmarks?



-----------------------------------

### Explanation of the Algorithm / Data Structure


**Sub-question 1:**
_What is the structure of the Queue Interface in the Java Collection Framework_

The Queue interface is part of the Java Collection Framework and is used to store elements in a specific order.
A queue helps programs decide which task should be handled first. For example, in a printer queue, the first document that was sent to the printer will also be printed first.


Most queues follow the FIFO (First In, First Out) principle, the first item added is also the first one to be removed (W3Schools, 2024). However, some types of queues can work differently, such as LIFO (Last In, First Out), where the last item added is the first one to be removed (Oracle, 2024).
Each queue has a head, which is the next element to be processed, and a tail, where new elements are added. Some queues, like priority queues, may handle elements in a different order, depending on what is most important. This makes the Queue interface very flexible, because different programs can use it in the way that best fits their needs (Oracle, 2024).


**Sub-question 2:**
_How does the LinkedList class implement the Queue Interface, and in which situations is it most effective to use it?_

The LinkedList is one of the classes in Java that can be used to create a queue. According to Oracle (2024), It is flexible because it could work as a list (where you access elements by position) and as a queue (where elements are processed in order).

Inside the LinkedList, all the elements are connected to each other like a chain. In the chain, each item knows which element comes before or comes after. This type of connection is called a doubly linked list (JavaGuides, 2023). Because of this structure, adding or removing elements in the middle of the list is fast and easy.

However, the flexibility does have a small drawback. Every element in the LinkedList needs to store two pieces of information, one for the previous element and one for the next. The LinkedList uses more memory compared to other types (JavaGuides, 2023).

LinkedList is the most effective when your program removes or adds elements in a different position, not just in the beginning or end. For example, if a program needs to frequently change the order of tasks or insert new items in between existing ones, the LinkedList is a good choice (W3Schools, 2024). 

**Sub-question 3:**
_How does the ArrayDeque class differ from LinkedList in internal structure and operational efficiency when used as a queue?_

The ArrayDeque is another class that could be used to create a queue. Unlike the LinkedList, that connects elements like a chain, the ArrayDeque stores its elements in a resizable array, like a row of boxes next to each other. When the array becomes full, it automatically grows to make room for new elements (JavaGuides, 2023).

Because everything is stored next to each other in memory, the ArrayDeque is faster when adding or removing elements from the start of the end of the queue. This makes it more efficient than the LinkedList for most queue operations (Oracle, 2024).

However, the ArrayDeque does not not allow null elements since those are used internally to mark empty spots in the array. It’s also less flexible for inserting or removing elements in the middle of the queue, something the LinkedList handles better.

In general, the ArrayDeque is most effective when the program mainly adds or removes elements from the beginning or end of the queue. For example, it is ideal for managing tasks or storing temporary data that changes often but doesn’t need rearranging (JavaTPoint, 2024).

**Sub-question 4:**
_How does PriorityQueue implement the Queue Interface, and in which situations is priority-based ordering more effective?_

A PriorityQueue does not work like a normal waiting line. In a regular queue, the first person who joins the line is also the first one to be helped (FIFO). Sometimes the last person to join is helped first (LIFO), like a stack of plates. But a PriorityQueue works differently: it behaves like a priority line, where the most important item is helped first, even if it arrived later (Oracle, 2024a; GeeksforGeeks, 2025).

You can think of it like a hospital emergency room:
the patient with the most urgent problem is treated before someone with a minor issue.

Inside Java, PriorityQueue keeps items in an order where the most important item automatically moves to the front (Oracle, 2024a). This makes it useful in situations where the order of arrival is not the most important factor. For example, some computer systems must always handle urgent tasks before normal tasks. It is also used in map systems that calculate the shortest path between two locations. A well-known method for this is Dijkstra’s algorithm, which always chooses the next closest point to move toward (Dijkstra, 1959).

Because Java offers both normal queues (FIFO), stack-like queues (LIFO), and priority-based queues, developers can choose the type that fits the situation best (Oracle, 2024b). This makes the Queue interface flexible and useful in many different programs.


**Sub-question 5:**
_How do my own performance measurements compare to official Java documentation and benchmark results?_

Based on my performance test results, the _ArrayDeque_ showed the most stable and efficient performance when adding elements to the queue. 
The graph shows the average add-time remained consistently low, with only a few small spikes. This aligns with the java documentation (Oracle, 2024) ans other benchmark sources (JavaGuides, 2023), which describes the _ArrayDeque_ fast for queue operations because it stores elements in a resizeable array located next to each other in memory. 

In contrast to the _ArrayDeque_, the _LinkedList_ showed several performance spikes. These occasional delays are caused by its internal structure, a chain of nodes linked next to each other, which requires additional elements. This supports the idea that the _LinkedList_ is less efficient for frequent add/remove operations but remains useful when flexible insertion positions are needed (W3Schools, 2024). 

The _PriorityQueue_ had the highest and most frequent spikes in add-time. This is the expected, since it reorders elements internally after each insertion to maintain its priority order. While it is slower for large amounts of data, it offers clear advantages in scenarios where the order of importance matters more than speed, such as scheduling urgent tasks or pathfinding algorithms (GeeksforGeeks, 2025).

Overall my results confirm the existing documentation:
_ArrayDeque:_ Best overall performance and lowest memory overhead.
_LinkedList:_ Slower but flexible for dynamic insertions.
_PriorityQueue:_ Less efficient for raw performance but essential for priority based processing.

**Conclusion:**

The comparison with the LinkedList, ArrayDeque and the PriorityQueue shows that the ArrayDeque the most efficient application is of the Java Queue interface for the most general use cases. It provides fast and consistent performance with low memory usage. The LinkedList is slower but does offer flexibility when frequent insertions or removals occur in the middle of the collection.
The PriorityQueue performs less efficiently because of its internal reordering, but is essential in scenarios where task priority is more important than speed is.

For developers, the recommendation is to use the ArrayDeque for most queue operations, LinkedList for flexible list-like structures, and the PriorityQueue when handeling priority based tasks.  




