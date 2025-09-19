package com.example.SmartFinanceManager.Security;

import com.example.SmartFinanceManager.Utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            jwt = authHeader.substring(7);
            try{
                if(jwtUtils.validateToken(jwt)){
                    email = jwtUtils.extractUsername(jwt);
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            var userDetails = userDetailsService.loadUserByUsername(email);

            var authtoken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authtoken);
        }
        filterChain.doFilter(request, response);
    }
}
