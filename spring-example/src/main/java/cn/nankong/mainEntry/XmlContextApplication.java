package cn.nankong.mainEntry;

import cn.nankong.bean.User;
import cn.nankong.service.impl.DepartServiceImpl;
import cn.nankong.service.impl.UserServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @Description: xml加载bean
 * @Author NanKong
 * @Date 2022/10/10 13:41
 */
public class XmlContextApplication {

	public static void main(String[] args) {
		// 单配置文件方式一
		ApplicationContext singleConf = new ClassPathXmlApplicationContext("beans.xml");
		// 单配置文件方式二
		ApplicationContext classpath = new ClassPathXmlApplicationContext("classpath:beans.xml");
		// 多配置文件
		ClassPathXmlApplicationContext multipleConf = new ClassPathXmlApplicationContext(new String[]{"beans.xml", "beans1.xml"});
		// 绝对值路径
		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("file:/Users/luchengwen/IdeaProjects/spring-framework/spring-example/src/main/resources/beans.xml");

		User user =  singleConf.getBean("user", User.class);
		User classpathBean =  classpath.getBean("user", User.class);
		User mu = multipleConf.getBean("user", User.class);
		DepartServiceImpl departService = multipleConf.getBean("departService", DepartServiceImpl.class);
		User contextBean = classPathXmlApplicationContext.getBean("user", User.class);
		System.out.println("singleConf------" + user.getName());
		System.out.println("classpath-------" + classpathBean.getName());
		System.out.println("multipleConf-------" + mu.getName());
		System.out.println("contextBean-------" + contextBean.getName());
		System.out.println("departService-------" + departService.getDepartInfo().getName());

	}
}
