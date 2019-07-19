package com.zgd.shop.common.dto;

import lombok.Data;

/**
 * @Author: zgd
 * @Date: 2019/1/16 11:30
 * @Description:
 */
@Data
public class UserRoleDto {

  private Long id;
  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;

  private String roleName;
}
