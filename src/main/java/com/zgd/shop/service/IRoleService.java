package com.zgd.shop.service;

import com.baomidou.mybatisplus.service.IService;
import com.zgd.shop.dao.entity.model.Role;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzzgd
 * @since 2019-01-16
 */
public interface IRoleService extends IService<Role> {


  String mustAdmin();
}
