package cn.nankong.mainEntry;

import cn.nankong.annotation.AnnotationContextConfig;
import cn.nankong.bean.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @Description: 注解使用ioc
 * @Author NanKong
 * @Date 2022/10/10 13:27
 */
@Component
public class AnnotationApplicationMain {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(AnnotationContextConfig.class);
		context.refresh();
		User user1 = context.getBean("user", User.class);
		User user2 = context.getBean("user2", User.class);
		System.out.println(user1.getName());
		System.out.println(user2.getName());
		String[] beanDefinitionNames = context.getBeanDefinitionNames();
		for (String beanDefinitionName : beanDefinitionNames) {
			System.out.println(beanDefinitionName);
		}
	}
}
