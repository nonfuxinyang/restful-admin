package com.zgd.shop.web.auth.user;

import com.google.common.collect.Maps;
import com.zgd.shop.common.constants.SecurityConstants;
import com.zgd.shop.common.constants.UserConstants;
import com.zgd.shop.common.util.ResponseUtil;
import com.zgd.shop.common.util.jwt.JwtTokenUtil;
import com.zgd.shop.core.result.ResultUtil;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * UserTokenManager
 * token管理
 *
 * @author zgd
 * @date 2019/7/19 15:25
 */
public class UserTokenManager {


  /**
   * 颁发token
   * @param principal
   * @author zgd
   * @date 2019/7/19 15:34
   * @return void
   */
  public static void awadAccessToken(CustomerUserDetails principal) {
    //颁发token
    Map<String,Object> emptyMap = new HashMap<>(4);
    emptyMap.put(UserConstants.USER_ID,principal.getId());
    String token = JwtTokenUtil.generateToken(principal.getUsername(), emptyMap);
    HashMap<String, String> map = Maps.newHashMapWithExpectedSize(1);
    map.put(SecurityConstants.HEADER,token);
    ResponseUtil.outWithHeader(ResultUtil.success(),map);
  }
}
