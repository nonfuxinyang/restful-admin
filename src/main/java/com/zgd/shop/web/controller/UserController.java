package com.zgd.shop.web.controller;

import com.zgd.shop.core.result.Result;
import com.zgd.shop.core.result.ResultUtil;
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

}
