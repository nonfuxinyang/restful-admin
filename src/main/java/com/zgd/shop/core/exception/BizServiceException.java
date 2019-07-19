package com.zgd.shop.core.exception;

import lombok.Data;

/**
 * BizServiceException
 * 业务抛出的异常
 * @author zgd
 * @date 2019/7/19 11:04
 */
@Data
public class BizServiceException extends RuntimeException{

  private String errCode;

  private String errMsg;

  private boolean isInnerError;

  public BizServiceException(){
    this.isInnerError=false;
  }

  public BizServiceException(String errCode){
    this.errCode =errCode;
    this.isInnerError = false;
  }

  public BizServiceException(String errCode,boolean isInnerError){
    this.errCode =errCode;
    this.isInnerError = isInnerError;
  }

  public BizServiceException(String errCode,String errMsg){
    this.errCode =errCode;
    this.errMsg = errMsg;
    this.isInnerError = false;
  }

  public BizServiceException(String errCode,String errMsg,boolean isInnerError){
    this.errCode =errCode;
    this.errMsg = errMsg;
    this.isInnerError = isInnerError;
  }
}
