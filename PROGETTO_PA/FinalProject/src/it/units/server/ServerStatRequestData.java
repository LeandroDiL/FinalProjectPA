package it.units.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerStatRequestData {
    private static final List<Double> responseTimes = new ArrayList<>();
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();


    //using try-finally block in case anything goes wrong like Exception being thrown etc... (standard practice)
    public static void addResponseTime(double time) {
        try {
            writeLock.lock();
            responseTimes.add(time);
        } finally {
            writeLock.unlock();
        }
    }

    public static int getNumberOfResponses() {
        try {
            readLock.lock();
            return responseTimes.size();
        } finally {
            readLock.unlock();
        }
    }

    public static double findMaximumResponseTime() {
        try {
            readLock.lock();
            if (responseTimes.size() == 0) {
                return Double.NaN;
            }
            double maximumResponseTime = 0;
            for (Double responseTime : responseTimes) {
                if (responseTime > maximumResponseTime) {
                    maximumResponseTime = responseTime;
                }
            }
            return maximumResponseTime;
        } finally {
            readLock.unlock();
        }
    }

    public static double findAverageResponseTime() {
        try {
            readLock.lock();
            if (responseTimes.size() == 0) {
                return Double.NaN;
            }
            return responseTimes.stream().mapToDouble(Double::doubleValue).sum() / responseTimes.size();
        } finally {
            readLock.unlock();
        }
    }
}
