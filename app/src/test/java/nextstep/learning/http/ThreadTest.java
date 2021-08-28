package nextstep.learning.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

class ThreadTest {

    @Test
    void testCounterWithConcurrency() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        MyCounter counter = new MyCounter();
        for (int i = 0; i < numberOfThreads; i++) {
            final int finalI = i;
            service.execute(() -> {
                counter.increment();
                System.out.println(String.format("%d 스레드 running", finalI));
                latch.countDown();
            });
        }
        latch.await();
        assertThat(counter.getCount()).isEqualTo(numberOfThreads);
    }

    @Test
    void testSummationWithConcurrency() throws InterruptedException {
        int numberOfThreads = 20;
        ExecutorService service = Executors.newFixedThreadPool(1);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        MyCounter counter = new MyCounter();
        for (int i = 0; i < numberOfThreads; i++) {
            final int finalI = i;
            service.submit(() -> {
                counter.increment();
                System.out.println(String.format("%d 스레드 running", finalI));
                latch.countDown();
            });
        }
        latch.await();
        assertThat(counter.getCount()).isEqualTo(numberOfThreads);
    }

    static class MyCounter {

        private int count;

        public synchronized void increment() {
            int temp = count;
            count = temp + 1;
        }

        public int getCount() {
            return count;
        }
    }
}
