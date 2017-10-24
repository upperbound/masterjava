package ru.javaops.masterjava.matrix;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {
    private static final int DEFAULT_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 4;

    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int matrixBT[][] = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBT[j][i] = matrixB[i][j];
            }
        }

        final AtomicInteger counter = new AtomicInteger();
        final int threadCount;
        if (executor instanceof ThreadPoolExecutor) threadCount = ((ThreadPoolExecutor) executor).getCorePoolSize();
        else threadCount = DEFAULT_THREAD_COUNT;
        Future[] futures = new Future[threadCount];
        for (int i = 0; i < threadCount; i++)
            futures[i] = executor.submit(() -> MatrixUtil.threadMultiply(matrixA, matrixBT, matrixC, matrixSize, threadCount, counter));
        for (Future future : futures)
            future.get();
        return matrixC;
    }

    private static void threadMultiply(int[][] mA, int[][] mB, int[][] mC, int matrixSize, int threadCount, AtomicInteger counter){
        int c;
        synchronized (counter) {
            if (counter.get() >= threadCount) return;
            else c = counter.getAndIncrement();
        }
        final int start = c*(matrixSize/threadCount);
        final int end = ++c < threadCount ? c*(matrixSize/threadCount) : matrixSize;
        for (int i = start; i < end; i++) {
            doOptimizedMultiply(mA, mB, mC, matrixSize, i);
        }
    }

    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int matrixBT[][] = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBT[j][i] = matrixB[i][j];
            }
        }
        for (int i = 0; i < matrixSize; i++) {
            doOptimizedMultiply(matrixA, matrixBT, matrixC, matrixSize, i);
        }
        return matrixC;
    }

    private static void doOptimizedMultiply(int[][] mA, int[][] mB, int[][] mC, int matrixSize, int curRow) {
        for (int j = 0; j < matrixSize; j++) {
            int sum = 0;
            for (int k = 0; k < matrixSize; k++) {
                sum += mA[curRow][k] * mB[j][k];
            }
            mC[curRow][j] = sum;
        }
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
