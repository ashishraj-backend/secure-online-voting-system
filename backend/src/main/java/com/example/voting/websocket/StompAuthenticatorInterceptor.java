package com.example.voting.websocket;

import com.example.voting.repository.UserRepository;
import com.example.voting.security.JwtService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.security.Principal;

public class StompAuthenticatorInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public StompAuthenticatorInterceptor(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String auth = accessor.getFirstNativeHeader("Authorization");
            if (auth == null) auth = accessor.getFirstNativeHeader("authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                try {
                    String subject = jwtService.parseSubject(token);
                    java.util.UUID userId = java.util.UUID.fromString(subject);
                    var user = userRepository.findById(userId);
                    if (user.isPresent()) {
                        Principal p = () -> userId.toString();
                        accessor.setUser(p);
                    }
                } catch (Exception ignored) {}
            }
        }
        return message;
    }
}
