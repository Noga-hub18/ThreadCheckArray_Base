import java.util.ArrayList;
import java.util.Scanner;

/**
 * Console entry point for the concurrent subset-sum search.
 *
 * <p>Reads an integer list and target from standard input, starts two search
 * workers, then prints the result when a solution is found.</p>
 */
public class TestThreadCheckArray {
    /**
     * Reads input, runs the search workers, and prints the solution markers.
     *
     * @param args command-line arguments; not used
     */
    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            Thread thread1, thread2;
            System.out.println("Enter array size");
            int num  = input.nextInt();
            ArrayList<Integer> array = new ArrayList<>(num);
            System.out.println("Enter numbers for array");

            for (int index = 0; index < num; index++)
                array.add(input.nextInt());

            System.out.println("Enter number");
            num = input.nextInt();

            SharedData sd = new SharedData(
                    array.stream().mapToInt(Integer::intValue).toArray(), num);

            thread1 = new Thread(new ThreadCheckArray(sd), "thread1");
            thread2 = new Thread(new ThreadCheckArray(sd), "thread2");
            thread1.start();
            thread2.start();
            try {
                thread1.join();
                thread2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!sd.getFlag()) {
                System.out.println("Sorry");
                return;
            }
            System.out.println("Solution for b : " + sd.getB() + ",n = " + array.size());
            System.out.print("I:    ");
            for (int index = 0; index < array.size(); index++)
                System.out.print(index + "    ");
            System.out.println();
            System.out.print("A:    ");
            for (int index : array) {
                System.out.print(index);
                int counter = 5;
                while (true) {
                    index = index / 10;
                    counter--;
                    if (index == 0) break;
                }
                for (int i = 0; i < counter; i++) System.out.print(" ");
            }
            System.out.println();
            System.out.print("C:    ");
            for (boolean index : sd.getWinArray()) {
                if (index) System.out.print("1    ");
                else System.out.print("0    ");
            }
        }
    }
}
