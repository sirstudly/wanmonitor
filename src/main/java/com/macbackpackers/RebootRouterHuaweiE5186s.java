
package com.macbackpackers;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

public class RebootRouterHuaweiE5186s implements RebootRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger( RebootRouterHuaweiE5186s.class );

    public static void main( String argv[] ) throws Exception {
        Properties props = new Properties();
        props.load( RebootRouterHuaweiE5186s.class.getClassLoader().getResourceAsStream( "config.properties" ) );
        new RebootRouterHuaweiE5186s().rebootRouter( props );
    }

    @Override
    public void rebootRouter( Properties properties ) throws IOException {
        String routerUrl = properties.getProperty( "router.url" );
        LOGGER.info( "Rebooting " + routerUrl );
        try( WebClient webClient = getWebClient() ) {
            HtmlPage routerPage = webClient.getPage( routerUrl );
            routerPage = routerPage.getElementById( "logout_span" ).click(); // actually is Log in
            HtmlInput usernameInput = routerPage.getHtmlElementById( "username" );
            HtmlInput passwordInput = routerPage.getHtmlElementById( "password" );
            usernameInput.setValueAttribute( properties.getProperty( "router.username" ) );
            passwordInput.setValueAttribute( properties.getProperty( "router.password" ) );
            HtmlSpan loginBtn = routerPage.getHtmlElementById( "pop_login" );

            LOGGER.info( "Logging in..." );
            routerPage = loginBtn.click();
            webClient.waitForBackgroundJavaScript( 8000 );
    
            LOGGER.info( "Navigating to settings page..." );
            HtmlAnchor settingsLink = routerPage.getHtmlElementById( "settings" );
            routerPage = settingsLink.click();
            webClient.waitForBackgroundJavaScript( 1000 );
    
            LOGGER.info( "Clicking on Systems side-menu option..." );
            HtmlListItem systemMenu = routerPage.getHtmlElementById( "system" );
            routerPage = systemMenu.click();
            webClient.waitForBackgroundJavaScript( 1000 );
    
            LOGGER.info( "Clicking on Reboot side-menu option..." );
            HtmlListItem rebootMenu = routerPage.getHtmlElementById( "reboot" );
            routerPage = rebootMenu.click();
            webClient.waitForBackgroundJavaScript( 1000 );
    
            LOGGER.info( "Clicking on Restart button..." );
            HtmlAnchor restartBtn = HtmlAnchor.class.cast( rebootMenu.getFirstElementChild() );
            routerPage = restartBtn.click();
            webClient.waitForBackgroundJavaScript( 1000 );
    
            LOGGER.info( "Clicking on Apply button..." );
            HtmlLabel applyBtn = routerPage.getHtmlElementById( "button_reboot" );
            routerPage = applyBtn.click();
            webClient.waitForBackgroundJavaScript( 2000 );
    
            LOGGER.info( "Clicking on Confirm..." );
            HtmlSpan confirmBtn = routerPage.getHtmlElementById( "pop_confirm" );
            routerPage = confirmBtn.click();
            webClient.waitForBackgroundJavaScript( 10000 );
    
            if ( routerPage.asXml().contains( "The device is rebooting" ) ) {
                LOGGER.info( "The device is rebooting..." );
            }
            else {
                LOGGER.debug( routerPage.asXml() );
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
