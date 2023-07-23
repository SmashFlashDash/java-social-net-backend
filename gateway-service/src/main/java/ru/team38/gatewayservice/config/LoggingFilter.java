package ru.team38.gatewayservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    private final Map<String, String> urlToServiceNameMap = new HashMap<>();

    public LoggingFilter() {
        urlToServiceNameMap.put("/api/v1/auth.*", "user-service");
        urlToServiceNameMap.put("/api/v1/friends.*", "user-service");
        urlToServiceNameMap.put("/api/v1/account.*", "user-service");
        urlToServiceNameMap.put("/api/v1/notifications.*", "user-service");
        urlToServiceNameMap.put("/api/v1/post.*", "communications-service");
        urlToServiceNameMap.put("/api/v1/storage.*","communications-service");
        urlToServiceNameMap.put("/api/v1/geo.*","user-service");
        urlToServiceNameMap.put("/api/v1/dialogs.*", "communications-service");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String serviceName = getServiceNameFromRequest(request);
        log.info("Request URI: {}, Service: {}", request.getRequestURI(), serviceName);
        filterChain.doFilter(request, response);
    }

    private String getServiceNameFromRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        for (Map.Entry<String, String> entry : urlToServiceNameMap.entrySet()) {
            if (requestURI.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "Unknown service";
    }
}