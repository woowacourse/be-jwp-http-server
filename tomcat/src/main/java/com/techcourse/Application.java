package com.techcourse;

import org.apache.catalina.controller.ControllerRegistry;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        ControllerRegistry.registerControllers();
        final var tomcat = new Tomcat();
        tomcat.start();
    }
}
