/*
 * Copyright (c) 2017 Enrico Benedetti
 *
 * This file is part of Class dependency viewer (CDV).
 *
 * CDV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CDV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CDV.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cdv.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

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

    public void stop() {
        running = false;
    }
}
