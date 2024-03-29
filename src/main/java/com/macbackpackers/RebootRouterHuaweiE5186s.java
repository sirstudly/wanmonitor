
package com.macbackpackers;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RebootRouterHuaweiE5186s implements RebootRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger( RebootRouterHuaweiE5186s.class );

    @Autowired
    private ApplicationContext context;

    @Override
    public void reboot( RouterConfig config ) throws IOException {
        String routerUrl = config.getUrl();
        LOGGER.info( "Rebooting " + routerUrl );
        try ( WebClient webClient = context.getBean( WebClient.class ) ) {
            HtmlPage routerPage = webClient.getPage( routerUrl );
            routerPage = routerPage.getElementById( "logout_span" ).click(); // actually is Log in
            HtmlInput usernameInput = routerPage.getHtmlElementById( "username" );
            HtmlInput passwordInput = routerPage.getHtmlElementById( "password" );
            usernameInput.setValueAttribute( config.getUsername() );
            passwordInput.setValueAttribute( config.getPassword() );
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
            rebootMenu.click();
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
}
