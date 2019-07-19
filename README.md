# 基于SpringSecurity和Jwt的前后端分离(restful)的后台管理
## 一. SpringBoot的权限控制
### 1.1 SpringSecurity介绍
1. 使用  
    加入依赖:
    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    ```
    如果不加这个依赖,可以正常访问暴露的接口.加入这个依赖后,请求会跳转到spring内置的登录页面

2. 原理   
    2.1 拦截校验  
        写一个类继承`WebSecurityConfigurerAdapter`类,重写`configure`方法,就可以配置springboot security了,我们可以配置拦截请求的规则,
        通过通配符来匹配是否需要进行权限校验,如果发现没有权限会跳转到我们配置的或者默认的登录页面进行登录.  
    2.2 登录  
        我们可以在内存中配置用户和角色,也可以从数据库中获取. 从数据库获取的话,需要实现`UserDetailService`这个类,重写`loadUserByUsername`
        这个方法,在这个方法里面我们根据登录的用户名通过mybatis等拿到数据库中这个用户信息,将用户的角色交个springboot security去校验.
        用户名必须要为username,密码必须是password,SpringBoot Security贴心提供了`User`类,sql语句,以及`JdbcDaoImpl`     
        **这里需要注意的是,角色命名需要用ROLE_开头**.    
    2.3 加密  
        spring boot security5以后,就必须配置加密方式,security提供了默认的加密方式,我们也可以通过实现`PasswordEncoder`来自定义我们自己的加密方式.  
    2.4 关于`ROLE_前缀`     
        数据库的角色,必须要有这个前缀,因为security在判断用户角色权限时,会判断是不是`ROLE_`开头.
    在注解中`@Secured`和`@PreAuthorize("hasAuthority('ROLE_ADMIN')")`的value,也需要`ROLE_`前缀,`@PreAuthorize("hasRole="ADMIN")`不需要前缀, 
    在`WebSecurityConfigurerAdapter`实现类,配置类里用代码配置角色的时候,不用前缀
    ```
    Spring Security有一个基于选民的架构，这意味着一系列的AccessDecisionVoters 做出访问决策。选民根据为安全资源（例如方法调用）指定的“配置属性”进行操作。
    通过这种方法，并非所有属性都可能与所有选民相关，并且选民需要知道何时应忽略属性（弃权）以及何时应根据属性值投票授予或拒绝访问。
    最常见的选民是RoleVoter默认情况下，只要找到具有“ROLE_”前缀的属性，就会进行投票。它将属性（例如“ROLE_USER”）与当前用户已分配的权限的名称进行简单比较。
    如果它找到匹配（它们具有称为“ROLE_USER”的权限），则它投票授予访问权限，否则它投票拒绝访问。
    ```

### 1.2 SpringSecurity使用注解来权限控制
a. 注解细粒度控制方法级别  
1 @Secured    
    需要在配置类上加上`@EnableGlobalMethodSecurity(securedEnabled = true)`注解,表示打开`@Secured`注解,只要在方法上加上`@Secured(value = {"ROLE_ADMIN"})`,
    就表示调用这个方法,需要`ROLE_ADMIN`的角色. 否则就会在拦截器被拦截:
    ```
    2019-01-21 16:15:31.263 DEBUG 23024 --- [nio-8088-exec-1] o.s.web.servlet.DispatcherServlet        : Failed to complete request: org.springframework.security.access.AccessDeniedException: 不允许访问
    ```  

2 @PreAuthorize和@PostAuthorize  
    这两个注解适用于比较复杂的情况,`@PostAuthorize`用的不多,是在请求返回的时候调用.`@PreAuthorize`可以使用spel语言,加上复杂的条件.比如某个方法,需要同时具备
    ADMIN和DBA的权限,那么就不能使用@Secured了,代码如下:  
```
@PreAuthorize("hasRole('ADMIN') AND hasRole('DBA')")
    void deleteUser(int id);

```

### 1.3 实现前后端分离
SpringSecurity默认不是前后端分离的,自带了login页面和其他页面.推荐的是配合
thymeleaf静态模板一起使用.

如果需要实现前后端分离,比如登录失败是返回json,而不是跳转到登录页面,需要做到两大点:
#### a. 实现各种handler
自己实现各种handler,让SpringSecurity在譬如没有权限的时候不是返回页面,而是根据handler处理返回数据

- 实现AuthenticationEntryPoint接口,当匿名请求需要登录的接口时,拦截处理
- 实现AuthenticationSuccessHandler接口,当登录成功后,该处理类的方法被调用
- 实现AuthenticationFailureHandler接口,当登录失败后,该处理类的方法被调用
- 实现AccessDeniedHandler接口,当登录后,访问接口没有权限的时候,该处理类的方法被调用
- 实现LogoutSuccessHandler接口,注销的时候调用

#### b. 将登录处理改为颁发token
1. 颁发token:

    使用Jwt来生成需要的token,在AuthenticationSuccessHandler接口中,登录成功后,颁发token
    
2. 解析token:

    继承OncePerRequestFilter过滤器,在请求过来的时候,解析请求头中的token,
    再解析token得到用户信息,再存到SecurityContextHolder中,后面的权限认证就和普通SpringSecurity一样,交给
    SpringSecurity处理.  
    为了避免每次请求都要读取数据库,可以将解析的用户信息放到Redis.
    

#### c. 将上面的handler和filter配置
其中重要的一点:
`http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)`
将SpringSecurity的session设置为无状态,否则在用户登录后,即使token无效,因为用户信息保存在session中,
还是认为用户已登录.这就不受token管理了.


```java
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
            //登录后,访问没有权限处理类
            .exceptionHandling().accessDeniedHandler(customerRestAccessDeniedHandler)
            //匿名访问,没有权限的处理类
            .authenticationEntryPoint(customerAuthenticationEntryPoint);

    //使用jwt的Authentication
    http
            .addFilterBefore(customerJwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

    http
            .authorizeRequests()
            //这里表示"/any"和"/ignore"不需要权限校验
            .antMatchers("/ignore/**", "/login", "/**/register/**").permitAll()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/emp/**").hasAnyRole("ADMIN", "EMPLOYEE")
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
//            .failureUrl("/login.json?error=true")
            .permitAll()
//配置取消session管理,又Jwt来获取用户状态,否则即使token无效,也会有session信息,依旧判断用户为登录状态
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            
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

```

#### d. token颁发和过期
1. 在登录成功后的`CustomerAuthenticationSuccessHandler`，创建并颁发token，状态码是200，在请求头中。
并储存session信息在缓存，redis或本地cache。
2. 在每次请求过来的时候，过滤器解析请求头，带有token标识则解析token，解析出username，查询缓存，如果有放入到
SpringSecurity的上下文中。如果token过期，通过username查询缓存，如果能查询到（session没过期），颁发新的token，
同样放到请求头，状态码为201。如果session中也没有，则放行。如果没有请求头没有token标识，直接放行
3. SpringSecurity根据上下文自己去判断权限，直接放行的没有查到用户信息，对于需要权限的资源，返回AccessDeny的异常




## 二. mybatis-plus
这个模块中,集成了mybatis-plus来操作数据,并在`com.zgd.springboot.demo.security.web.config.MysqlGenerator`提供了逆向工程的工具类,可以生成
我们需要的接口,mapper等文件








