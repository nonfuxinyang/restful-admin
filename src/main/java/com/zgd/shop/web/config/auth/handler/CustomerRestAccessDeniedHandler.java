package com.zgd.shop.web.config.auth.handler;


import com.zgd.shop.core.error.ErrorCodeConstants;
import com.zgd.shop.core.result.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import com.zgd.shop.common.util.ResponseUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Exrickx
 */
@Component
@Slf4j
public class CustomerRestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        ResponseUtil.out(403, ResultUtil.failure(ErrorCodeConstants.PERMISSION_DENY));
    }

}
