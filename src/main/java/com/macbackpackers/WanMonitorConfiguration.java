package com.macbackpackers;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import java.util.List;

@Configuration
@ConfigurationProperties
@PropertySources( {
        // priority is in reverse order
        @PropertySource( value = "classpath:config.yaml", factory = YamlPropertySourceFactory.class ),
        @PropertySource( value = "file:./config.yaml", factory = YamlPropertySourceFactory.class, ignoreResourceNotFound = true )
} )
public class WanMonitorConfiguration {

    private String chromeOptions;
    private int chromeMaxWaitSeconds;
    private boolean chromeTest;
    private List<ModemConfigProperties> modems;

    public String getChromeOptions() {
        return chromeOptions;
    }

    public void setChromeOptions( String chromeOptions ) {
        this.chromeOptions = chromeOptions;
    }

    public int getChromeMaxWaitSeconds() {
        return chromeMaxWaitSeconds;
    }

    public void setChromeMaxWaitSeconds( int chromeMaxWaitSeconds ) {
        this.chromeMaxWaitSeconds = chromeMaxWaitSeconds;
    }

    public boolean isChromeTest() {
        return chromeTest;
    }

    public void setChromeTest( boolean chromeTest ) {
        this.chromeTest = chromeTest;
    }

    public List<ModemConfigProperties> getModems() {
        return modems;
    }

    public void setModems( List<ModemConfigProperties> modems ) {
        this.modems = modems;
    }
}
