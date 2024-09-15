package org.apache.catalina.connector;

import org.apache.coyote.ServletContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectorTest {

    @Test
    @DisplayName("ExcutorService가 정상적으로 종료되는지 확인하는 테스트 작성")
    void stopConnector() {
        Connector connector = new Connector(new ServletContainer(), 8081, 100, 250);
        connector.stop();
        assertThat(connector.isShutdown()).isTrue();
    }
}
