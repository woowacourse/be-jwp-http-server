package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    public static final int DEFAULT_PORT = 8080;
    public static final int DEFAULT_ACCEPT_COUNT = 100;

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private final ServerSocket serverSocket;
    private boolean stopped;
    private final ThreadPoolExecutor threadPoolExecutor;

    public Connector(final int port,
                     final int acceptCount,
                     final int maxThreadSize,
                     final int coreThreadSize,
                     final long keepAliveTimeSecond,
                     final int threadPoolQueueSize) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.threadPoolExecutor = new ThreadPoolExecutor(
                coreThreadSize,
                maxThreadSize,
                keepAliveTimeSecond,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(threadPoolQueueSize)
        );
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            final int checkedPort = checkPort(port);
            final int checkAcceptCount = checkAcceptCount(acceptCount);
            return new ServerSocket(checkedPort, checkAcceptCount);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void start() {
        var thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        stopped = false;
    }

    @Override
    public void run() {
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

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        var processor = new Http11Processor(connection);
        threadPoolExecutor.submit(processor);
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
