package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.Mapper;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_CORE_POOL_SIZE = 50;
    private static final int BLOCKING_QUEUE_SIZE = 100;
    private static final long KEEP_ALIVE_TIME = 60L;

    private final ServerSocket serverSocket;
    private final ThreadPoolExecutor threadPoolExecutor;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_CORE_POOL_SIZE);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.threadPoolExecutor = new ThreadPoolExecutor(
                DEFAULT_CORE_POOL_SIZE, maxThreads,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(BLOCKING_QUEUE_SIZE)
        );
        this.stopped = false;
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            final int checkedPort = checkPort(port);
            final int checkedAcceptCount = checkAcceptCount(acceptCount);
            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void start(Mapper mapper) {
        var thread = new Thread(() -> run(mapper));
        thread.setDaemon(true);
        thread.start();
        stopped = false;
        log.info("Web Application Server started {} port.", serverSocket.getLocalPort());
    }

    public void run(Mapper mapper) {
        // 클라이언트가 연결될때까지 대기한다.
        while (!stopped) {
            connect(mapper);
        }
    }

    private void connect(Mapper mapper) {
        try {
            process(serverSocket.accept(), mapper);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection, Mapper mapper) throws IOException {
        if (connection == null) {
            return;
        }
        var processor = new Http11Processor(connection, mapper);

        try {
            threadPoolExecutor.submit(processor);
            log.info("Task submitted: {} {}",
                    threadPoolExecutor.getActiveCount(),
                    threadPoolExecutor.getQueue().size()
            );
        } catch (RejectedExecutionException e) {
            log.error("Task rejected: The queue is full. {}", e.getMessage());
            connection.close();
        }
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private int checkPort(final int port) {
        final var MIN_PORT = 1;
        final var MAX_PORT = 65535;

        if (port < MIN_PORT || MAX_PORT < port) {
            return DEFAULT_PORT;
        }
        return port;
    }

    private int checkAcceptCount(final int acceptCount) {
        return Math.max(acceptCount, DEFAULT_ACCEPT_COUNT);
    }
}
