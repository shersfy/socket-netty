package org.shersfy.server.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
/**
 * WebServer启动入口
 * @author py
 * 2018年7月5日
 */
@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages="org.shersfy.server")
public class SocketServerApplication {
    
	public static void main(String[] args) {
		SpringApplication.run(SocketServerApplication.class, args);
	}

}
