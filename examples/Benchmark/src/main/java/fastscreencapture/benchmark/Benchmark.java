package fastscreencapture.benchmark;

import fastscreencapture.FastBmpWriter;
import fastscreen.FastScreen;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class Benchmark {

    private FastScreen screen;
    private int[] testPixels;
    private static final String BENCH_FILE = "benchmark_test_grab.bmp";

    @Setup
    public void setup() {
        screen = new FastScreen();
        testPixels = screen.captureRaw(0, 0, 800, 600);
        if (testPixels == null) {
            testPixels = new int[800 * 600];
        }
    }

    @TearDown
    public void tearDown() {
        if (screen != null) {
            screen.dispose();
        }
        File f = new File(BENCH_FILE);
        if (f.exists()) {
            f.delete();
        }
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void benchmarkFastScreenCaptureBmpWriter() throws Exception {
        FastBmpWriter.writeBmp(BENCH_FILE, 800, 600, testPixels);
    }
}
