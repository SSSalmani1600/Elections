package nl.hva.research.niek.benchmark;

import org.openjdk.jmh.annotations.*;
import java.util.*;

@State(Scope.Thread)
public abstract class BaseBenchmark {

    List<Integer> keys;
    Random random;

    @Param({"1000", "100000", "1000000"})
    public int size;

    public abstract Map<Integer, Integer> createMap();

    Map<Integer, Integer> map;

    @Setup(Level.Iteration)
    public void setup() {
        map = createMap();
        random = new Random();
        keys = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            map.put(i, i);
            keys.add(i);
        }
    }

    @Benchmark
    public void testPutUpdate() {
        int key = keys.get(random.nextInt(size));
        map.put(key, 43);
    }

    @Benchmark
    public Integer testGet() {
        int key = keys.get(random.nextInt(size));
        return map.get(key);
    }

    @Benchmark
    public void testRemove() {
        int key = keys.get(random.nextInt(size));
        map.remove(key);
    }
}
