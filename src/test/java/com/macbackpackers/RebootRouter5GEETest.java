package com.macbackpackers;

import org.junit.jupiter.api.Test;

public class RebootRouter5GEETest {

    @Test
    public void testRebootRouter() {
        RouterConfig config = new RouterConfig();
        config.setUrl( "https://192.168.5.1" );
        config.setPassword( "XXXXXX" );
        new RebootRouter5GEE().reboot( config );
    }
}
