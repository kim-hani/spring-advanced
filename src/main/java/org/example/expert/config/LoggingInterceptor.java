package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.enums.UserRole;
import org.slf4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.slf4j.LoggerFactory;


import java.time.LocalDateTime;

public class LoggingInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String user = request.getSession().getAttribute("userRole").toString();

        if(user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"관리자 권한이 필요합니다.");
            return false;
        }

        try{

            UserRole userRole = UserRole.of(user);
            if(!UserRole.ADMIN.equals(userRole)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"관리자 권한이 필요합니다.");
                return false;
            }
        }catch(InvalidRequestException e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,e.getMessage());
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        String requestUrl = request.getRequestURI();

        log.info("관리자 요청이 {} 에 {} 에서 발생 " , now , requestUrl );

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
