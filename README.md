# springclientaccessingsslenabledrestservice

## We create 2 webclients 

1. one where the SSL is disabled

2. Normal WebClient


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


WebClient defined in controller


        package com.arun.springclientaccessingsslenabledrestservice.controller;
        
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RestController;
        import org.springframework.web.reactive.function.client.WebClient;
        import reactor.core.publisher.Flux;
        
        /**
         * @author arun on 9/22/20
         */
        
        @RestController
        public class StudentController {
        
            private final WebClient studentSSLDisabledWebClient;
            private final WebClient studentWebClient;
        
            @Autowired
            public StudentController(WebClient studentSSLDisabledWebClient, WebClient studentWebClient) {
                this.studentSSLDisabledWebClient = studentSSLDisabledWebClient;
                this.studentWebClient = studentWebClient;
            }
        
        
            @GetMapping("/v2/student")
            public ResponseEntity<String> getStudent() {
                Flux<String> stringFlux = studentSSLDisabledWebClient.get().uri("https://localhost:8443/v1/student")
                        .retrieve()
                        .bodyToFlux(String.class);
        
                String studentDate = stringFlux.blockFirst();
                System.out.println(studentDate);
                return ResponseEntity.ok(studentDate);
            }
        
            @GetMapping("/v3/student")
            public ResponseEntity<String> getSSLEnabledStudent() {
                Flux<String> stringFlux = studentWebClient.get().uri("https://localhost:8443/v1/student")
                        .retrieve()
                        .bodyToFlux(String.class);
        
                String studentDate = stringFlux.blockFirst();
                System.out.println(studentDate);
                return ResponseEntity.ok(studentDate);
            }
        }
 
 
 Here /v3/student will give an error as below
 
        {
            "timestamp": "2020-09-22T14:59:17.978+00:00",
            "status": 500,
            "error": "Internal Server Error",
            "trace": "reactor.core.Exceptions$ReactiveException: javax.net.ssl.SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target\n\tat reactor.core.Exceptions.propagate(Exceptions.java:393)\n\tat reactor.core.publisher.BlockingSingleSubscriber.blockingGet(BlockingSingleSubscriber.java:97)\n\tat reactor.core.publisher.Flux.blockFirst(Flux.java:2452)\n\tat com.arun.springclientaccessingsslenabledrestservice.controller.StudentController.getSSLEnabledStudent(StudentController.java:44)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.base/java.lang.reflect.Method.invoke(Method.java:566)\n\tat org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:190)\n\tat org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:138)\n\tat org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:105)\n\tat org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:878)\n\tat org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:792)\n\tat org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\n\tat org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1040)\n\tat org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:943)\n\tat org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006)\n\tat org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:898)\n\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:626)\n\tat org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883)\n\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:733)\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n\tat org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n\tat org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n\tat org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n\tat org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\n\tat org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:202)\n\tat org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:97)\n\tat org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:541)\n\tat org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:143)\n\tat org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)\n\tat org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:78)\n\tat org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343)\n\tat org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:374)\n\tat org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65)\n\tat org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:868)\n\tat org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1590)\n\tat org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)\n\tat java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)\n\tat java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)\n\tat org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)\n\tat java.base/java.lang.Thread.run(Thread.java:834)\n\tSuppressed: java.lang.Exception: #block terminated with an error\n\t\tat reactor.core.publisher.BlockingSingleSubscriber.blockingGet(BlockingSingleSubscriber.java:99)\n\t\t... 52 more\nCaused by: javax.net.ssl.SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target\n\tat java.base/sun.security.ssl.Alert.createSSLException(Alert.java:131)\n\tSuppressed: reactor.core.publisher.FluxOnAssembly$OnAssemblyException: \nError has been observed at the following site(s):\n\t|_ checkpoint ⇢ Request to GET https://localhost:8443/v1/student [DefaultWebClient]\nStack trace:\n\t\tat java.base/sun.security.ssl.Alert.createSSLException(Alert.java:131)\n\t\tat java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:327)\n\t\tat java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:270)\n\t\tat java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:265)\n\t\tat java.base/sun.security.ssl.CertificateMessage$T13CertificateConsumer.checkServerCerts(CertificateMessage.java:1344)\n\t\tat java.base/sun.security.ssl.CertificateMessage$T13CertificateConsumer.onConsumeCertificate(CertificateMessage.java:1219)\n\t\tat java.base/sun.security.ssl.CertificateMessage$T13CertificateConsumer.consume(CertificateMessage.java:1162)\n\t\tat java.base/sun.security.ssl.SSLHandshake.consume(SSLHandshake.java:392)\n\t\tat java.base/sun.security.ssl.HandshakeContext.dispatch(HandshakeContext.java:451)\n\t\tat java.base/sun.security.ssl.SSLEngineImpl$DelegatedTask$DelegatedAction.run(SSLEngineImpl.java:1078)\n\t\tat java.base/sun.security.ssl.SSLEngineImpl$DelegatedTask$DelegatedAction.run(SSLEngineImpl.java:1065)\n\t\tat java.base/java.security.AccessController.doPrivileged(Native Method)\n\t\tat java.base/sun.security.ssl.SSLEngineImpl$DelegatedTask.run(SSLEngineImpl.java:1012)\n\t\tat io.netty.handler.ssl.SslHandler.runAllDelegatedTasks(SslHandler.java:1550)\n\t\tat io.netty.handler.ssl.SslHandler.runDelegatedTasks(SslHandler.java:1564)\n\t\tat io.netty.handler.ssl.SslHandler.unwrap(SslHandler.java:1448)\n\t\tat io.netty.handler.ssl.SslHandler.decodeJdkCompatible(SslHandler.java:1275)\n\t\tat io.netty.handler.ssl.SslHandler.decode(SslHandler.java:1322)\n\t\tat io.netty.handler.codec.ByteToMessageDecoder.decodeRemovalReentryProtection(ByteToMessageDecoder.java:501)\n\t\tat io.netty.handler.codec.ByteToMessageDecoder.callDecode(ByteToMessageDecoder.java:440)\n\t\tat io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:276)\n\t\tat io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)\n\t\tat io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)\n\t\tat io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)\n\t\tat io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1410)\n\t\tat io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)\n\t\tat io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)\n\t\tat io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:919)\n\t\tat io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:166)\n\t\tat io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:714)\n\t\tat io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:650)\n\t\tat io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:576)\n\t\tat io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:493)\n\t\tat io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:989)\n\t\tat io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)\n\t\tat io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)\n\t\tat java.base/java.lang.Thread.run(Thread.java:834)\nCaused by: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target\n\tat java.base/sun.security.validator.PKIXValidator.doBuild(PKIXValidator.java:439)\n\tat java.base/sun.security.validator.PKIXValidator.engineValidate(PKIXValidator.java:306)\n\tat java.base/sun.security.validator.Validator.validate(Validator.java:264)\n\tat java.base/sun.security.ssl.X509TrustManagerImpl.validate(X509TrustManagerImpl.java:313)\n\tat java.base/sun.security.ssl.X509TrustManagerImpl.checkTrusted(X509TrustManagerImpl.java:276)\n\tat java.base/sun.security.ssl.X509TrustManagerImpl.checkServerTrusted(X509TrustManagerImpl.java:141)\n\tat java.base/sun.security.ssl.CertificateMessage$T13CertificateConsumer.checkServerCerts(CertificateMessage.java:1322)\n\tat java.base/sun.security.ssl.CertificateMessage$T13CertificateConsumer.onConsumeCertificate(CertificateMessage.java:1219)\n\tat java.base/sun.security.ssl.CertificateMessage$T13CertificateConsumer.consume(CertificateMessage.java:1162)\n\tat java.base/sun.security.ssl.SSLHandshake.consume(SSLHandshake.java:392)\n\tat java.base/sun.security.ssl.HandshakeContext.dispatch(HandshakeContext.java:451)\n\tat java.base/sun.security.ssl.SSLEngineImpl$DelegatedTask$DelegatedAction.run(SSLEngineImpl.java:1078)\n\tat java.base/sun.security.ssl.SSLEngineImpl$DelegatedTask$DelegatedAction.run(SSLEngineImpl.java:1065)\n\tat java.base/java.security.AccessController.doPrivileged(Native Method)\n\tat java.base/sun.security.ssl.SSLEngineImpl$DelegatedTask.run(SSLEngineImpl.java:1012)\n\tat io.netty.handler.ssl.SslHandler.runAllDelegatedTasks(SslHandler.java:1550)\n\tat io.netty.handler.ssl.SslHandler.runDelegatedTasks(SslHandler.java:1564)\n\tat io.netty.handler.ssl.SslHandler.unwrap(SslHandler.java:1448)\n\tat io.netty.handler.ssl.SslHandler.decodeJdkCompatible(SslHandler.java:1275)\n\tat io.netty.handler.ssl.SslHandler.decode(SslHandler.java:1322)\n\tat io.netty.handler.codec.ByteToMessageDecoder.decodeRemovalReentryProtection(ByteToMessageDecoder.java:501)\n\tat io.netty.handler.codec.ByteToMessageDecoder.callDecode(ByteToMessageDecoder.java:440)\n\tat io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:276)\n\tat io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)\n\tat io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)\n\tat io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357)\n\tat io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1410)\n\tat io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379)\n\tat io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365)\n\tat io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:919)\n\tat io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:166)\n\tat io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:714)\n\tat io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:650)\n\tat io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:576)\n\tat io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:493)\n\tat io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:989)\n\tat io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)\n\tat io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)\n\tat java.base/java.lang.Thread.run(Thread.java:834)\nCaused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target\n\tat java.base/sun.security.provider.certpath.SunCertPathBuilder.build(SunCertPathBuilder.java:141)\n\tat java.base/sun.security.provider.certpath.SunCertPathBuilder.engineBuild(SunCertPathBuilder.java:126)\n\tat java.base/java.security.cert.CertPathBuilder.build(CertPathBuilder.java:297)\n\tat java.base/sun.security.validator.PKIXValidator.doBuild(PKIXValidator.java:434)\n\t... 38 more\n",
            "message": "javax.net.ssl.SSLHandshakeException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target",
            "path": "/v3/student"
        }
    
 where /v2/student will provide a proper response
 
 Hello Student