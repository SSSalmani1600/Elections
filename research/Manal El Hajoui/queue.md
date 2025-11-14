## Interface Queue with implementation classes LinkedList, ArrayDeque

### Problem or Need Analysis:

The data in software development often needs to be processed, in a specific order to maintain consistency and efficiency. Systems such as printers, task schedulers and webservers handle multiple requests at a time. Without a proper data structure, the tasks could overlap, be lost or even be executed in the wrong order, leading to performance issues and unreliable results.

The queue interface is important for solving this problem by organizing the elements according to the First In, First Out (FIFO) or Last In, First Out (LIFO) principles. It provides a structured and predictable way of processing data, to ensure that operations occur in the correct sequence (Oracle, 2024).
Understanding the Queue interface and its main implementations, LinkedList and ArrayDeque, is important because these structures are frequently used in many applications. As a developer, it is essential to understand the differences between them to choose the most suitable option for each specific situation. Each of these has different performance characteristics and memory requirements. This research focuses on comparing these two implementations to determine their strengths and weaknesses (JavaGuides, 2023).
To better understand how different Queue implementations behave and perform, this research is guided by a main question and several sub-questions. With these questions were aiming to explore the theoretical and practical aspects of the Queue Interface in Java.

**Main research question:**

How do the LinkedList and ArrayDeque implementations of the Java Queue Interface differ in terms of performance, memory usage, and suitability across various use cases?

**Sub-Questions:**

1. What is the structure of the Queue Interface in the Java Collection Framework
2.  How does the LinkedList class implement the Queue Interface, and in which situations is it most effective to use it?
3. How does the ArrayDeque class implement the Queue Interface, and in which situations is it most effective to use it?
4. How do the results from my own Queue implementation compare to existing Java documentation and performance benchmarks?



-----------------------------------

### Explanation of the Algorithm / Data Structure


**Sub-question 1:**
What is the role and structure of the Queue Interface in the Java Collection Framework, and how does it manage element ordering (FIFO and LIFO)?
The Queue interface is part of the Java Collection Framework and is used to store elements in a specific order. A queue helps programs decide which task should be handled first. For example, in a printer queue, the first document that was sent to the printer will also be printed first.
By default, a queue follows the FIFO (First In, First Out) principle — the first item added is also the first one to be removed (W3Schools, 2024). However, some types of queues can work differently, such as LIFO (Last In, First Out), where the last item added is the first one to be removed (Oracle, 2024).
Each queue has a head, which is the next element to be processed, and a tail, where new elements are added. Some queues, like priority queues, may handle elements in a different order, depending on what is most important. This makes the Queue interface very flexible, because different programs can use it in the way that best fits their needs (Oracle, 2024).

