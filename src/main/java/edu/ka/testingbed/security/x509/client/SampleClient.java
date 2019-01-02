package edu.ka.testingbed.security.x509.client;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * Sample class to test Secret Management functionality.
 *
 * This class assumes {@link edu.ka.testingbed.security.x509.server.X509Main}
 * is running in the background for it to work properly.
 *
 */
@SpringBootApplication(scanBasePackageClasses = SampleClient.class)
public class SampleClient
{

    public static void main(String[] args)
    {
        ApplicationContext context = new SpringApplicationBuilder().
                web(false).
                sources(SampleClient.class).
                run(args);

        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        String responseEntity = restTemplate.getForObject("https://localhost:8443/greeting", String.class);

        System.out.println("Rest response is " + responseEntity);
    }

    @Configuration
    public static class SampleClientConfiguration
    {
        @Bean
        public RestTemplate restTemplate(RestTemplateBuilder builder,
                                         @Value("${server.ssl.trust-store-password}") String truststorePass,
                                         @Value("${server.ssl.trust-store}") Resource truststore,
                                         @Value("${server.ssl.key-store-password}") char[] keystorePass,
                                         @Value("${server.ssl.key-store}") String keystorePath) throws Exception {

            setTruststoreProperties(truststore, truststorePass);

            SSLContext sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(keyStore(keystorePath, keystorePass), keystorePass)
                    .build();

            HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();
            return builder
                    .requestFactory(new HttpComponentsClientHttpRequestFactory(client))
                    .build();
        }

        private void setTruststoreProperties(Resource truststore, String truststorePass) throws IOException {
            String truststorePath = truststore.getFile().getPath();

            System.setProperty("javax.net.ssl.trustStore", truststorePath);
            System.setProperty("javax.net.ssl.trustStorePassword", truststorePass);
        }

        private KeyStore keyStore(String file, char[] password) throws Exception {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            File key = ResourceUtils.getFile(file);
            try (InputStream in = new FileInputStream(key)) {
                keyStore.load(in, password);
            }
            return keyStore;
        }
    }
}
