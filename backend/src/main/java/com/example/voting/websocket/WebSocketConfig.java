package com.example.voting.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import com.example.voting.websocket.StompAuthenticatorInterceptor;
import com.example.voting.security.JwtService;
import com.example.voting.repository.UserRepository;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new StompAuthenticatorInterceptor(jwtService(), userRepository()));
    }

    // helper bean accessors to avoid full ApplicationContext wiring here
    private JwtService jwtService() {
        return org.springframework.web.context.support.WebApplicationContextUtils
            .getRequiredWebApplicationContext(org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext().getServletContext())
            .getBean(JwtService.class);
    }

    private UserRepository userRepository() {
        return org.springframework.web.context.support.WebApplicationContextUtils
            .getRequiredWebApplicationContext(org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext().getServletContext())
            .getBean(UserRepository.class);
    }
}
