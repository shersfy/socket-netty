package org.shersfy.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix="netty.config")
public class NettyConfig {
    
    private int port;
    private String loggerLevel;
    
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public String getLoggerLevel() {
        return loggerLevel;
    }
    public void setLoggerLevel(String loggerLevel) {
        this.loggerLevel = loggerLevel;
    }

}
