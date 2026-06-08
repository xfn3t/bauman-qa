package ru.bauman.seminar.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

	@Around("@annotation(ru.bauman.seminar.common.aop.MeasureExecutionTime)")
	public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		MeasureExecutionTime annotation = method.getAnnotation(MeasureExecutionTime.class);

		String operationName = annotation.operationName().isBlank()
				? signature.getDeclaringType().getSimpleName() + "." + method.getName()
				: annotation.operationName();

		long startNano = System.nanoTime();
		try {
			return joinPoint.proceed();
		} finally {
			long elapsedMs = (System.nanoTime() - startNano) / 1_000_000;
			log.info("[TIMING] ⏱ {} | Время выполнения: {} мс", operationName, elapsedMs);
		}
	}
}