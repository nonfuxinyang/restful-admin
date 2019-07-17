package com.zgd.shop.common.util;

import com.alibaba.fastjson.JSON;
import com.zgd.shop.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Exrickx
 */
@Slf4j
public class ResponseUtil {

    public static void out(Result result){
        out(200,result);
    }

    /**
     *  使用response输出JSON
     * @param statusCode
     * @param result
     */
    public static void out( int statusCode, Result result){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        ServletOutputStream out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(statusCode);
            out = response.getOutputStream();
            out.write(JSON.toJSONString(result).getBytes());
        } catch (Exception e) {
            log.error("[ResponseUtil] 响应出错 ",e);
        } finally{
            if(out!=null){
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
