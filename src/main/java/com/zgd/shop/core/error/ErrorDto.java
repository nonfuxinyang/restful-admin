package com.zgd.shop.core.error;

import lombok.Data;

/**
 * ErrorDto
 *
 * @author zgd
 * @date 2019/7/17 17:56
 */
@Data
public class ErrorDto {

  private String code;

  private String msg;
  
  private String innerMsg;
}
