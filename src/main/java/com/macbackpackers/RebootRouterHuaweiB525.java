
package com.macbackpackers;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class RebootRouterHuaweiB525 {

    private static final Logger LOGGER = LoggerFactory.getLogger( RebootRouterHuaweiB525.class );
    private Properties properties = new Properties();

    public static void main( String argv[] ) throws Exception {
        Properties props = new Properties();
        props.load( RebootRouterHuaweiB525.class.getClassLoader().getResourceAsStream( "config.properties" ) );
        new RebootRouterHuaweiB525( props ).rebootRouter();
    }
    
    public RebootRouterHuaweiB525( Properties props ) throws Exception {
        properties = props;
    }

    public void rebootRouter() throws IOException {
        String routerUrl = properties.getProperty( "router.url" );
        LOGGER.info( "Rebooting " + routerUrl );
        WebClient webClient = getWebClient();
        HtmlPage routerPage = webClient.getPage( routerUrl );
        LOGGER.info( routerPage.asXml() );
        routerPage = routerPage.getElementById( "logout_span" ).click(); // actually is Log in
        LOGGER.info( "LOGIN: " + routerPage.asXml() );
        HtmlInput usernameInput = routerPage.getHtmlElementById("username");
        HtmlInput passwordInput = routerPage.getHtmlElementById("password");
        usernameInput.setValueAttribute( properties.getProperty( "router.username" ) );
        passwordInput.setValueAttribute( properties.getProperty( "router.password" ) );
        HtmlInput loginBtn = routerPage.getHtmlElementById("pop_login");
        routerPage = loginBtn.click();
        webClient.waitForBackgroundJavaScript( 10000 );
        LOGGER.info( "LOGGED IN: " + routerPage.asXml() );
        HtmlAnchor settingsLink = routerPage.getHtmlElementById( "settings" );
        routerPage = settingsLink.click();
        LOGGER.info( "SETTINGS: " + routerPage.asXml() );
        webClient.waitForBackgroundJavaScript( 10000 );
        HtmlListItem systemMenu = routerPage.getHtmlElementById( "system" );
        routerPage = systemMenu.click();
        webClient.waitForBackgroundJavaScript( 1000 );
        HtmlListItem rebootMenu = routerPage.getHtmlElementById( "reboot" );
//        routerPage = webClient.getPage( "http://192.168.19.1/html/reboot.html" );
        routerPage = rebootMenu.click();
        webClient.waitForBackgroundJavaScript( 1000 );
        HtmlAnchor restartBtn = HtmlAnchor.class.cast( rebootMenu.getFirstElementChild() );
        routerPage = restartBtn.click();
        webClient.waitForBackgroundJavaScript( 1000 );
        LOGGER.info( "REBOOT PAGE: " + routerPage.asXml() );
        HtmlInput applyBtn = routerPage.getHtmlElementById( "reboot_apply_button" );
        routerPage = applyBtn.click();
        webClient.waitForBackgroundJavaScript( 1000 );
        HtmlInput confirmBtn = routerPage.getHtmlElementById( "pop_confirm" );
        routerPage = confirmBtn.click();
        webClient.waitForBackgroundJavaScript( 4000 );
        LOGGER.info( "POST APPLY PAGE: " + routerPage.asXml() );
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

//        DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
//        credentialsProvider.addCredentials( properties.getProperty( "router.username" ), properties.getProperty( "router.password" ) );
        return webClient;
    }
    
}
