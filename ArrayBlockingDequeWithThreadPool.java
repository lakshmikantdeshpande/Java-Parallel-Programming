import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ArrayBlockingQueueDemo {

  private ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
  private static final int THREAD_COUNT = 4;

  public static void main(String[] args) {
    new ArrayBlockingQueueDemo().process();
  }

  private void process() {
    ExecutorService service = Executors.newFixedThreadPool(THREAD_COUNT);
    Consumer[] consumers = new Consumer[THREAD_COUNT];
    for (int i = 0; i < THREAD_COUNT; i++) {
      final String threadName = "Thread " + i;
      Consumer consumer = new Consumer(queue, threadName);
      consumers[i] = consumer;
      service.submit(consumer);
    }

    int i = 0;
    while (i < 10000) {
      try {
        queue.put(i++);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private static class Consumer implements Runnable {
    private final ArrayBlockingQueue<Integer> queue;
    private boolean stop = false;

    public synchronized void stop() {
      this.stop = true;
    }

    public Consumer(ArrayBlockingQueue queue, String name) {
      this.queue = queue;
      Thread.currentThread().setName(name);
    }

    private synchronized boolean keepRunning() {
      return !this.stop;
    }

    @Override
    public void run() {
      while (keepRunning()) {

        try {
          Integer number = queue.take();
          System.out.println("In thread: " + Thread.currentThread().getName() + "   " + number);
        } catch (InterruptedException e) {
          System.out.println(e);
          try {
            TimeUnit.SECONDS.sleep(1000L);
          } catch (InterruptedException ex) {
            ex.printStackTrace();
          }
        }
      }
    }
  }
}
