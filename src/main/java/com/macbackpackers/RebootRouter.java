
package com.macbackpackers;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

public class RebootRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger( RebootRouter.class );
    private Properties properties = new Properties();

    public static void main( String argv[] ) throws Exception {
        new RebootRouter().rebootRouter();
    }
    
    public RebootRouter() throws Exception {
        properties.load( getClass().getClassLoader().getResourceAsStream( "config.properties" ) );
    }

    private void rebootRouter() throws IOException {
        String routerUrl = properties.getProperty( "router.url" );
        LOGGER.info( "Rebooting " + routerUrl );
        HtmlPage routerPage = getWebClient().getPage( routerUrl );
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
