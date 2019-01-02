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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * Sample class to test Secret Management functionality.
 *
 * This class assumes {@link edu.ka.testingbed.security.x509.server.X509Main}
 * is running in the background for it to work properly.
 */
@SpringBootApplication(scanBasePackageClasses = SampleClient.class)//(exclude = {EmbeddedServletContainerAutoConfiguration.class, DispatcherServletAutoConfiguration.class })
public class SampleClient
{

    public static void main(String[] args)
    {
//        ApplicationContext context = new AnnotationConfigApplicationContext(SampleClientConfiguration.class);
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
                                         @Value("${server.ssl.trust-store-password}") char[] truststorePass,
                                         @Value("${server.ssl.trust-store}") String truststorePath) throws Exception {
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(keyStore(truststorePath, truststorePass), truststorePass)
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();

            HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();
            return builder
                    .requestFactory(new HttpComponentsClientHttpRequestFactory(client))
                    .build();
        }

        private KeyStore keyStore(String file, char[] password) throws Exception {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            File key = ResourceUtils.getFile(file);
            try (InputStream in = new FileInputStream(key)) {
                keyStore.load(in, password);
            }
            return keyStore;
        }
    }
}
