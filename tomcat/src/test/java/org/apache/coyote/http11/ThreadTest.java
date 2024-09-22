package org.apache.coyote.http11;

import org.apache.catalina.connector.Connector;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ThreadTest {

    @Test
    @DisplayName("스레드의 수만큼 스레드풀의이 생성된다.")
    void threadPoolSize() {
        Connector connector = new Connector(0, 0, 2);

        int poolSize = connector.getPoolSize();

        Assertions.assertThat(poolSize).isSameAs(2);
    }

    @Test
    @DisplayName("스레드가 모두 바쁘면 처리되지 못하고 queue에서 대기한다.")
    void go() {
        Connector connector = new Connector(0, 0, 2);
        connector.submit(System.out::println);
        connector.submit(System.out::println);

        connector.submit(System.out::println);
        connector.submit(System.out::println);

        Assertions.assertThat(connector.getQueueSize()).isEqualTo(2);
    }
}
