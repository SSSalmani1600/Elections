package nl.hva.research.streams;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class JmhRunner {
    public static void main(String[] args) throws Exception {
        System.out.println("Java runtime: " + System.getProperty("java.runtime.version"));
        System.out.println("JVM: " + System.getProperty("java.vm.name"));
        
        new Runner(
            new OptionsBuilder()
                .include(StreamsBenchmark.class.getSimpleName())
                .forks(3)
                    .addProfiler(GCProfiler.class)
                .build()
        ).run();
    }
}
