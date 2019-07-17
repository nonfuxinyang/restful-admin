package com.zgd.shop.core.error;

import com.alibaba.fastjson.JSON;
import com.zgd.shop.common.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Objects;

/**
 * ErrorCache
 *
 * @author zgd
 * @date 2019/7/17 17:55
 */
@Slf4j
public class ErrorCache {

  private static final String ERROR_JSON_PATH = "json/errorCode.json";

  private static HashMap<String,ErrorDto> errorCodeCache;

  static {
    String s = FileUtil.readResourceFile(ERROR_JSON_PATH);
    HashMap hashMap = JSON.parseObject(s, HashMap.class);
    errorCodeCache = hashMap;
  }


  /**
   * 获取错误消息
   * @param code
   * @return
   */
  public static String getMsg(String code){
    ErrorDto errorDto = errorCodeCache.get(code);
    if (Objects.nonNull(errorDto)){
      return errorDto.getMsg();
    }
    return null;
  }

  public static void main(String[] args){
    System.out.println("s");
  }

}
