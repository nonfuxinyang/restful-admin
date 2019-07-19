package com.zgd.shop.web.auth.user;

/**
 * UserSessionService
 * 用来管理用户会话信息，登录后储存，注销则清空
 * @author zgd
 * @date 2019/7/19 14:24
 */
public interface UserSessionService {

  void saveSession(CustomerUserDetails userDetails) ;

  CustomerUserDetails getSessionByUsername(String username);

  void destroySession(String username);
}
