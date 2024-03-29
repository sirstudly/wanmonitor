
package com.macbackpackers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class WanMonitor implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger( WanMonitor.class );

    @Autowired
    private ApplicationContext context;

    public static void main( String args[] ) throws Exception {
        System.getProperties().load( WanMonitor.class.getClassLoader().getResourceAsStream( "application.properties" ) );
        SpringApplication.run( WanMonitor.class, args );
    }

    @Override
    public void run( String... args ) {

        WanMonitorConfiguration config = context.getBean( WanMonitorConfiguration.class );
        if ( config.isChromeTest() ) {
            doChromeTest();
        }

        List<Thread> threads = (List<Thread>) context.getBean( "workers" );
        LOGGER.info( "Found " + threads.size() + " workers" );

        LOGGER.info( "Kicking off threads..." );
        ExecutorService executorService = Executors.newCachedThreadPool();
        threads.forEach( executorService::execute );

        // the worker threads don't ever terminate so we'll just block forever here...
        LOGGER.info( "Waiting for threads to finish..." );
        executorService.shutdown();
    }

    private void doChromeTest() {
        ChromeWebDriver chrome = context.getBean( ChromeWebDriver.class );
        try {
            LOGGER.info( "Checking able to launch Chrome!" );
            chrome.getWebDriver().get( "https://www3.pioneer.com/argentina/SIC/test.html" );
            LOGGER.info( "DONE! " + chrome.getWebDriver().getPageSource() );
        }
        finally {
            chrome.getWebDriver().close();
        }
    }

    @Bean( name = "workers" )
    public List<Thread> getWorkerThreads( WanMonitorConfiguration yamlProperties ) {
        return yamlProperties.getModems().stream().map( m ->
                new Thread( new WanMonitorRunner( m, (RebootRouter) context.getBean( m.getBeanname() ) ) ) ).toList();
    }
}
