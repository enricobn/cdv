package org.altviews.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by enrico on 3/16/16.
 */
public class TimedQueueThread<T> {
    private final Queue<T> queue = new ConcurrentLinkedDeque<>();
    private final ElementRunnable<T> runnable;
    private final long millis;
    private boolean running = true;

    public TimedQueueThread(ElementRunnable<T> runnable, long millis) {
        this.runnable = runnable;
        this.millis = millis;
    }

    public synchronized void add(T element) {
        queue.clear();
        queue.add(element);
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        Thread.sleep(millis);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        running = false;
                    }

                    T element = null;
                    synchronized (this) {
                        if (!queue.isEmpty()) {
                            element = queue.remove();
                        }
                    }
                    if (element != null) {
                        runnable.run(element);
                    }
                }
            }
        }).start();
    }

    public interface ElementRunnable<T> {
        void run(T element);
    }
}
