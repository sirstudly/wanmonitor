package com.macbackpackers;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WanMonitorRunner implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger( WanMonitorRunner.class );
    private final ModemConfigProperties properties;
    private final RebootRouter router;

    public WanMonitorRunner( ModemConfigProperties properties, RebootRouter router ) {
        this.properties = properties;
        this.router = router;
    }

    @Override
    public void run() {

        MDC.put( "monitorName", properties.getName() );
        LOGGER.info( "Starting monitoring for " + properties.getName() );
        final int MAX_FAILURES = properties.getMonitor().getMaxFailuresBeforeReboot();
        int numberFailures = 0;
        int maxNumberFailures = MAX_FAILURES;

        // loop forever
        try {
            for ( ; ; sleep() ) {

                // reset if we're successful
                if ( isTraceRouteSuccessful() ) {
                    LOGGER.debug( "Situation nominal" );
                    maxNumberFailures = MAX_FAILURES;
                    numberFailures = 0;
                }
                else {
                    LOGGER.info( "Uh oh...there's trouble in paradise." );
                    numberFailures++;
                }

                // exponential backoff; if modem or line is f*cked, don't just restart it every X minutes
                if ( numberFailures > maxNumberFailures ) {
                    LOGGER.info( "Over " + maxNumberFailures + " failures... Rebooting router." );
                    rebootRouter();
                    numberFailures = 0; // reset
                    maxNumberFailures *= 2;
                }
            }
        }
        finally {
            MDC.remove( "monitorName" );
        }
    }

    private void sleep() {
        try {
            Thread.sleep( properties.getMonitor().getPeriodSeconds() * 1000L );
        }
        catch ( InterruptedException ex ) {
            // awake
        }
    }

    public boolean isTraceRouteSuccessful() {
        final Pattern p = Pattern.compile( properties.getMonitor().getIpRegex(), Pattern.DOTALL );
        try {
            String out = runCommand( properties.getMonitor().getCommand() );
            Matcher m = p.matcher( out );
            if ( m.find() ) {
                if ( m.group( 1 ).matches( properties.getMonitor().getIpRegexMatch() ) ) {
                    LOGGER.debug( "MATCHED: " + m.group( 1 ) );
                }
                else {
                    LOGGER.info( "NOT MATCHED: traceroute check failed." );
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
            LOGGER.info( properties.getName() + " failed.", e );
            return false;
        }
    }

    private String runCommand( String commandLine ) throws IOException {
        LOGGER.debug( "Attempting to run command: " + commandLine );
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
            router.reboot( properties.getRouter() );
        }
        catch( Exception ex ) {
            LOGGER.error( "Failed to reboot router.", ex );
        }
    }

}
