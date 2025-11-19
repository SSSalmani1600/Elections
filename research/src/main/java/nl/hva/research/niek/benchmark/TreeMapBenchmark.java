package nl.hva.research.niek.benchmark;

import java.util.Map;
import java.util.TreeMap;

public class TreeMapBenchmark extends BaseBenchmark {
    @Override
    public Map<Integer, Integer> createMap() {
        return new TreeMap<>();
    }
}
