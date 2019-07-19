package com.zgd.shop.web.config.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * UserAuthProperties
 * 登录认证相关配置类
 * @author zgd
 * @date 2019/7/19 14:34
 */
@Configuration
@ConfigurationProperties(
        prefix = "user-auth"
)
@Data
public class UserAuthProperties {

  /**
   * session过期时间，默认1天
   */
  private long sessionExpirationTime = 24 * 60 * 60 * 1000;

  /**
   * token过期时间，默认1小时
   */
  private long jwtExpirationTime = 60 * 60 * 1000;
}
