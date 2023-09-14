package com.example.stay.common.util;

import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatWebCustomConfig implements WebServerFactoryCustomizer <TomcatServletWebServerFactory> {
    /**
     * RFC 3986 avoid
     * 허용 문자 추가
     *
     */
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers((TomcatConnectorCustomizer)
        connector -> connector.setAttribute("relaxedQueryChars", "<>[\\]^`{|}"));
    }
}
