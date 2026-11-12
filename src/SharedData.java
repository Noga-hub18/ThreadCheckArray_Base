import java.util.ArrayList;

/**
 * Holds the input sequence, target, and shared search result used by
 * {@link ThreadCheckArray} workers.
 */
public class SharedData {
    /** Input sequence stored internally as an {@code ArrayList<Integer>}. */
    private final ArrayList<Integer> array;

    /** Indicates which input elements form the discovered solution. */
    private boolean[] winArray;

    /** Whether any worker has found a solution. */
    private boolean flag;

    /** Target sum that the workers search for. */
    private final int b;

    /**
     * Creates shared search state from an input sequence and target.
     *
     * @param array input values to search
     * @param b target sum
     */
    public SharedData(int[] array, int b) {
        this.array = new ArrayList<>();
        for (int value : array) {
            this.array.add(value);
        }
        this.winArray = new boolean[this.array.size()];
        this.b = b;
    }

    /**
     * Returns the solution indicator for each input element.
     *
     * @return the shared boolean solution array
     */
    public boolean[] getWinArray() {
        return winArray;
    }

    /**
     * Sets the solution indicator for each input element.
     *
     * @param winArray boolean indicators corresponding to the input sequence
     */
    public void setWinArray(boolean[] winArray) {
        this.winArray = winArray;
    }

    /**
     * Returns the input values as an array snapshot.
     *
     * @return a new array containing the values stored in the internal
     *         {@code ArrayList<Integer>}
     */
    public int[] getArray() {
        int[] values = new int[array.size()];
        for (int index = 0; index < array.size(); index++) {
            values[index] = array.get(index);
        }
        return values;
    }

    /**
     * Returns the target sum.
     *
     * @return target sum
     */
    public int getB() {
        return b;
    }

    /**
     * Reports whether a worker has found a solution.
     *
     * @return {@code true} if a solution has been found; {@code false}
     *         otherwise
     */
    public boolean getFlag() {
        return flag;
    }

    /**
     * Updates whether a solution has been found.
     *
     * @param flag {@code true} if a solution has been found; {@code false}
     *             otherwise
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
