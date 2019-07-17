package com.zgd.shop.core.error;

/**
 * ErrorCodeConstants
 *
 * @author zgd
 * @date 2019/7/17 17:38
 */
public interface ErrorCodeConstants {

  /**
   * 没有权限
   */
  String PERMISSION_DENY = "AUTH-PER-01";
  /**
   * 没有权限
   */
  String PERMISSION_NOT_FOUND = "AUTH-PER-02";
  /**
   * 账号或密码不正确
   */
  String LOGIN_FAIED = "AUTH-LOGIN-01";

}
