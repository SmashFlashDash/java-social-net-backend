package ru.team38.common.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class LoggerAspect {
    private final ConcurrentHashMap<String, Logger> loggersMap = new ConcurrentHashMap<>();

    @Around(value = "execution(public * * (..)) && @within(ru.team38.common.aspects.LoggingClass)")
    public Object logClassMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return doLogging(joinPoint);
    }

    @Around(value = "@annotation(ru.team38.common.aspects.LoggingMethod)")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return doLogging(joinPoint);
    }

    private Object doLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getName();
        String methodName = joinPoint.getSignature().getName();
        Logger logger = loggersMap.get(className);
        if (logger == null) {
            logger = LoggerFactory.getLogger(className);
            loggersMap.put(className, logger);
        }
        logger.info("Executing " + methodName);
        try {
            Object returnValue = joinPoint.proceed();
            logger.info("Success " + methodName);
            return returnValue;
        } catch (Throwable e) {
            // logger.error("Error " + methodName, e);
            logger.error("Method: " + methodName + "; Type: " + e.getClass() + "; Message: " + e.getMessage());
            throw e;
        }
    }
}
