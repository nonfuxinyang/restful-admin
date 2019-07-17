package com.zgd.shop.service;

import com.baomidou.mybatisplus.service.IService;
import com.zgd.shop.dao.entity.model.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzzgd
 * @since 2019-01-16
 */
public interface IUserService extends IService<User> {

  /**
   * 获取用户信息
   * @param username
   */
  User getUserRoleByUserName(String username);

  /**
   * 注册
   * @param user
   * @param role
   * @return
   */
  String register(User user, String role);
}
