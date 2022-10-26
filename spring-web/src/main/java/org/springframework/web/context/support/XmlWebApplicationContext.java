/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.context.support;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * {@link org.springframework.web.context。WebApplicationContext}实现，它从XML文档中获取配置，
 * 由{@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}理解。
 * 这本质上相当于{@link org.springframework.context.support。GenericXmlApplicationContext}用于web环境。
 * 默认情况下，根上下文的配置将取自"WEB-INFapplicationContext.xml"，命名空间为"test-servlet"的上下文的配置将取自"web-intest-servlet.xml"
 * (就像DispatcherServlet实例的servlet名称为"test")。配置位置的默认值可以通过{@link org.springframework.web.context的"contextConfigLocation"上下文参数来覆盖。
 * ContextLoader}和{@link org.springframework.web.servlet.FrameworkServlet}的初始化参数。配置位置既可以表示具体的文件，如“WEB-INFcontext.xml”，
 * 也可以表示ant风格的模式，如“WEB-INFcontext.xml”(参见{@link org.springframework.util。模式细节的PathMatcher} javadoc)。
 * 注意:在多个配置位置的情况下，以后的bean定义将覆盖早期加载文件中定义的。可以利用这一点，通过额外的XML文件故意覆盖某些bean定义。
 * 对于以不同bean定义格式读取的WebApplicationContext，创建一个类似的{@link AbstractRefreshableWebApplicationContext}的子类。
 * 这样的上下文实现可以指定为ContextLoader的"contextClass" context-param或FrameworkServlet的"contextClass" init-param。
 */
public class XmlWebApplicationContext extends AbstractRefreshableWebApplicationContext {

	/** 根上下文的默认配置位置。. */
	public static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/applicationContext.xml";

	/** 用于为名称空间构建配置位置的默认前缀。 */
	public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/";

	/** 用于为名称空间构建配置位置的默认后缀。 */
	public static final String DEFAULT_CONFIG_LOCATION_SUFFIX = ".xml";


	/**
	 *通过XmlBeanDefinitionReader加载bean定义。
	 */
	@Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
		// Create a new XmlBeanDefinitionReader for the given BeanFactory.
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

		// 使用该上下文的资源加载环境配置bean定义阅读器。
		beanDefinitionReader.setEnvironment(getEnvironment());
		beanDefinitionReader.setResourceLoader(this);
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

		// 允许子类提供读取器的自定义初始化，然后实际加载bean定义。
		initBeanDefinitionReader(beanDefinitionReader);
		loadBeanDefinitions(beanDefinitionReader);
	}

	/**
	 *初始化用于加载此上下文的bean定义的bean定义读取器。默认实现为空。<p>可以在子类中重写，
	 * 例如关闭XML验证或使用不同的XmlBeanDefinitionParser实现。@param beanDefinitionReader此上下文使用的bean定义阅读器
	 */
	protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
	}

	/**
	 * 使用给定的XmlBeanDefinitionReader加载bean定义。bean工厂的生命周期由refreshBeanFactory方法处理;因此，这个方法只是用来加载或注册bean定义。
	 * <p>委托给ResourcePatternResolver将位置模式解析到资源实例中。
	 */
	protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws IOException {
		String[] configLocations = getConfigLocations();
		if (configLocations != null) {
			for (String configLocation : configLocations) {
				reader.loadBeanDefinitions(configLocation);
			}
		}
	}

	/**
	 * 根上下文的默认位置是“WEB-INFapplicationContext.xml”，以及名称空间为“test-servlet”的上下文的“web-intest-servlet.xml”(比如具有servlet名称为“test”的DispatcherServlet实例)。
	 */
	@Override
	protected String[] getDefaultConfigLocations() {
		if (getNamespace() != null) {
			return new String[] {DEFAULT_CONFIG_LOCATION_PREFIX + getNamespace() + DEFAULT_CONFIG_LOCATION_SUFFIX};
		}
		else {
			return new String[] {DEFAULT_CONFIG_LOCATION};
		}
	}

}
