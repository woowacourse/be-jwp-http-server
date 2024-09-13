package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.Controller;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int MAX_THREADS = 250;

    private final Controller controller;
    private final ServerSocket serverSocket;
    private boolean stopped;
    private final ExecutorService executorService;

    public Connector(final Controller controller) {
        // maxThreads: 스레드 풀에서 동시에 실행할 수 있는 최대 스레드 수입니다. 초과하면 대기 큐에 추가된다.
        // acceptCount: 대기 큐가 가득 찼을 때 추가 요청이 대기할 수 있는 큐의 크기이다.
        this(controller, DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, MAX_THREADS);
    }

    public Connector(final Controller controller, final int port, final int acceptCount, final int maxThreads) {
        this.controller = controller;
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.executorService = Executors.newFixedThreadPool(maxThreads);
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

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        Http11Processor processor = new Http11Processor(controller, connection);
        executorService.execute(processor);
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
            shutdown();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void shutdown() {
        try {
            executorService.shutdown(); // 실행 중인 모든 Task가 수행되면 종료
            // 800ms 이내에 종료되지 않으면 실행 중인 모든 Task를 즉시 종료
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            executorService.shutdownNow(); // 인터럽트 발생 경우에도 종료
            Thread.currentThread().interrupt(); // 인터럽트 상태를 다시 설정
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
