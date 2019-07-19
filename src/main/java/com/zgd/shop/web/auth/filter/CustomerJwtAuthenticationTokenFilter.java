package com.zgd.shop.web.auth.filter;

import com.zgd.shop.common.constants.SecurityConstants;
import com.zgd.shop.common.util.jwt.JwtTokenUtil;
import com.zgd.shop.web.auth.user.CustomerUserDetailService;
import com.zgd.shop.web.auth.user.CustomerUserDetails;
import com.zgd.shop.web.auth.user.UserSessionService;
import com.zgd.shop.web.auth.user.UserTokenManager;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤器,在请求过来的时候,解析请求头中的token,再解析token得到用户信息,再存到SecurityContextHolder中
 * @author zzzgd
 */
@Component
@Slf4j
public class CustomerJwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    CustomerUserDetailService customerUserDetailService;
    @Autowired
    UserSessionService userSessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        
    	//请求头为 accessToken
    	//请求体为 Bearer token

    	String authHeader = request.getHeader(SecurityConstants.HEADER);

        if (authHeader != null && authHeader.startsWith(SecurityConstants.TOKEN_SPLIT)) {

            final String authToken = authHeader.substring(SecurityConstants.TOKEN_SPLIT.length());

            String username;
            try {
                username = JwtTokenUtil.parseTokenGetUsername(authToken);
            } catch (ExpiredJwtException e) {
                //token过期
                username = e.getClaims().getSubject();
                CustomerUserDetails userDetails = userSessionService.getSessionByUsername(username);
                if (userDetails != null){
                    //session未过期，重新颁发token
                    UserTokenManager.awadAccessToken(userDetails);
                }
            }
            CustomerUserDetails userDetails = userSessionService.getSessionByUsername(username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = customerUserDetailService.loadUserByUsername(username);
                //避免每次请求都请求数据库查询用户信息，从缓存中查询

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