package com.zgd.shop.web.auth.handler;

import com.zgd.shop.web.auth.user.CustomerUserDetails;
import com.zgd.shop.web.auth.user.UserSessionService;
import com.zgd.shop.web.auth.user.UserTokenManager;
import com.zgd.shop.web.config.auth.UserAuthProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录成功处理类,登录成功后会调用里面的方法
 * @author Exrickx
 */
@Slf4j
@Component
public class CustomerAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private UserSessionService userSessionService;
    @Autowired
    private UserTokenManager userTokenManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("登陆成功...");
        CustomerUserDetails principal = (CustomerUserDetails) authentication.getPrincipal();
        //保存用户信息到会话
        userSessionService.saveSession(principal);
        userTokenManager.awadAccessToken(principal);
    }

}
