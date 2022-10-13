package cn.nankong.mainEntry;

import cn.nankong.bean.User;
import cn.nankong.service.impl.DepartServiceImpl;
import cn.nankong.service.impl.UserServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @Description: bean测试
 * @Author NanKong
 * @Date 2022/10/10 10:17
 */
public class FileSystemXmlApplicationMain {

	public static void main(String[] args) {
		// classpath路径加载
		ApplicationContext context = new FileSystemXmlApplicationContext("classpath:beans.xml");
		// 相对值路径加载配置文件
		ApplicationContext xmlApplicationContext = new FileSystemXmlApplicationContext("spring-example/src/main/resources/beans.xml");
		// 多文件加载配置文件
		ApplicationContext multipleConf = new FileSystemXmlApplicationContext(new String[]{"spring-example/src/main/resources/beans.xml", "spring-example/src/main/resources/beans1.xml"});
		// 绝对值路径
		FileSystemXmlApplicationContext fileSystemXmlApplicationContext = new FileSystemXmlApplicationContext("//Users/luchengwen/IdeaProjects/spring-framework/spring-example/src/main/resources/beans.xml");
		User xmlApplicationContextBean = fileSystemXmlApplicationContext.getBean("user", User.class);
		User user = context.getBean("user", User.class);
		User mu = multipleConf.getBean("user", User.class);

		DepartServiceImpl departService = multipleConf.getBean("departService", DepartServiceImpl.class);
		System.out.println("multipleConf-------" + mu.getName());
		System.out.println("departService-------" + departService.getDepartInfo().getName());
		User contextBean = xmlApplicationContext.getBean("user", User.class);
		UserServiceImpl test = (UserServiceImpl) context.getBean("userService");
		System.out.println("contextBean------" + contextBean.getName());
		System.out.println("xmlApplicationContextBean------" + xmlApplicationContextBean.getName());
	}
}
