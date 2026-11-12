import java.util.ArrayList;

/**
 * Searches integer values for a subset whose sum equals a target.
 *
 * <p>Two instances are used concurrently. The worker named {@code thread1}
 * searches subsets containing the final value first; the other worker starts
 * with subsets that do not contain it.</p>
 */
public class ThreadCheckArray implements Runnable {
    /** Whether this worker has found a solution. */
    private boolean flag;
    /** Marks the values selected by this worker's solution. */
    private boolean[] winArray;
    /** Shared input and result state. */
    private final SharedData sd;
    /** Input values copied from the shared integer array. */
    private final ArrayList<Integer> array;
    /** Target sum to find. */
    private final int b;

    /**
     * Creates a search worker for the input and target in the shared data.
     *
     * @param sd shared data containing the input values, target, and result
     */
    public ThreadCheckArray(SharedData sd) {
        this.sd = sd;
        synchronized (sd) {
            array = new ArrayList<>();
            for (int value : sd.getArray()) {
                array.add(value);
            }
            b = sd.getB();
        }
        winArray = new boolean[array.size()];
    }

    /**
     * Recursively searches the first {@code n} values for a subset with the
     * specified remaining sum. The search stops early when another worker
     * publishes a solution.
     *
     * @param n number of values in the current search range
     * @param b remaining sum required for the current branch
     */
    void rec(int n, int b) {
        synchronized (sd) {
            if (sd.getFlag()) return;
        }
        if (n == 1) {
            if (b == 0 || b == array.get(n - 1)) {
                flag = true;
                synchronized (sd) { sd.setFlag(true); }
            }
            if (b == array.get(n - 1)) winArray[n - 1] = true;
            return;
        }

        rec(n - 1, b - array.get(n - 1));
        if (flag) winArray[n - 1] = true;
        synchronized (sd) {
            if (sd.getFlag()) return;
        }
        rec(n - 1, b);
    }

    /**
     * Executes this worker's subset search and publishes its markers if it
     * finds a solution.
     */
    @Override
    public void run() {
        if (array.size() != 1)
            if (Thread.currentThread().getName().equals("thread1"))
                rec(array.size() - 1, b - array.get(array.size() - 1));
            else
                rec(array.size() - 1, b);
        if (array.size() == 1)
            if (b == array.get(0) && !flag) {
                winArray[0] = true;
                flag = true;
                synchronized (sd) { sd.setFlag(true); }
            }
        if (flag) {
            if (Thread.currentThread().getName().equals("thread1"))
                winArray[array.size() - 1] = true;
            synchronized (sd) { sd.setWinArray(winArray); }
        }
    }
}
