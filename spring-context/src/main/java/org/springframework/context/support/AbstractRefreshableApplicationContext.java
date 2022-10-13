/*
 * Copyright 2002-2020 the original author or authors.
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
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.lang.Nullable;

/**
 * {@link org.springframework.context的基类。ApplicationContext}实现应该支持对{@link refresh()}的多次调用，每次都创建一个新的内部bean工厂实例。
 * 通常(但不一定)，这样的上下文将由一组用于加载bean定义的配置位置驱动。由子类实现的唯一方法是{@link loadBeanDefinitions}，它在每次刷新时被调用。
 * 一个具体的实现应该将bean定义加载到给定的{@link org.springframework.beans.factory.support。DefaultListableBeanFactory}，
 * 通常委托给一个或多个特定的bean定义阅读器。注意，WebApplicationContexts有一个类似的基类。< b > {@link org.springframework.web.context.support。
 * 提供了相同的子类化策略，但是额外地预先实现了web环境的所有上下文功能。还有一种预定义的方式来接收web上下文的配置位置。这个基类的具体独立子类
 * ，以特定的bean定义格式读取，是{@link ClassPathXmlApplicationContext}和{@link FileSystemXmlApplicationContext}，
 * 它们都派生自公共的{@link AbstractXmlApplicationContext}基类;{@link org.springframework.context.annotation。
 * AnnotationConfigApplicationContext}支持{@code @Configuration}注释类作为bean定义的源。
 */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

	@Nullable
	private Boolean allowBeanDefinitionOverriding;

	@Nullable
	private Boolean allowCircularReferences;

	@Nullable
	private volatile DefaultListableBeanFactory beanFactory;


	public AbstractRefreshableApplicationContext() {
	}

	public AbstractRefreshableApplicationContext(@Nullable ApplicationContext parent) {
		super(parent);
	}


	/**
	 * 设置是否应该通过注册具有相同名称的不同定义(自动替换前者)来覆盖bean定义。如果不是，则会抛出异常。
	 * 默认设置是“真实的”。@see org.springframework.beans.factory.support.DefaultListableBeanFactorysetAllowBeanDefinitionOverriding
	 */
	public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
		this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
	}

	/**
	 * 设置是否允许bean之间的循环引用—并自动尝试解析它们。< p >默认是“真正的”。关闭此选项可在遇到循环引用时抛出异常，完全禁止循环引用。
	 */
	public void setAllowCircularReferences(boolean allowCircularReferences) {
		this.allowCircularReferences = allowCircularReferences;
	}


	/**
	 * 该实现对该上下文的底层bean工厂执行实际刷新，关闭前一个bean工厂(如果有的话)，并为上下文生命周期的下一个阶段初始化一个新的bean工厂。
	 */
	@Override
	protected final void refreshBeanFactory() throws BeansException {
		if (hasBeanFactory()) {
			destroyBeans();
			closeBeanFactory();
		}
		try {
			DefaultListableBeanFactory beanFactory = createBeanFactory();
			beanFactory.setSerializationId(getId());
			customizeBeanFactory(beanFactory);
			// TODO 通过XmlBeanDefinitionReader加载bean定义。
			loadBeanDefinitions(beanFactory);
			this.beanFactory = beanFactory;
		}
		catch (IOException ex) {
			throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
		}
	}

	@Override
	protected void cancelRefresh(BeansException ex) {
		DefaultListableBeanFactory beanFactory = this.beanFactory;
		if (beanFactory != null) {
			beanFactory.setSerializationId(null);
		}
		super.cancelRefresh(ex);
	}

	@Override
	protected final void closeBeanFactory() {
		DefaultListableBeanFactory beanFactory = this.beanFactory;
		if (beanFactory != null) {
			beanFactory.setSerializationId(null);
			this.beanFactory = null;
		}
	}

	/**
	 * Determine whether this context currently holds a bean factory,
	 * i.e. has been refreshed at least once and not been closed yet.
	 */
	protected final boolean hasBeanFactory() {
		return (this.beanFactory != null);
	}

	@Override
	public final ConfigurableListableBeanFactory getBeanFactory() {
		DefaultListableBeanFactory beanFactory = this.beanFactory;
		if (beanFactory == null) {
			throw new IllegalStateException("BeanFactory not initialized or already closed - " +
					"call 'refresh' before accessing beans via the ApplicationContext");
		}
		return beanFactory;
	}

	/**
	 * Overridden to turn it into a no-op: With AbstractRefreshableApplicationContext,
	 * {@link #getBeanFactory()} serves a strong assertion for an active context anyway.
	 */
	@Override
	protected void assertBeanFactoryActive() {
	}

	/**
	 *为此上下文中创建一个内部bean工厂。每次{@link refresh()}尝试时调用。默认实现创建一个
	 * {@link org.springframework.beans.factory.support。DefaultListableBeanFactory}
	 * 使用该上下文的父级的{@linkplain getInternalParentBeanFactory()内部bean工厂}作为父级bean工厂。
	 * 可以在子类中重写，例如自定义DefaultListableBeanFactory的设置。
	 */
	protected DefaultListableBeanFactory createBeanFactory() {
		return new DefaultListableBeanFactory(getInternalParentBeanFactory());
	}

	/**
	 * 自定义此上下文使用的内部bean工厂。每次{@link refresh()}尝试时调用。如果指定，默认实现应用此上下文的{@linkplain setallowbeandefinitionoverride "
	 * allowbeandefinitionoverride "}和{@linkplain setAllowCircularReferences "allowCircularReferences"}设置。可以在子类中重写以自定义
	 * {@link DefaultListableBeanFactory}的任何设置。
	 */
	protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
		if (this.allowBeanDefinitionOverriding != null) {
			beanFactory.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
		}
		if (this.allowCircularReferences != null) {
			beanFactory.setAllowCircularReferences(this.allowCircularReferences);
		}
	}

	/**
	 * 将bean定义加载到给定的bean工厂中，通常是通过委托给一个或多个bean定义读取器。
	 */
	protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
			throws BeansException, IOException;

}
