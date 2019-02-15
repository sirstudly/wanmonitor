
package com.macbackpackers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

public class WanMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger( WanMonitor.class );
    private Properties properties = new Properties();

    public static void main( String argv[] ) throws Exception {
        new WanMonitor().run();
    }
    
    public WanMonitor() throws Exception {
        properties.load( getClass().getClassLoader().getResourceAsStream( "config.properties" ) );
    }

    public void run() throws IOException {

        final int MAX_FAILURES = Integer.parseInt( properties.getProperty( "max.failures.before.reboot" ) );
        int numberFailures = 0;
        int maxNumberFailures = MAX_FAILURES;

        for ( ;; sleep() ) {

            // reset if we're successful
            if ( isTraceRouteSuccessful() ) {
                maxNumberFailures = MAX_FAILURES;
                numberFailures = 0;
            }
            else {
                numberFailures++;
            }

            if ( numberFailures > maxNumberFailures ) {
                LOGGER.info( "Over " + maxNumberFailures + " failures... Rebooting router." );
                rebootRouter();
                numberFailures = 0; // reset
                maxNumberFailures *= 2;
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep( Integer.parseInt( properties.getProperty( "monitor.period.seconds" ) ) * 1000 );
        }
        catch ( InterruptedException ex ) {
            // awake
        }
    }

    private boolean isTraceRouteSuccessful() {
        final Pattern p = Pattern.compile( properties.getProperty( "monitor.ip.regex" ) );
        try {
            String out = runCommand( properties.getProperty( "monitor.command" ) );
            Matcher m = p.matcher( out );
            if ( m.find() ) {
                if ( false == m.group( 1 ).matches( properties.getProperty( "monitor.ip.regex.match" ) ) ) {
                    LOGGER.info( "tracert no longer goes through modem." );
                    LOGGER.info( out );
                    return false;
                }
            }
            else {
                LOGGER.info( "No regex match??" );
                LOGGER.info( out );
            }
            return true;
        }
        catch ( IOException e ) {
            LOGGER.info( "WAN failed." );
            return false;
        }
    }

    private String runCommand( String commandLine ) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CommandLine commandline = CommandLine.parse( commandLine );
        DefaultExecutor exec = new DefaultExecutor();
        PumpStreamHandler streamHandler = new PumpStreamHandler( outputStream );
        exec.setStreamHandler( streamHandler );
        exec.execute( commandline );
        return outputStream.toString();
    }

    private void rebootRouter() throws IOException {
        HtmlPage routerPage = getWebClient().getPage( properties.getProperty( "router.url" ) );
        HtmlSpan rebootSpan = routerPage.getFirstByXPath( "//span[text()='Reboot']" );
        routerPage = rebootSpan.click();
    }

    private WebClient getWebClient() {
        WebClient webClient = new WebClient( BrowserVersion.CHROME ); // return a new instance of this when requested
        webClient.getOptions().setThrowExceptionOnFailingStatusCode( false );
        webClient.getOptions().setThrowExceptionOnScriptError( false );
        webClient.getOptions().setJavaScriptEnabled( true );
        webClient.getOptions().setCssEnabled( false );
        webClient.getOptions().setRedirectEnabled( true );
        webClient.getOptions().setUseInsecureSSL( true );
        webClient.setAjaxController( new NicelyResynchronizingAjaxController() );
        webClient.getOptions().setTimeout( 60000 );
        webClient.setJavaScriptTimeout( 60000 );

        DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
        credentialsProvider.addCredentials( properties.getProperty( "router.username" ), properties.getProperty( "router.password" ) );
        return webClient;
    }
}
