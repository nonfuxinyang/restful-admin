package com.zgd.shop.common.util.jwt;

import com.alibaba.fastjson.JSON;
import com.zgd.shop.common.util.encryp.AESUtil;
import com.zgd.shop.common.util.encryp.RSAUtil;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: zgd
 * @Date: 18/09/17 20:44
 * @Description: jwt来生成token和解析token
 */
@Slf4j
public class JwtTokenUtil {


  /**
   * 默认token过期时间1小时 单位 毫秒
   */
  private static long KEEP_ALIVE_TIME = 60 * 60 * 1000L;

  /**
   * 密钥key
   */
  private static String SECRET_KEY = "zgd";


  /**
   * 生成token，过期时间为默认值
   * @param username
   * @param map
   */
  public static String generateToken(String username, Map<String, Object> map){
    return generateToken(username, map, KEEP_ALIVE_TIME);
  }

  /**
   * 生成token 参数可以是任何业务需求中需要用到的值
   *
   * @param map 业务需要携带又不敏感的一些信息
   * @return
   */
  public static String generateToken(String username, Map<String, Object> map, Long expireTime) {
    return generateToken(username, map, expireTime, null, null);
  }


  /**
   * 生成token 参数可以是任何业务需求中需要用到的值
   *
   * @param map        业务需要携带又不敏感的一些信息
   * @param expireMill 过期时间,毫秒
   * @param publicKey  公钥
   * @param privateKey 私钥
   * @return token
   */
  public static String generateToken(String username, Map<String, Object> map, Long expireMill, RSAPublicKey publicKey, RSAPrivateKey privateKey) {
    if (expireMill == null) {
      expireMill = KEEP_ALIVE_TIME;
    }
    SignatureAlgorithm algorithm;
    Key key;
    //密钥及加密算法 普通签名算法
    if (Objects.nonNull(publicKey) && Objects.nonNull(privateKey)) {
      //RSA
      algorithm = SignatureAlgorithm.RS256;
      key = privateKey;
    } else {
      algorithm = SignatureAlgorithm.HS256;
      key = AESUtil.generalKey(SECRET_KEY);
    }

    Date nowDate = new Date();
    //过期时间
    Date expireDate = new Date(nowDate.getTime() + expireMill);
    /*
     * iss(Issuser)：代表这个JWT的签发主体；
     * sub(Subject)：代表这个JWT的主体，即它的所有人；
     * aud(Audience)：代表这个JWT的接收对象；
     * exp(Expiration time)：是一个时间戳，代表这个JWT的过期时间；
     * nbf(Not Before)：是一个时间戳，代表这个JWT生效的开始时间，意味着在这个时间之前验证JWT是会失败的；
     * iat(Issued at)：是一个时间戳，代表这个JWT的签发时间；
     * jti(JWT ID)：是JWT的唯一标识。
     */
    //添加附加信息
    return Jwts.builder()
            .setClaims(map)
            .setIssuer("zgd")
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(expireDate)
            .signWith(algorithm, key).compact();
  }

  /**
   * 解析token
   *
   * @param token
   * @return
   */
  public static boolean validateToken(String token) {
    return validateToken(token, null, null);
  }


  /**
   * 解析token
   *
   * @param token
   * @return
   */
  public static boolean validateToken(String token, RSAPublicKey publicKey, RSAPrivateKey privateKey) {
    try {
      parseToken(token, publicKey, privateKey);
    } catch (Exception e) {
      log.error("[token验证失败]", e);
      return false;
    }
    return true;
  }


  /**
   * 校验token
   *
   * @param token
   * @return
   */
  public static Claims parseToken(String token) {
    return parseToken(token, null, null);
  }


  /**
   * 校验token
   *
   * @param token
   * @param publicKey
   * @param privateKey
   * @return
   */
  public static Claims parseToken(String token, RSAPublicKey publicKey, RSAPrivateKey privateKey)throws ExpiredJwtException {
    //密钥及加密算法 普通签名算法
    Key key;
    if (Objects.nonNull(publicKey) && Objects.nonNull(privateKey)) {
      //RSA
      key = publicKey;
    } else {
      key = AESUtil.generalKey(SECRET_KEY);
    }
    try {
      return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      log.error("[token解析失败] token过期");
      throw e;
    } catch (Exception e) {
      log.error("[token解析失败] ",e);
      return null;
    }
  }


  /**
   * 解析获取用户主体信息
   *
   * @return
   */
  public static String parseTokenGetUsername(String token){
    Claims claims = parseToken(token);
    if (claims != null){
      return claims.getSubject();
    }
    return null;
  }


  //--------------------------------------
  public static void main(String[] args) throws Exception {
    //解密
    hmacDemo();
  }


  public static void rsaDemo() {
    //生成rsa的key
//    RSAUtil.generateKeysToFile("E:/");

    String publicKey = RSAUtil.readKeyFromFile("E:/publicKey.keystore");
    String privateKey = RSAUtil.readKeyFromFile("E:/privateKey.keystore");
//
    RSAPublicKey rsaPublicKey = RSAUtil.readPublicKeyFromString(publicKey);
    RSAPrivateKey rsaPrivateKey = RSAUtil.readPrivateKeyFromString(privateKey);

    HashMap<String, Object> map = new HashMap<>(1);
    map.put("name", "zgd");
    map.put("age", "18");

//    String generateToken = generateToken("aaa",map, 1000L * 60 ,rsaPublicKey,rsaPrivateKey);
//    System.out.println("generateToken = " + generateToken);

    String sign = "eyJhbGciOiJSUzI1NiJ9.eyJuYW1lIjoiemdkIiwiaXNzIjoiemdkIiwic3ViIjoiYWFhIiwiZXhwIjoxNTYzNDE0NTAxLCJpYXQiOjE1NjMzNTQ1MDEsImFnZSI6IjE4In0.UV1jh0B2H8b58Icn8vBoqsG0M2vaVtJcQ0q8aD1WsAnVlDbBEEXNTM_1_8chuFWvd7GGlpVNSapvfiWD6e5CbnDsYaWx1dg07RHGcV-CbLHCwY03TkLSkDHUbxQVC-lMJdjWkazVY8Mdx_j-3O16VGmVRMy768t-SezQQPPRYNg";

//    boolean b = validateToken(generateToken,rsaPublicKey,rsaPrivateKey);
//    System.out.println("b = " + b);

    Claims claims = parseToken(sign, rsaPublicKey, rsaPrivateKey);
    System.out.println("getClaims= " + JSON.toJSONString(claims));
    System.out.println("getClaims= " + claims.getSubject());
    System.out.println("name = " + claims.get("name"));
  }


  public static void hmacDemo() {
    HashMap<String, Object> map = new HashMap<>(1);
    map.put("name", "zgd");
    map.put("age", "18");
//    String generateToken = generateToken("aaa", map, 1000L * 60);
//    System.out.println("generateToken = " + generateToken);
    String sign = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiemdkIiwiaXNzIjoiemdkIiwic3ViIjoiYWFhIiwiZXhwIjoxNTYzMzU0OTgxLCJpYXQiOjE1NjMzNTQ5MjIsImFnZSI6IjE4In0.oPfGAgwcGNDKUAcMR_Tf8p9_Tc3u7B5gYCwt2QCgf7M";

    boolean b = validateToken(sign);
    System.out.println("b = " + b);
    Claims claims = parseToken(sign);
    System.out.println("getClaims= " + JSON.toJSONString(claims));
    System.out.println("getClaims= " + claims.getSubject());
    System.out.println("getClaims= " + claims.getExpiration().toLocaleString());
    System.out.println("name = " + claims.get("name"));

  }
}
