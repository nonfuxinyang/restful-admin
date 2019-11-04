package com.zgd.shop.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zgd.base.util.encryp.AESUtil;
import com.zgd.shop.dao.entity.model.Role;
import com.zgd.shop.dao.entity.model.User;
import com.zgd.shop.dao.entity.model.UserRole;
import com.zgd.shop.dao.mapper.UserMapper;
import com.zgd.shop.service.IRoleService;
import com.zgd.shop.service.IUserRoleService;
import com.zgd.shop.service.IUserService;
import com.zgd.shop.web.auth.encoder.MyAesPasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

  @Autowired
  private IUserRoleService userRoleService;

  @Autowired
  private IRoleService roleService;

  /**
   * 获取用户信息
   * @param username
   * @author zgd
   * @date 2019/1/16 16:30
   * @return void
   */
  @Override
  public User getUserRoleByUserName(String username) {
    User user = baseMapper.getUserRoleByUserName(username);
    log.info("[getUserRoleByUserName] 获取到user:{}",JSON.toJSONString(user));
    return user;
  }

  /**
   * 注册用户
   * @param user
   * @param roleName
   * @return
   */
  @Override
  public String register(User user, String roleName) {
    log.info("[register] 注册用户: user:{}\trole:{}", JSON.toJSONString(user),roleName);
    int i = super.selectCount(new EntityWrapper<User>().eq("username", user.getUsername()));
    if (i == 0){
      Role role = roleService.selectOne(new EntityWrapper<Role>().eq("role_name", roleName));
      if (role == null){
        log.warn("[register] 注册用户 找不到该角色:{}",roleName);
        return "找不到该角色";
      }

      user.setStatus(1);
      String password = user.getPassword();
      //aes加密
      String encrypt = AESUtil.encrypt2Base64ByRandom(password, MyAesPasswordEncoder.SECRET_SEED);
      user.setPassword(encrypt);
      super.insert(user);

      UserRole userRole = new UserRole();
      userRole.setUserId(user.getId());
      userRole.setRoleId(role.getId());
      userRoleService.insert(userRole);
      return "ok";
    }
    return "用户名已注册!请更换用户名";
  }
}
