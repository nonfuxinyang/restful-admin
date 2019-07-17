package com.zgd.shop.common.util;

import com.zgd.shop.core.error.ErrorCache;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * FileUtil
 *
 * @author zgd
 * @date 2019/7/17 18:11
 */
@Slf4j
public class FileUtil {
  public static String readResourceFile(String path) {
    StringBuilder sb = new StringBuilder();
    try {
      InputStream is = ErrorCache.class.getClassLoader().getResourceAsStream(path);
      BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
    } catch (Exception e) {
      log.error("[读取文件失败] ", e);
    }
    return sb.toString();
  }
}
