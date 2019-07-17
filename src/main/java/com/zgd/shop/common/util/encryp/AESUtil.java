package com.zgd.shop.common.util.encryp;

/**
 * @Author: zgd
 * @Date: 2019/1/16 17:22
 * @Description:
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @version V1.0
 */
@Slf4j
public class AESUtil {

  private static final String KEY_ALGORITHM = "AES";
  /**
   * 默认的加密算法
   */
  private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
  /**
   * 编码格式
   */
  private static final String ENCODING = "UTF-8";
  /**
   * 签名算法
   */
  private static final String SIGN_ALGORITHMS = "SHA1PRNG";

  /**
   * AES的key的位数只能是128, 192 or 256
   */
  private static final int KEY_SIZE_128 = 128;
  private static final int KEY_SIZE_192 = 192;
  private static final int KEY_SIZE_256 = 256;

  /**
   * AES 加密操作
   *
   * @param content 待加密内容
   * @param keyWord 生成密钥的关键词
   * @return 返回Base64转码后的加密数据
   */
  public static String encrypt(String content, String keyWord) {
    try {
      // 创建密码器
      Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

      byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
      // 初始化为加密模式的密码器
      cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(keyWord));
      // 加密
      byte[] result = cipher.doFinal(byteContent);
      //通过Base64转码返回
      return Base64Utils.encodeToString(result);
    } catch (Exception ex) {
      log.error("[AESUtil] [encrypt] 异常:", ex);
    }
    return null;
  }

  /**
   * AES 解密操作
   *
   * @param content
   * @param keyWord
   * @return
   */
  public static String decrypt(String content, String keyWord) {

    try {
      //实例化
      Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
      //使用密钥初始化，设置为解密模式
      cipher.init(Cipher.DECRYPT_MODE, getSecretKey(keyWord));
      //执行操作
      byte[] result = cipher.doFinal(Base64Utils.decodeFromString(content));

      return new String(result, StandardCharsets.UTF_8);
    } catch (Exception ex) {
      log.error("[AESUtil] [decrypt] 异常:", ex);
    }

    return null;
  }


  /**
   * 根据加密关键词生成加密秘钥
   *
   * @return
   */
  private static SecretKeySpec getSecretKey(final String keyWord) throws UnsupportedEncodingException {
    //返回生成指定算法密钥生成器的 KeyGenerator 对象
    KeyGenerator kg;
    try {
      kg = KeyGenerator.getInstance(KEY_ALGORITHM);

      //指定签名算法
      SecureRandom random = SecureRandom.getInstance(SIGN_ALGORITHMS);
      random.setSeed(keyWord.getBytes(ENCODING));
      //AES 要求密钥长度为 128
      kg.init(KEY_SIZE_128, random);
//      kg.init(128, new SecureRandom(keyWord.getBytes(ENCODING)));

      //生成一个密钥
      SecretKey secretKey = kg.generateKey();
      log.debug("[AESUtil] [getSecretKey] 根据关键词[{}]生成一个密钥:{}", keyWord, secretKey.getEncoded());
      // 转换为AES专用密钥
      return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    } catch (NoSuchAlgorithmException ex) {
      log.error("[AESUtil] [getSecretKey] 异常:", ex);
    }

    return null;
  }


  /**
   * 由字符串生成加密key
   *
   * @return
   */
  public static SecretKey generalKey(String selectKey) {
    // 本地的密码解码
    byte[] encodedKey = Base64.decodeBase64(selectKey);
    // 根据给定的字节数组使用AES加密算法构造一个密钥
    SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    return key;
  }



  public static void main(String[] args) {

    String s1 = AESUtil.encrypt("你好啊", "123");
    System.out.println("s1:" + s1);

    System.out.println("s2:" + AESUtil.decrypt("LoHQNKeDdaGyH3Nbsq+jHA==", "zgd123"));
  }
}