
package com.macbackpackers;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class RebootRouterHuaweiB593s {

    private static final Logger LOGGER = LoggerFactory.getLogger( RebootRouterHuaweiB593s.class );
    private Properties properties = new Properties();

    public static void main( String argv[] ) throws Exception {
        Properties props = new Properties();
        props.load( RebootRouterHuaweiB593s.class.getClassLoader().getResourceAsStream( "config.properties" ) );
        new RebootRouterHuaweiB593s( props ).rebootRouter();
    }

    public RebootRouterHuaweiB593s( Properties props ) throws Exception {
        properties = props;
    }

    public void rebootRouter() throws IOException {
        String routerUrl = properties.getProperty( "router.url" );
        LOGGER.info( "Rebooting " + routerUrl );
        try (WebClient webClient = getWebClient()) {
            HtmlPage routerPage = webClient.getPage( routerUrl );
            webClient.waitForBackgroundJavaScript( 2000 );

            LOGGER.info( "Logging in..." );
            HtmlInput usernameInput = routerPage.getHtmlElementById( "txt_Username" );
            HtmlInput passwordInput = routerPage.getHtmlElementById( "txt_Password" );
            usernameInput.setValueAttribute( properties.getProperty( "router.username" ) );
            passwordInput.setValueAttribute( properties.getProperty( "router.password" ) );
            routerPage = routerPage.getHtmlElementById( "login_button" ).click();
            webClient.waitForBackgroundJavaScript( 4000 );

            String maintenanceURL = routerUrl + "/html/management/maintenance.asp";
            LOGGER.info( "Navigating to " + maintenanceURL );
            routerPage = webClient.getPage( maintenanceURL );
            webClient.waitForBackgroundJavaScript( 2000 );

            LOGGER.info( "Clicking on Restart button..." );
            routerPage = routerPage.getHtmlElementById( "btnReboot" ).click();
            webClient.waitForBackgroundJavaScript( 2000 );

            if ( routerPage.asXml().contains( "A reiniciar... Aguarde." ) ) {
                LOGGER.info( "The device is rebooting..." );
            }
            else {
                LOGGER.info( routerPage.asXml() );
                LOGGER.info( "Weirdness... I think something unexpected happened." );
            }
        }
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
        return webClient;
    }

}
