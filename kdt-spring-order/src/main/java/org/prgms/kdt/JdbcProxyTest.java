package org.prgms.kdt;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcProxyTest {
	private static final Logger log = LoggerFactory.getLogger(JdbcProxyTest.class);

	public static void main(String[] args) {
		final CalculatorImpl calculator = new CalculatorImpl();
		final Calculator proxyInstance = (Calculator)Proxy.newProxyInstance(
			LoggingInvocationHandler.class.getClassLoader(),
			new Class[] {Calculator.class},
			new LoggingInvocationHandler(calculator)); // 프록시 객체가 만들어
		final int result = proxyInstance.add(1, 2);
		log.info("ADD 1 , 2 -> {}", result);

	}
}

class CalculatorImpl implements Calculator {

	@Override
	public int add(int a, int b) {
		return 0;
	}
}

interface Calculator {
	int add(int a, int b);
}

class LoggingInvocationHandler implements InvocationHandler {
	private static final Logger logger = LoggerFactory.getLogger(LoggingInvocationHandler.class);
	private final Object target;

	LoggingInvocationHandler(Object target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		logger.info("{} executed {}", method.getName(), target.getClass().getCanonicalName());
		return method.invoke(target, args); // 실제 target의 메서드를 실행
		// 이것은 JDK Proxy를 이용한 방식이다? Interface 기반	}
		// Spring은 이것을 내부적으로 사용하고 있는 것이다?
	}
}
