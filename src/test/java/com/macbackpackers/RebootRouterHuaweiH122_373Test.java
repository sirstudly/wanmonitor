package com.macbackpackers;

import org.junit.jupiter.api.Test;

public class RebootRouterHuaweiH122_373Test {

    @Test
    public void testRebootRouter() throws Exception {
        RouterConfig config = new RouterConfig();
        config.setUrl( "https://192.168.5.1" );
        config.setPassword( "XXXXXX" );
        new RebootRouterHuaweiH122_373().reboot( config );
    }
}
