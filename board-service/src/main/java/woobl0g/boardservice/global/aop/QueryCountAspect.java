package woobl0g.boardservice.global.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import woobl0g.boardservice.global.query.QueryCounter;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class QueryCountAspect {

    private final QueryCounter queryCounter;

    @Around("@annotation(QueryCountLogging)")
    public Object queryCount(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        String methodName = joinPoint.getSignature().getName();
        int count = queryCounter.getCount();
        log.info("[Query Count] 메서드명: {} | 실행 쿼리: {}개", methodName, count);
        return result;
    }
}
