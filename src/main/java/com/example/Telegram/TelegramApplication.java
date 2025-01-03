package com.example.Telegram;

import com.example.Telegram.service.socket.ServerManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;


@SpringBootApplication
public class TelegramApplication {

    public static void main(String[] args) throws IOException {
        ApplicationContext context = SpringApplication.run(TelegramApplication.class, args);
        ServerManager serverManager = context.getBean(ServerManager.class);
        serverManager.startServer();

    }
}

