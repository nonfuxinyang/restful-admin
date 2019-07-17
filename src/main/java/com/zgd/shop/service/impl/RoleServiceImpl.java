package com.zgd.shop.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zgd.shop.dao.entity.model.Role;
import com.zgd.shop.dao.mapper.RoleMapper;
import com.zgd.shop.service.IRoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zzzgd
 * @since 2019-01-16
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {


  /**
   * 配置调用该业务层方法需要的权限为：ROLE_ADMIN
   * @return
   */
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @Override
  public String mustAdmin() {
    return "业务层,只有ROLE_ADMIN权限可见.";
  }
}
