package com.zgd.shop.web.config.auth.handler;

import com.zgd.shop.common.constants.UserConstants;
import com.zgd.shop.common.util.ResponseUtil;
import com.zgd.shop.common.util.jwt.JwtTokenUtil;
import com.zgd.shop.core.result.ResultUtil;
import com.zgd.shop.web.config.auth.user.CustomerUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录成功处理类,登录成功后会调用里面的方法
 * @author Exrickx
 */
@Slf4j
@Component
public class CustomerAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("登陆成功...");
        CustomerUserDetails principal = (CustomerUserDetails) authentication.getPrincipal();
        //颁发token
        Map<String,Object> emptyMap = new HashMap<>(4);
        emptyMap.put(UserConstants.USER_ID,principal.getId());
        String token = JwtTokenUtil.generateToken(principal.getUsername(), emptyMap);
        ResponseUtil.out(ResultUtil.success(token));
    }
}
