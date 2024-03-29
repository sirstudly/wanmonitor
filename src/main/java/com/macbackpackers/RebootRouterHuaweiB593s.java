
package com.macbackpackers;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RebootRouterHuaweiB593s implements RebootRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger( RebootRouterHuaweiB593s.class );

    @Autowired
    private ApplicationContext context;

    @Override
    public void reboot( RouterConfig config ) throws IOException {
        String routerUrl = config.getUrl();
        LOGGER.info( "Rebooting " + routerUrl );
        try ( WebClient webClient = context.getBean( WebClient.class ) ) {
            HtmlPage routerPage = webClient.getPage( routerUrl );
            webClient.waitForBackgroundJavaScript( 2000 );

            LOGGER.info( "Logging in..." );
            HtmlInput usernameInput = routerPage.getHtmlElementById( "txt_Username" );
            HtmlInput passwordInput = routerPage.getHtmlElementById( "txt_Password" );
            usernameInput.setValueAttribute( config.getUsername() );
            passwordInput.setValueAttribute( config.getPassword() );
            routerPage.getHtmlElementById( "login_button" ).click();
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
}
