package com.zgd.shop.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zgd.shop.dao.entity.model.User;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzzgd
 * @since 2019-01-16
 */
public interface UserMapper extends BaseMapper<User> {

  User getUserRoleByUserName(String username);

  User lockUserById(Long id);
}
