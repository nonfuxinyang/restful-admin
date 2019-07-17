package com.zgd.shop.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zgd
 * @Date: 2019/1/15 16:28
 * @Description: 不校验权限,不需要登录
 */
@Slf4j
@RestController
@RequestMapping("/ignore")
public class IgnoreController {


  @RequestMapping("/hello")
  public String hello() {
    return "hello";
  }

}
