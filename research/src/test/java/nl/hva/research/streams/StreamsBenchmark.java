package nl.hva.research.streams;

import org.openjdk.jmh.annotations.*;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)           // time per operation
@OutputTimeUnit(TimeUnit.MILLISECONDS)     // results in ms
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)      // ~5s warmup
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS) // ~10s measurement
@Fork(value = 3)                           // isolated JVM forks (3 separate JVMs)
@State(Scope.Benchmark)                    // shared state per benchmark
public class StreamsBenchmark {

    @Param({"100", "50000", "2000000"})    // small/medium/large 
    private int size;

    private int[] data;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(42);
        data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = random.nextInt();
        }
    }

    @Benchmark
    public long forLoop() {
        long sum = 0;
        for (int x : data) {
            if (x % 2 == 0) sum += x * 2L;
        }
        return sum;
    }

    @Benchmark
    public long intStream() {
        return Arrays.stream(data)
                .filter(x -> x % 2 == 0)
                .map(x -> x * 2)
                .asLongStream()
                .sum();
    }

    @Benchmark
    public long boxedStream() {
        return Arrays.stream(data)
                .boxed()
                .filter(x -> x % 2 == 0)
                .map(x -> x * 2)
                .mapToLong(Integer::longValue)
                .sum();
    }

    @Benchmark
    public long parallelIntStream() {
        return Arrays.stream(data)
                .parallel()
                .filter(x -> x % 2 == 0)
                .map(x -> x * 2)
                .asLongStream()
                .sum();
    }
}