package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList Ns = new AList<Integer>();
        AList times = new AList<Double>();
        AList opCounts = new AList<Integer>();
        int[] num_used = {1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000};
        for (int i = 0; i < num_used.length; i += 1) {
            AddItems(num_used[i], Ns, times, opCounts);
        }
        printTimingTable(Ns, times, opCounts);
    }

    private static void AddItems(int N, AList Ns, AList times, AList opCounts) {
        Ns.addLast(N);
        opCounts.addLast(1000);
        SLList test = new SLList<Integer>();
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < N; i += 1) {
            test.addLast(1);
        }
        double timeInSeconds = sw.elapsedTime();
        times.addLast(timeInSeconds);
    }

}
