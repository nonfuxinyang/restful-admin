package com.zgd.shop.web.auth.user;

import com.google.common.collect.Maps;
import com.zgd.shop.common.constants.SecurityConstants;
import com.zgd.shop.common.constants.UserConstants;
import com.zgd.shop.common.util.ResponseUtil;
import com.zgd.shop.common.util.jwt.JwtTokenUtil;
import com.zgd.shop.core.result.ResultUtil;
import com.zgd.shop.web.config.auth.UserAuthProperties;
import org.apache.commons.collections.MapUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * UserTokenManager
 * token管理
 *
 * @author zgd
 * @date 2019/7/19 15:25
 */
@Component
public class UserTokenManager {

  @Autowired
  private UserAuthProperties userAuthProperties;
  @Autowired
  private UserSessionService userSessionService;

  /**
   * 颁发token
   * @param principal
   * @author zgd
   * @date 2019/7/19 15:34
   * @return void
   */
  public void awardAccessToken(CustomerUserDetails principal,boolean isRefresh) {
    //颁发token 确定时间戳，保存在session中和token中
    long mill = System.currentTimeMillis();
    userSessionService.saveSession(principal);
    userSessionService.saveTokenTimestamp(principal.getUsername(),mill);

    Map<String,Object> param = new HashMap<>(4);
    param.put(UserConstants.USER_ID,principal.getId());
    param.put(SecurityConstants.TIME_STAMP,mill);

    String token = JwtTokenUtil.generateToken(principal.getUsername(), param,userAuthProperties.getJwtExpirationTime());
    HashMap<String, String> map = Maps.newHashMapWithExpectedSize(1);
    map.put(SecurityConstants.HEADER,token);
    int code = isRefresh ? 201 : 200;
    ResponseUtil.outWithHeader(code,ResultUtil.success(),map);
  }
}
