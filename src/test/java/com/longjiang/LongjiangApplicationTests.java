package com.longjiang;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
@SpringBootTest
class LongjiangApplicationTests implements ApplicationContextAware {
	private ApplicationContext all;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.all=applicationContext;
	}

}
