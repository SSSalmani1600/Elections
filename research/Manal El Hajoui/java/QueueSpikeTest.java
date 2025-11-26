import java.io.PrintWriter;
import java.util.*;

public class QueueSpikeTest {
    public static void main(String[] args) {
        int elements = 1_000_000;
        int batch = 1000; // measure every 1000 operations

        runSpikeTest(new ArrayDeque<>(), "ArrayDeque_spikes.csv", elements, batch);
        runSpikeTest(new LinkedList<>(), "LinkedList_spikes.csv", elements, batch);
        runSpikeTest(new PriorityQueue<>(), "PriorityQueue_spikes.csv", elements, batch);
    }

    private static void runSpikeTest(Queue<Integer> queue, String fileName, int elements, int batchSize) {
        try (PrintWriter writer = new PrintWriter(fileName)) {

            writer.println("Batch,AddTime(ns),PollTime(ns)");

            int batchCount = elements / batchSize;

            for (int b = 0; b < batchCount; b++) {

                long startAdd = System.nanoTime();
                for (int i = 0; i < batchSize; i++) queue.add(i);
                long endAdd = System.nanoTime();
                long addTime = endAdd - startAdd;

                long startPoll = System.nanoTime();
                for (int i = 0; i < batchSize; i++) queue.poll();
                long endPoll = System.nanoTime();
                long pollTime = endPoll - startPoll;

                writer.println(b + "," + addTime + "," + pollTime);
            }

            System.out.println("Spike test saved to: " + fileName);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
