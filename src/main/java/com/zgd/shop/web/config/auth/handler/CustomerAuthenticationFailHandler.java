package com.zgd.shop.web.config.auth.handler;

import com.zgd.shop.common.util.ResponseUtil;
import com.zgd.shop.core.error.ErrorCodeConstants;
import com.zgd.shop.core.result.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Exrickx
 */
@Slf4j
@Component
public class CustomerAuthenticationFailHandler implements AuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        ResponseUtil.out(401, ResultUtil.failure(ErrorCodeConstants.LOGIN_FAIED));
    }

}
