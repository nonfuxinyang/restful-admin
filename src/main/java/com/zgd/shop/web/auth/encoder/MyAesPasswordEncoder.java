package com.zgd.shop.web.auth.encoder;

import com.zgd.shop.common.util.encryp.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author: zgd
 * @Date: 2019/1/16 17:32
 * @Description: 自己定义一个加密器
 */
@Slf4j
public class MyAesPasswordEncoder implements PasswordEncoder {

  /**
   * 密钥
   */
  public static final String SECRET_KEY = "zgd123";

  @Override
  public String encode(CharSequence pwd) {
    String end = AESUtil.encrypt(String.valueOf(pwd), SECRET_KEY);
    log.info("[MyAesPasswordEncoder] [encode] 加密的密码明文:{}\t加密后:{}",pwd,end);
    return end;
  }

  @Override
  public boolean matches(CharSequence charSequence, String encodedPassword) {
    if (StringUtils.isNotBlank(charSequence)){
      String end = AESUtil.encrypt(String.valueOf(charSequence), SECRET_KEY);
      log.info("[MyAesPasswordEncoder] [matches] 密码:{}\t加密后:{}\t用户密码:{}",charSequence,end,encodedPassword);
      return end.equals(encodedPassword);
    }
    return false;
  }
}
