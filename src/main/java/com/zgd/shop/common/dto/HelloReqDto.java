package com.zgd.shop.common.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * HelloReqDto
 *
 * @author zgd
 * @date 2019/7/19 10:11
 */
@Data
public class HelloReqDto {

  @NotEmpty(message = "name不能为空")
  public String name;

  @NotNull(message = "id不能为null")
  public Integer id;

  @Range(min = 18,max = 80,message = "age不符合范围")
  public Integer age;
}
