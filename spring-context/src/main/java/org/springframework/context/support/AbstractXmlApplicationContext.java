/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.context.support;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/**
 * {@link org.springframework.context的方便基类。ApplicationContext}实现，从包含被
 * {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}理解的bean定义的XML文档绘制配置。
 * 子类只需要实现{@link getConfigResources}和{@link getConfigLocations}方法。此外，它们可能会覆盖{@link
 * getResourceByPath}钩子以特定于环境的方式解释相对路径，或者{@link getResourcePatternResolver}用于扩展模式解析。
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #getConfigResources
 * @see #getConfigLocations
 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext {

	private boolean validating = true;



	public AbstractXmlApplicationContext() {
	}

	public AbstractXmlApplicationContext(@Nullable ApplicationContext parent) {
		super(parent);
	}


	/**
	 * 设置是否使用XML验证。默认值是{@code true}。
	 */
	public void setValidating(boolean validating) {
		this.validating = validating;
	}


	/**
	 * 通过XmlBeanDefinitionReader加载bean定义。
	 */
	@Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
		// 为给定的BeanFactory创建一个新的XmlBeanDefinitionReader。
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
		// 使用该上下文的资源加载环境配置bean定义阅读器。
		beanDefinitionReader.setEnvironment(this.getEnvironment());
		beanDefinitionReader.setResourceLoader(this);
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));
		// 允许子类提供读取器的自定义初始化，然后实际加载bean定义。
		initBeanDefinitionReader(beanDefinitionReader);
		loadBeanDefinitions(beanDefinitionReader);
	}

	/**
	 * 初始化用于加载此上下文的bean定义的bean定义读取器。默认实现为空。
	 * <p>可以在子类中重写，例如关闭XML验证或使用不同的XmlBeanDefinitionParser实现。
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader#setDocumentReaderClass
	 */
	protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
		reader.setValidating(this.validating);
	}

	/**
	 * 使用给定的XmlBeanDefinitionReader加载bean定义。
	 * bean工厂的生命周期由{@link refreshBeanFactory}方法处理;因此，这个方法应该只是加载和或注册bean定义。
	 */
	protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
		Resource[] configResources = getConfigResources();
		if (configResources != null) {
			reader.loadBeanDefinitions(configResources);
		}
		String[] configLocations = getConfigLocations();
		if (configLocations != null) {
			reader.loadBeanDefinitions(configLocations);
		}
	}


	@Nullable
	protected Resource[] getConfigResources() {
		return null;
	}

}
