package com.zgd.shop.core.error;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zgd.shop.common.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
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
    errorCodeCache = JSON.parseObject(s, new TypeReference<HashMap<String,ErrorDto>>(){});
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
    return "";
  }


  /**
   * 获取内部错误消息
   * @param code
   * @return
   */
  public static String getInternalMsg(String code){
    ErrorDto errorDto = errorCodeCache.get(code);
    if (Objects.nonNull(errorDto)){
      String innerMsg = errorDto.getInnerMsg();
      return StringUtils.isEmpty(innerMsg) ? errorDto.getMsg() : innerMsg;
    }
    return "";
    }
  }


