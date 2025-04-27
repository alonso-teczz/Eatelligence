package com.alonso.eatelligence;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling
public class EatelligenceApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry ->
            System.setProperty(entry.getKey(), entry.getValue())
        );
        SpringApplication.run(EatelligenceApplication.class, args);
    }

	//! Ejecuta el navegador automáticamente cuando se inicie la aplicacion
	@EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        String url = "http://localhost:8080";
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open " + url);
            } else if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec("xdg-open " + url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
