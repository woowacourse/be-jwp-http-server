package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);
    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_THREAD_COUNT = 250;

    private final ServerSocket serverSocket;
    private final ThreadPoolExecutor pool;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_THREAD_COUNT);
    }

    public Connector(int port, int acceptCount, int threadCount) {
        int checkedThreadCount = checkThreadCount(threadCount);
        this.serverSocket = createServerSocket(port, acceptCount);
        this.pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(checkedThreadCount);

        this.stopped = false;
    }

    private ServerSocket createServerSocket(int port, int acceptCount) {
        try {
            int checkedPort = checkPort(port);
            int checkedAcceptCount = checkAcceptCount(acceptCount);
            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private int checkThreadCount(int threadCount) {
        return Math.min(threadCount, DEFAULT_THREAD_COUNT);
    }

    public void start() {
        var thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        stopped = false;
        log.info("Web Application Server started {} port.", serverSocket.getLocalPort());
    }

    @Override
    public void run() {
        // 클라이언트가 연결될때까지 대기한다.
        while (!stopped) {
            connect();
        }
    }

    private void connect() {
        try {
            process(serverSocket.accept());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(Socket connection) {
        if (connection == null) {
            return;
        }
        // 스레드는 자원이고
        // 작업을 객체화한 게 쓰레드다.
        pool.execute(new Http11Processor(connection));
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private int checkPort(int port) {
        final var MIN_PORT = 1;
        final var MAX_PORT = 65535;

        if (port < MIN_PORT || MAX_PORT < port) {
            return DEFAULT_PORT;
        }
        return port;
    }

    private int checkAcceptCount(int acceptCount) {
        return Math.max(acceptCount, DEFAULT_ACCEPT_COUNT);
    }

    public int getPoolSize() {
        return pool.getCorePoolSize();
    }

    public int getQueueSize() {
        return pool.getQueue().size();
    }

    public void submit(Runnable runnable) {
        pool.submit(runnable);
    }

    public void close(){
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
