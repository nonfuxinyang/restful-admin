package com.zgd.shop.web.config.auth.filter;

import com.zgd.shop.common.constants.SecurityConstants;
import com.zgd.shop.common.util.jwt.JwtTokenUtil;
import com.zgd.shop.web.config.auth.user.CustomerUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zzzgd
 */
@Component
@Slf4j
public class CustomerJwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    CustomerUserDetailService customerUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        
    	//请求头为 accessToken
    	//请求体为 Bearer token

    	String authHeader = request.getHeader(SecurityConstants.HEADER);

        if (authHeader != null && authHeader.startsWith(SecurityConstants.TOKEN_SPLIT)) {

            final String authToken = authHeader.substring(SecurityConstants.TOKEN_SPLIT.length());
            String username = JwtTokenUtil.parseTokenGetUsername(authToken);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customerUserDetailService.loadUserByUsername(username);
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
}