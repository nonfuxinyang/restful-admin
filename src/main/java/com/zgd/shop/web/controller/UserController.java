package com.zgd.shop.web.controller;

import com.zgd.shop.core.result.Result;
import com.zgd.shop.core.result.ResultUtil;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController
 *
 * @author zgd
 * @date 2019/7/17 19:34
 */
@RestController
public class UserController {

  @GetMapping("/hello")
  public Result<String> hello(){
    return ResultUtil.success("hello world");
  }


  @Secured("ROLE_SUPER")
  @GetMapping("/super")
  public Result<String> superr(){
    return ResultUtil.success("hello world,super可以访问");
  }


  @Secured("ROLE_ADMIN")
  @GetMapping("/admin")
  public Result<String> admin(){
    return ResultUtil.success("hello world,admin可以访问");
  }

  @PreAuthorize("hasRole('ADMIN') AND hasRole('EMPLOYEE')")
  @GetMapping("/employee")
  public Result<String> employee(){
    return ResultUtil.success("hello world,employee可以访问");
  }
}
