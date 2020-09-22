package com.arun.springclientaccessingsslenabledrestservice.client;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import javax.net.ssl.SSLException;

/**
 * @author arun on 9/22/20
 */

@Configuration
public class StudentWebClientConfig {


    @Bean
    public WebClient studentSSLDisabledWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        TcpClient tcpClient = TcpClient.create().secure(t -> t.sslContext(sslContext));
        HttpClient httpClient = HttpClient.from(tcpClient);
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }


    @Bean
    public WebClient studentWebClient() {
        return WebClient.builder().build();
    }
}
