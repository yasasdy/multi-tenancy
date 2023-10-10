package com.sample.multitenant.mongo.interceptor;

import com.sample.multitenant.mongo.context.TenantContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@AllArgsConstructor
@Slf4j
@Component
public class HttpInterceptor implements HandlerInterceptor {

    private static final String TENANT_ID_HEADER = "tenantId";

    @Autowired
    TenantContext tenantContext;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
                             final Object handler) {
        String tenantId = request.getHeader(TENANT_ID_HEADER);
        TenantContext.setTenantId(tenantId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) {
        TenantContext.clear();
    }
}
