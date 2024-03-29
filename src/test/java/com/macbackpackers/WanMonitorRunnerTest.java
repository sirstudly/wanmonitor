package com.macbackpackers;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WanMonitorRunnerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger( WanMonitorRunnerTest.class );

    @Test
    public void testIsTraceRouteSuccessful() throws Exception {
        ModemConfigProperties properties = new ModemConfigProperties();
        MonitorConfig config = new MonitorConfig();
        config.setCommand( "curl -m 3 -s --interface en0 https://wtfismyip.com/text" );
        config.setIpRegex( "(\\d+\\.\\d+\\.\\d+\\.\\d+)" );
        config.setIpRegexMatch( ".*" );
        properties.setName( "WAN TEST" );
        properties.setMonitor( config );
        WanMonitorRunner monitor = new WanMonitorRunner( properties, null );
        LOGGER.info( "is traceroute successful returned " + monitor.isTraceRouteSuccessful() );
    }
}
