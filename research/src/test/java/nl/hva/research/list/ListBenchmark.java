package nl.hva.research.list;

import org.openjdk.jmh.annotations.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
@State(Scope.Benchmark)
public class ListBenchmark {

    @Param({"10000"})
    private int size;

    private List<Integer> arrayList;
    private List<Integer> linkedList;
    private Random random;

    @Setup
    public void setup() {
        random = new Random();
        arrayList = new ArrayList<>();
        linkedList = new LinkedList<>();

        for (int i = 0; i < size; i++) {
            arrayList.add(i);
            linkedList.add(i);
        }
    }

    @Benchmark
    public void arrayListAdd() {
        arrayList.add(123);
    }

    @Benchmark
    public void linkedListAdd() {
        linkedList.add(123);
    }

    @Benchmark
    public void arrayListGet() {
        arrayList.get(random.nextInt(size));
    }

    @Benchmark
    public void linkedListGet() {
        linkedList.get(random.nextInt(size));
    }
}
