package com.zgd.shop.web.config.auth.encoder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author: zgd
 * @Date: 2019/1/21 16:08
 * @Description: 简单的加密器,其实简单起见并未加密
 */
@Slf4j
public class MyEmptyPasswordEncoder implements PasswordEncoder {

  @Override
  public String encode(CharSequence charSequence) {
    return String.valueOf(charSequence);
  }

  @Override
  public boolean matches(CharSequence charSequence, String s) {
    log.info("[MyEmptyPasswordEncoder] [matches] 密码:{}\t用户密码:{}",charSequence,s);
    return s.equals(charSequence.toString());
  }

}
