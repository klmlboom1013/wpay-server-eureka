package com.wpay.server.eureka;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Log4j2
@Order(1)
@WebFilter(urlPatterns = "/*")
@RequiredArgsConstructor
public class LoggingFilter implements Filter {

    private final HttpServletRequest httpServletRequest;

    /** loggingBody 디버깅 여부 (true : 사용 안함) */
    private static final boolean loggingRequestBodyUnUsedFlag = true;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("==================== {} START ====================", httpServletRequest.getRequestURI());
        this.loggingRequestHeaders();
        this.loggingRequestParams();
        this.loggingBody(this.httpServletRequest);
        chain.doFilter(request, response);
        log.info("==================== {} END ====================", httpServletRequest.getRequestURI());
    }

    private void loggingRequestHeaders() {
        StringBuilder sb = new StringBuilder();
        this.httpServletRequest.getHeaderNames().asIterator().forEachRemaining((key) ->
                sb.append("    ").append(key).append(" : ").append(this.httpServletRequest.getHeader(key)).append("\n"));
        if(Strings.isNotBlank(sb.toString()))
            log.info("[ Request Headers ]\n{}",sb.toString());
    }

    private void loggingRequestParams() {
        StringBuilder sb = new StringBuilder();
        this.httpServletRequest.getParameterNames().asIterator().forEachRemaining((key) ->
                sb.append("    ").append(key).append(" : ").append(this.httpServletRequest.getHeader(key)).append("\n"));
        if(Strings.isNotBlank(sb.toString()))
            log.info("[ Request Parameters ]\n{}",sb.toString());
    }

    public void loggingBody(HttpServletRequest request) throws IOException {

        if(loggingRequestBodyUnUsedFlag) return;

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        log.info("requestBody: \n{}", stringBuilder.toString());
    }
}
