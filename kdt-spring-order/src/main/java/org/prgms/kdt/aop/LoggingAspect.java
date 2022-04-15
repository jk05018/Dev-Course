package org.prgms.kdt.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component // 빈으로 등록해야 한다. 자동 주입이 있을라나? Component Scan의 대상이 된다
public class LoggingAspect {
	private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

	// 여러 어드바이스가 결국은 메서드가 된다.
	// advise는 pointcut을 줘야 한다.
	// advice가 다 적용 될 수 있는 것이다.mn
	// @Around("org.prgms.kdt.aop.CommonPointCut.servicePublicMethodPointCut()")// advice?
	@Around("@annotation(org.prgms.kdt.aop.TrackTime)")
	public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("Before method called. {}", joinPoint.getSignature());
		final long startTime = System.nanoTime(); // 호출하고 반환하는 데까지 어느 정도의 시간이 걸리
		final long endTime = System.nanoTime() - startTime; // 호출하고 반환하는 데까지 어느 정도의 시간이 걸리
		final Object result = joinPoint.proceed();
		log.info("After Method called with result => {} and time taken {} nanoseconds", result, endTime);
		return result;
	}
}
