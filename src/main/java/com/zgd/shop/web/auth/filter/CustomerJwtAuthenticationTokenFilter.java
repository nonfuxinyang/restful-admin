package com.zgd.shop.web.auth.filter;

import com.zgd.shop.common.constants.SecurityConstants;
import com.zgd.shop.common.util.ResponseUtil;
import com.zgd.shop.common.util.jwt.JwtTokenUtil;
import com.zgd.shop.core.error.ErrorCodeConstants;
import com.zgd.shop.core.result.ResultUtil;
import com.zgd.shop.web.auth.user.CustomerUserDetailService;
import com.zgd.shop.web.auth.user.CustomerUserDetails;
import com.zgd.shop.web.auth.user.UserSessionService;
import com.zgd.shop.web.auth.user.UserTokenManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @Autowired
    UserTokenManager userTokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        
    	//请求头为 accessToken
    	//请求体为 Bearer token

    	String authHeader = request.getHeader(SecurityConstants.HEADER);

        if (authHeader != null && authHeader.startsWith(SecurityConstants.TOKEN_SPLIT)) {
            //请求头有token
            final String authToken = authHeader.substring(SecurityConstants.TOKEN_SPLIT.length());

            String username;
            Claims claims;
            try {
                claims = JwtTokenUtil.parseToken(authToken);
                username = claims.getSubject();
            } catch (ExpiredJwtException e) {
                //token过期
                claims = e.getClaims();
                username = claims.getSubject();
                CustomerUserDetails userDetails = userSessionService.getSessionByUsername(username);
                if (userDetails != null){
                    //session未过期，比对时间戳是否一致，是则重新颁发token
                    if (isSameTimestampToken(username,e.getClaims())){
                        userTokenManager.awardAccessToken(userDetails,true);
                        //直接设置响应码为201,直接返回
                        return;
                    }else{
                        //时间戳不一致.无效token,无法刷新token,响应码401,前端跳转登录页
                        ResponseUtil.out(HttpStatus.UNAUTHORIZED.value(),ResultUtil.failure(ErrorCodeConstants.REQUIRED_LOGIN_ERROR));
                        return;
                    }
                }else{
                    //直接放行,交给后面的handler处理,如果当前请求是需要访问权限,则会由CustomerRestAccessDeniedHandler处理
                    chain.doFilter(request, response);
                    return;
                }
            }

            //避免每次请求都请求数据库查询用户信息，从缓存中查询
            CustomerUserDetails userDetails = userSessionService.getSessionByUsername(username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = customerUserDetailService.loadUserByUsername(username);
                if (userDetails != null) {
                    if(isSameTimestampToken(username,claims)){
                        //必须token解析的时间戳和session保存的一致
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * 判断是否同一个时间戳
     * @param username
     * @param claims
     * @return
     */
    private boolean isSameTimestampToken(String username, Claims claims){
        Long timestamp = userSessionService.getTokenTimestamp(username);
        Long jwtTimestamp = (Long) claims.get(SecurityConstants.TIME_STAMP);
        return timestamp.equals(jwtTimestamp);
    }
}