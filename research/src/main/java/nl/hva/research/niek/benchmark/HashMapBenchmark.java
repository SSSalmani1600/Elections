package nl.hva.research.niek.benchmark;

import org.openjdk.jmh.annotations.*;

import java.util.*;

@State(Scope.Thread)
public class HashMapBenchmark extends BaseBenchmark {
    @Override
    public Map<Integer, Integer> createMap() {
        return new HashMap<>();
    }
}

