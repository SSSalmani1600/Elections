import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class SetDemo {

    public static void main(String[] args) {
        int size = 1_000_000;
        int runs = 20;
        int warmupRuns = 5;

        int[] data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = i;
        }

        int[] keys = {0, size / 2, size - 1, -1};

        System.out.println("=== WARM-UP (" + warmupRuns + " runs) ===");
        for (int i = 0; i < warmupRuns; i++) {
            runSingleTest(data, keys, false);
        }

        long totalHashInsert = 0, totalTreeInsert = 0;
        long totalHashLookup = 0, totalTreeLookup = 0;
        long totalHashRemove = 0, totalTreeRemove = 0;
        long totalHashMemory = 0, totalTreeMemory = 0;

        System.out.println("\n=== MEASURED RUNS (" + runs + " runs) ===");

        for (int i = 0; i < runs; i++) {
            long[] r = runSingleTest(data, keys, false);

            totalHashInsert += r[0];
            totalTreeInsert += r[1];

            totalHashLookup += r[2];
            totalTreeLookup += r[3];

            totalHashRemove += r[4];
            totalTreeRemove += r[5];

            totalHashMemory += r[6];
            totalTreeMemory += r[7];
        }

        System.out.println("\n=== AVERAGE RESULTS OVER " + runs + " RUNS ===");

        System.out.println("\n--- INSERT (ns) ---");
        System.out.println("HashSet:   " + (totalHashInsert / runs));
        System.out.println("TreeSet:   " + (totalTreeInsert / runs));

        System.out.println("\n--- LOOKUP (ns) ---");
        System.out.println("HashSet:   " + (totalHashLookup / runs));
        System.out.println("TreeSet:   " + (totalTreeLookup / runs));

        System.out.println("\n--- REMOVE (ns) ---");
        System.out.println("HashSet:   " + (totalHashRemove / runs));
        System.out.println("TreeSet:   " + (totalTreeRemove / runs));

        System.out.println("\n--- MEMORY USED (bytes) ---");
        System.out.println("HashSet:   " + (totalHashMemory / runs));
        System.out.println("TreeSet:   " + (totalTreeMemory / runs));
    }

    /**
     * Voert één volledige test uit (insert, lookup, remove, memory) voor HashSet en TreeSet.
     * Retourneert:
     * [0] hashInsert, [1] treeInsert,
     * [2] hashLookup, [3] treeLookup,
     * [4] hashRemove, [5] treeRemove,
     * [6] hashMemory, [7] treeMemory
     */
    private static long[] runSingleTest(int[] data, int[] keys, boolean print) {

        Runtime rt = Runtime.getRuntime();

        // ===== HASHSET =====
        rt.gc();
        long beforeHash = rt.totalMemory() - rt.freeMemory();

        Set<Integer> hashSet = new HashSet<>();

        long start = System.nanoTime();
        for (int v : data) {
            hashSet.add(v);
        }
        long hashInsert = System.nanoTime() - start;

        start = System.nanoTime();
        for (int key : keys) {
            hashSet.contains(key);
        }
        long hashLookup = System.nanoTime() - start;

        start = System.nanoTime();
        for (int key : keys) {
            hashSet.remove(key);
        }
        long hashRemove = System.nanoTime() - start;

        long afterHash = rt.totalMemory() - rt.freeMemory();
        long hashMemory = afterHash - beforeHash;


        // ===== TREESET =====
        rt.gc();
        long beforeTree = rt.totalMemory() - rt.freeMemory();

        Set<Integer> treeSet = new TreeSet<>();

        start = System.nanoTime();
        for (int v : data) {
            treeSet.add(v);
        }
        long treeInsert = System.nanoTime() - start;

        start = System.nanoTime();
        for (int key : keys) {
            treeSet.contains(key);
        }
        long treeLookup = System.nanoTime() - start;

        start = System.nanoTime();
        for (int key : keys) {
            treeSet.remove(key);
        }
        long treeRemove = System.nanoTime() - start;

        long afterTree = rt.totalMemory() - rt.freeMemory();
        long treeMemory = afterTree - beforeTree;

        if (print) {
            System.out.println("\n--- Single Run ---");
            System.out.println("HashSet Insert: " + hashInsert);
            System.out.println("TreeSet Insert: " + treeInsert);
            System.out.println("HashSet Lookup: " + hashLookup);
            System.out.println("TreeSet Lookup: " + treeLookup);
            System.out.println("HashSet Remove: " + hashRemove);
            System.out.println("TreeSet Remove: " + treeRemove);
            System.out.println("HashSet Memory: " + hashMemory);
            System.out.println("TreeSet Memory: " + treeMemory);
        }

        return new long[]{
                hashInsert, treeInsert,
                hashLookup, treeLookup,
                hashRemove, treeRemove,
                hashMemory, treeMemory
        };
    }
}
