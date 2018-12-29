package edu.ka.testingbed.security.x509;

import edu.ka.testingbed.security.x509.config.X509Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = X509Configuration.class)
public class X509Main {
    public static void main(String[] args) {
        SpringApplication.run(X509Main.class, args);
    }
}