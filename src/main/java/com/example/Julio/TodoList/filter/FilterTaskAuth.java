package com.example.Julio.TodoList.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.Julio.TodoList.user.InterfaceUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter{

    @Autowired
    InterfaceUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException 
    {
        var servletPath = request.getServletPath();

        if(servletPath.contains("/tasks/")) {

            // Get authentication (user and password)
            var authorization = request.getHeader("Authorization");

            var authEncoded = authorization.substring("Basic".length()).trim();

            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);

            var authString = new String(authDecoded);

            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];

            // User validation
            var user = this.userRepository.findByUsername(username);
            
            if(user == null) {
                response.sendError(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            // Password validation
            var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

            if(passwordVerify.verified) {
                request.setAttribute("userId", user.getId());

                filterChain.doFilter(request, response);
                return;
            }

            response.sendError(HttpStatus.UNAUTHORIZED.value());
        } else {
            filterChain.doFilter(request, response);
        }
    }
}