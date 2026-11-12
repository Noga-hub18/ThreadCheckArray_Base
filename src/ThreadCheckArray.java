import java.util.ArrayList;

/**
 * Searches a shared integer sequence for a subset whose sum equals the
 * shared target. Each instance can run on a separate thread and publishes a
 * winning selection through the shared data object.
 */
public class ThreadCheckArray implements Runnable {
    /** Whether this worker has found a winning subset. */
    private boolean flag;

    /** Indicates which sequence elements belong to this worker's solution. */
    private boolean[] winArray;

    /** Shared target, input sequence, and result state. */
    SharedData sd;

    /** Local {@link ArrayList} copy of the input sequence. */
    ArrayList<Integer> array;

    /** Target sum to be matched by a subset of {@link #array}. */
    int b;

    /**
     * Creates a worker for the supplied shared search state.
     *
     * @param sd shared input, target, and result state; its integer array is
     *           copied into this worker's {@code ArrayList<Integer>}
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
     * Recursively searches the first {@code n} elements for a subset that
     * matches the remaining target.
     *
     * @param n number of leading elements to consider
     * @param b target remaining after any previously selected elements
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
     * Runs this worker's subset search and publishes its winning selection,
     * if one is found.
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
