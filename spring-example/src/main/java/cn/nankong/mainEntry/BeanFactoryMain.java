package cn.nankong.mainEntry;

import cn.nankong.service.impl.DepartServiceImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @Description: BeanFactoryMain
 * @Author NanKong
 * @Date 2022/10/10 18:51
 */
public class BeanFactoryMain {

	public static void main(String[] args) {
		Resource resource = new ClassPathResource("beans1.xml");
		BeanFactory beanFactory = new DefaultListableBeanFactory();
		BeanDefinitionReader bddr = new XmlBeanDefinitionReader((BeanDefinitionRegistry)beanFactory);
		bddr.loadBeanDefinitions(resource);
		DepartServiceImpl departService = beanFactory.getBean("departService", DepartServiceImpl.class);
		System.out.println(departService.getDepartInfo().getName());
	}
}
