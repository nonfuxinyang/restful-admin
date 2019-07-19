package com.zgd.shop.web.auth.handler;

import com.zgd.shop.common.util.ResponseUtil;
import com.zgd.shop.core.error.ErrorCodeConstants;
import com.zgd.shop.core.result.ResultUtil;
import com.zgd.shop.web.auth.user.CustomerUserDetails;
import com.zgd.shop.web.auth.user.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登出成功的调用类
 *
 * @author zzzgd
 */
@Component
public class CustomerLogoutSuccessHandler implements LogoutSuccessHandler {

  @Autowired
  private UserSessionService userSessionService;

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    if (authentication != null) {
      Object principal1 = authentication.getPrincipal();
      if (principal1 instanceof CustomerUserDetails) {
        CustomerUserDetails principal = (CustomerUserDetails) principal1;
        //清除session会话信息
        userSessionService.destroySession(principal.getUsername());
        ResponseUtil.out(ResultUtil.success("Logout Success!"));
      }
    }
    ResponseUtil.out(ResultUtil.failure(ErrorCodeConstants.BAD_REQUEST_ERROR));
  }
}