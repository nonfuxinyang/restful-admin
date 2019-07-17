package com.zgd.shop.web.config;

import com.zgd.shop.web.config.auth.encoder.MyAesPasswordEncoder;
import com.zgd.shop.web.config.auth.encoder.MyEmptyPasswordEncoder;
import com.zgd.shop.web.config.auth.handler.*;
import com.zgd.shop.web.config.auth.filter.CustomerJwtAuthenticationTokenFilter;
import com.zgd.shop.web.config.auth.user.CustomerUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Author: zgd
 * @Date: 2019/1/15 17:42
 * @Description:
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true)// 控制@Secured权限注解
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  /**
   * 这里需要交给spring注入,而不是直接new
   */
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private CustomerUserDetailService customerUserDetailService;
  @Autowired
  private CustomerAuthenticationFailHandler customerAuthenticationFailHandler;
  @Autowired
  private CustomerAuthenticationSuccessHandler customerAuthenticationSuccessHandler;
  @Autowired
  private CustomerJwtAuthenticationTokenFilter customerJwtAuthenticationTokenFilter;
  @Autowired
  private CustomerRestAccessDeniedHandler customerRestAccessDeniedHandler;
  @Autowired
  private CustomerLogoutSuccessHandler customerLogoutSuccessHandler;




  @Bean
  public PasswordEncoder passwordEncoder() {
//    return new BCryptPasswordEncoder();
    return new MyAesPasswordEncoder();
  }

  public static void main(String[] args) {
    System.out.println(new BCryptPasswordEncoder().encode("zhangguodong"));
    //$2a$10$dWr60zL6zXvgXowvePDFdupVZTKSisR2JNkh/Ak.v0kG.docCdKk.
  }

  /**
   * 该方法定义认证用户信息获取的来源、密码校验的规则
   *
   * @param auth
   * @throws Exception
   */
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    //auth.authenticationProvider(myauthenticationProvider)  自定义密码校验的规则

    //如果需要改变认证的用户信息来源，我们可以实现UserDetailsService
    auth.userDetailsService(customerUserDetailService).passwordEncoder(passwordEncoder);

    //inMemoryAuthentication 从内存中获取
    auth.inMemoryAuthentication()
            //spring security5 以上必须配置加密
            .passwordEncoder(new MyEmptyPasswordEncoder())
            .withUser("zzzgd").password("zhangguodong").roles("EMPLOYEE")
            .and()
            .withUser("admin").password("admin").roles("ADMIN", "EMPLOYEE")
    ;
    //jdbcAuthentication 从数据库中获取，但是默认是以security提供的表结构
    //usersByUsernameQuery 指定查询用户SQL
    //authoritiesByUsernameQuery 指定查询权限SQL
    //auth.jdbcAuthentication().dataSource(dataSource).usersByUsernameQuery(query).authoritiesByUsernameQuery(query);

  }


  @Override
  protected void configure(HttpSecurity http) throws Exception {
    /**
     * antMatchers: ant的通配符规则
     * ?	匹配任何单字符
     * *	匹配0或者任意数量的字符，不包含"/"
     * **	匹配0或者更多的目录，包含"/"
     */
    http
            .headers()
            .frameOptions().disable();

    http
            .exceptionHandling().accessDeniedHandler(customerRestAccessDeniedHandler);

    //使用jwt的Authentication
    http
            .addFilterBefore(customerJwtAuthenticationTokenFilter,UsernamePasswordAuthenticationFilter.class);

    http
            .authorizeRequests()
            //这里表示"/any"和"/ignore"不需要权限校验
            .antMatchers( "/ignore/**", "/login", "/**/register/**").permitAll()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/emp/**").hasAnyRole("ADMIN","EMPLOYEE")
            .anyRequest().authenticated()
            // 这里表示任何请求都需要校验认证(上面配置的放行)


            .and()
            //配置登录,检测到用户未登录时跳转的url地址,登录放行
            .formLogin()
//            .loginPage("/login.json")
            //成功后调用的的url
//            .defaultSuccessUrl("/hello",true)
            //需要跟前端表单的action地址一致
            .loginProcessingUrl("/login")
            .successHandler(customerAuthenticationSuccessHandler)
            .failureHandler(customerAuthenticationFailHandler)
            //失败后重定向的url,默认是[loginPage] + [?error]
//            .failureForwardUrl("/login.json?error=true")
            .failureUrl("/login.json?error=true")
            .permitAll()

            //配置登出,登出放行
            .and()
            .logout()
            .logoutSuccessHandler(customerLogoutSuccessHandler)
            .deleteCookies("JESSIONID")
            .permitAll()

            //开启记住我功能,会给浏览器生成cookie
            .and()
            .rememberMe()
            .rememberMeCookieName("zgd")

            .and()
            .csrf().disable()
    ;
  }


  @Override
  public void configure(WebSecurity web) throws Exception {
    //权限控制需要忽略所有静态资源，不然登录页面未登录状态无法加载css等静态资源
    web.ignoring().mvcMatchers("/static/**", "/img/**");
  }

}
