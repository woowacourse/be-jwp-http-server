package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public interface RequestManager {

    String generateResponse() throws IOException;
}
