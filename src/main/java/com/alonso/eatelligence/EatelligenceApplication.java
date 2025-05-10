package com.alonso.eatelligence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class EatelligenceApplication {

    public static void main(String[] args) {
        // Dotenv dotenv = Dotenv.load();
        // dotenv.entries().forEach(entry ->
        //     System.setProperty(entry.getKey(), entry.getValue())
        // );
        System.out.println(">> APP STARTING <<");
        SpringApplication.run(EatelligenceApplication.class, args);
    }

    @EventListener
    public void onReady(ApplicationReadyEvent event) {
        var ctx = event.getApplicationContext();
        if (ctx instanceof ServletWebServerApplicationContext serverCtx) {
            int port = serverCtx.getWebServer().getPort();
            System.out.println(">> APPLICATION READY ON PORT: " + port);
        } else {
            System.out.println(">> APPLICATION READY, but not a WebServerApplicationContext");
        }
    }

	//! Ejecuta el navegador automáticamente cuando se inicie la aplicacion
	// @EventListener(ApplicationReadyEvent.class)
    // public void openBrowser() {
    //     String url = "http://localhost:8080";
    //     String os = System.getProperty("os.name").toLowerCase();
    //     try {
    //         if (os.contains("win")) {
    //             Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
    //         } else if (os.contains("mac")) {
    //             Runtime.getRuntime().exec("open " + url);
    //         } else if (os.contains("nix") || os.contains("nux")) {
    //             Runtime.getRuntime().exec("xdg-open " + url);
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
}
