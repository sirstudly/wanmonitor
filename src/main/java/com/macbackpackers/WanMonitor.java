
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

public class WanMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger( WanMonitor.class );
    private Properties properties = new Properties();

    public static void main( String argv[] ) throws Exception {
        new WanMonitor().run();
    }
    
    public WanMonitor() throws Exception {
        properties.load( getClass().getClassLoader().getResourceAsStream( "config.properties" ) );
    }

    public void run() throws Exception {

        final int MAX_FAILURES = Integer.parseInt( properties.getProperty( "max.failures.before.reboot" ) );
        int numberFailures = 0;
        int maxNumberFailures = MAX_FAILURES;

        for ( ;; sleep() ) {

            // reset if we're successful
            if ( isTraceRouteSuccessful() ) {
                LOGGER.debug( "Situation nominal" );
                maxNumberFailures = MAX_FAILURES;
                numberFailures = 0;
            }
            else {
                LOGGER.debug( "Uh oh..." );
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
        final Pattern p = Pattern.compile( properties.getProperty( "monitor.ip.regex" ), Pattern.DOTALL );
        try {
            String out = runCommand( properties.getProperty( "monitor.command" ) );
            Matcher m = p.matcher( out );
            if ( m.find() ) {
                if ( m.group( 1 ).matches( properties.getProperty( "monitor.ip.regex.match" ) ) ) {
                    LOGGER.debug( "MATCHED: " + m.group( 1 ) );
                }
                else {
                    LOGGER.info( "tracert no longer goes through modem." );
                    LOGGER.info( out );
                    return false;
                }
            }
            else {
                LOGGER.info( "No regex match??" );
                LOGGER.info( out );
                return false;
            }
            return true;
        }
        catch ( IOException e ) {
            LOGGER.info( "WAN failed.", e );
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

    private void rebootRouter() {
        try {
            if( properties.containsKey( "chromescraper.maxwait.seconds" )) {
                new RebootRouterHuaweiB525( properties ).rebootRouter();
            }
            else {
                new RebootRouter( properties ).rebootRouter();
            }
        }
        catch( Exception ex ) {
            LOGGER.error( "Failed to reboot router.", ex );
        }
    }

}
