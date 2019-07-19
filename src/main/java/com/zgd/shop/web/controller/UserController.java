package com.zgd.shop.web.controller;

import com.alibaba.fastjson.JSON;
import com.zgd.shop.common.dto.HelloReqDto;
import com.zgd.shop.core.error.ErrorCodeConstants;
import com.zgd.shop.core.exception.BizServiceException;
import com.zgd.shop.core.result.Result;
import com.zgd.shop.core.result.ResultUtil;
import org.hibernate.validator.HibernateValidator;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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



  @PostMapping("/valid")
  public Result<String> valid(@Valid @RequestBody HelloReqDto reqDto){
    if (reqDto.getAge() == 20){
      throw new BizServiceException(ErrorCodeConstants.NOT_FOUND_USER_ERROR);
    }else if(reqDto.getAge() == 21){
      throw new BizServiceException("ABC-ERROR","自定义异常");
    }else if(reqDto.getAge() == 22){
      throw new BizServiceException(ErrorCodeConstants.NOT_FOUND_USER_ERROR,"自定义异常");
    }
    return ResultUtil.success(JSON.toJSONString(reqDto));
  }
}
