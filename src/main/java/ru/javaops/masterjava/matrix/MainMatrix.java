package ru.javaops.masterjava.matrix;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * gkislin
 * 03.07.2016
 */
public class MainMatrix {
    private static final int MATRIX_SIZE = 1000;
    private static final int THREAD_NUMBER = 10;
    private static final int COMPARE_COUNT = 5;

    private final static ExecutorService executor = Executors.newFixedThreadPool(MainMatrix.THREAD_NUMBER);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final int[][] matrixA = MatrixUtil.create(MATRIX_SIZE);
        final int[][] matrixB = MatrixUtil.create(MATRIX_SIZE);

        double singleDuration, singleThreadSum = 0.;
        double concurrentDuration, concurrentThreadSum = 0.;
        final String output = "thread time, sec: %.3f";
        int count = 1;
        System.out.println(Runtime.getRuntime().availableProcessors());
        while (count <= COMPARE_COUNT) {
            System.out.println("Pass " + count);
            long start = System.currentTimeMillis();
            final int[][] matrixC = MatrixUtil.singleThreadMultiply(matrixA, matrixB);
            singleDuration = (System.currentTimeMillis() - start) / 1000.;
            singleThreadSum += singleDuration;

            start = System.currentTimeMillis();
            final int[][] concurrentMatrixC = MatrixUtil.concurrentMultiply(matrixA, matrixB, executor);
            concurrentDuration = (System.currentTimeMillis() - start) / 1000.;
            concurrentThreadSum += concurrentDuration;

            out(
                    "Single " + output,
                    singleDuration
            );
            out(
                    "Concurrent " + output,
                    concurrentDuration
            );

            if (!MatrixUtil.compare(matrixC, concurrentMatrixC)) {
                System.err.println("Comparison failed");
                break;
            }
            count++;
        }
        executor.shutdown();
        out(
                "\nAverage single " + output,
                singleThreadSum / COMPARE_COUNT
        );
        out(
                "Average concurrent " + output,
                concurrentThreadSum / COMPARE_COUNT
        );
    }

    private static void out(String format, double ms) {
        System.out.println(String.format(format, ms));
    }
}
