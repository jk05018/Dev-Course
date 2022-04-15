package org.prgms.kdt.aop;

import org.aspectj.lang.annotation.Pointcut;

public class CommonPointCut {

	@Pointcut("execution(public * org.prgms.kdt..*Service.*(..))")
	public void servicePublicMethodPointCut(){
	}

	@Pointcut("execution(* org.prgms.kdt..*Repository.*(..))")
	public void repositoryMethodPointCut(){
	}

	@Pointcut("execution(* org.prgms.kdt..*Repository.insert(..))")
	public void repositoryInsertMethodPointCut(){
	}
}
