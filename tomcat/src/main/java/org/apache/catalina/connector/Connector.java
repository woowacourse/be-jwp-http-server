package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.adaptor.ControllerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private final ControllerAdaptor controllerAdaptor;
    private final ExecutorService executorService;

    private final ServerSocket serverSocket;
    private boolean stopped;

    public Connector(ControllerAdaptor controllerAdaptor, int maxThreads) {
        this(controllerAdaptor, DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, maxThreads);
    }

    public Connector(ControllerAdaptor controllerAdaptor, int port, int acceptCount, int maxThreads) {
        this.controllerAdaptor = controllerAdaptor;
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.executorService = Executors.newFixedThreadPool(maxThreads);
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

    public void start() {
        var thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        stopped = false;
        log.info("Web Application Server started {} port.", serverSocket.getLocalPort());
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

    private void process(Socket connection) {
        if (connection == null) {
            return;
        }
        var processor = new Http11Processor(connection, controllerAdaptor);
        executorService.submit(processor);
//        new Thread(processor).start();
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
        var MIN_PORT = 1;
        var MAX_PORT = 65535;

        if (port < MIN_PORT || MAX_PORT < port) {
            return DEFAULT_PORT;
        }
        return port;
    }

    private int checkAcceptCount(int acceptCount) {
        return Math.max(acceptCount, DEFAULT_ACCEPT_COUNT);
    }
}
