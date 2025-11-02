import java.util.Random;

class NumberGenerator {
    int[] numbers = new int[10]; // store 10 random numbers

    void generateNumbers() {
        Random rand = new Random();
        System.out.println("Generated Numbers:");
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = rand.nextInt(201) - 100;
            System.out.print(numbers[i] + " ");
        }
        System.out.println("\n");
    }

    int[] getNumbers() {
        return numbers;
    }
}

class NegativeNumbers implements Runnable {
    int[] arr;
    NegativeNumbers(int[] a) {
        arr = a;
    }

    public void run() {
        System.out.print("Negative Numbers: ");
        for (int n : arr) {
            if (n < 0)
                System.out.print(n + " ");
        }
        System.out.println();
    }
}

class PositiveEvenNumbers implements Runnable {
    int[] arr;
    PositiveEvenNumbers(int[] a) {
        arr = a;
    }

    public void run() {
        System.out.print("Positive Even Numbers: ");
        for (int n : arr) {
            if (n > 0 && n % 2 == 0)
                System.out.print(n + " ");
        }
        System.out.println();
    }
}

class PositiveOddNumbers implements Runnable {
    int[] arr;
    PositiveOddNumbers(int[] a) {
        arr = a;
    }

    public void run() {
        System.out.print("Positive Odd Numbers: ");
        for (int n : arr) {
            if (n > 0 && n % 2 != 0)
                System.out.print(n + " ");
        }
        System.out.println();
    }
}

public class ThreadDemo2 {
    public static void main(String[] args) {
        NumberGenerator ng = new NumberGenerator();
        ng.generateNumbers();
        int[] nums = ng.getNumbers();

        // Create and start threads concurrently
        Thread t1 = new Thread(new NegativeNumbers(nums));
        Thread t2 = new Thread(new PositiveEvenNumbers(nums));
        Thread t3 = new Thread(new PositiveOddNumbers(nums));

        t1.start();
        t2.start();
        t3.start();
    }
}