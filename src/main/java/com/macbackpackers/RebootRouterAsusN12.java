
package com.macbackpackers;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RebootRouterAsusN12 implements RebootRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger( RebootRouterAsusN12.class );

    @Autowired
    private ApplicationContext context;

    @Override
    public void reboot( RouterConfig config ) throws IOException {
        String routerUrl = config.getUrl();
        LOGGER.info( "Rebooting " + routerUrl );
        try ( WebClient webClient = context.getBean( WebClient.class ) ) {
            DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
            credentialsProvider.addCredentials( config.getUsername(), config.getPassword() );
            HtmlPage routerPage = webClient.getPage( routerUrl );
            HtmlSpan rebootSpan = routerPage.getFirstByXPath( "//span[text()='Reboot']" );
            rebootSpan.click();
        }
    }
}
