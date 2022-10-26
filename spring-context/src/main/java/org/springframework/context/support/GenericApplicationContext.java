/*
 * Copyright 2002-2019 the original author or authors.
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
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 通用的ApplicationContext实现，它拥有一个单独的内部{@link org.springframe.beans.factory .support。DefaultListableBeanFactory}实例，
 * 并且不假定有特定的bean定义格式。实现{@link org.springframework.beans.factory.support。接口，以便允许将任何bean定义读取器应用到它。
 * 典型用法是通过{@link org.springframework.beans.factory.support注册各种bean定义。
 * BeanDefinitionRegistry}接口，然后调用{@link refresh()}来使用应用程序上下文语义初始化这些bean
 * (处理{@link org.springframework.context.xml)。applicationcontexttaware}、
 * 自动检测{@link org.springframe.beans.factory .config. beanfactorypostprocessor BeanFactoryPostProcessors}等)。
 * 与为每次刷新创建新的内部BeanFactory实例的其他ApplicationContext实现相比，该上下文的内部BeanFactory从一开始就可用，
 * 可以在其上注册bean定义。{@link refresh()}只能被调用一次。<p>使用示例:<pre class="code"> GenericApplicationContext ctx =
 * new GenericApplicationContext();xmlReader = new XmlBeanDefinitionReader(ctx);xmlReader。loadBeanDefinitions
 * (新ClassPathResource("中"));PropertiesBeanDefinitionReader propReader = new PropertiesBeanDefinitionReader(ctx);
 * propReader。loadBeanDefinitions(新ClassPathResource(“otherBeans.properties”);ctx.refresh ();MyBean MyBean =
 * (MyBean) ctx.getBean(" MyBean ");.．.对于XML bean定义的典型情况，只需使用{@link ClassPathXmlApplicationContext}或
 * {@link FileSystemXmlApplicationContext}，它们更容易设置——但灵活性较差，因为您可以仅为XML bean定义使用标准资源位置，
 * 而不是混合任意的bean定义格式。在web环境中等价的是{@link org.springframework.web.context.support.XmlWebApplicationContext}。
 * 对于应该以可刷新的方式读取特殊bean定义格式的自定义应用程序上下文实现，考虑从{@link AbstractRefreshableApplicationContext}基类派生。
 */
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {

	private final DefaultListableBeanFactory beanFactory;

	@Nullable
	private ResourceLoader resourceLoader;

	private boolean customClassLoader = false;

	private final AtomicBoolean refreshed = new AtomicBoolean();


	public GenericApplicationContext() {
		this.beanFactory = new DefaultListableBeanFactory();
	}


	public GenericApplicationContext(DefaultListableBeanFactory beanFactory) {
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		this.beanFactory = beanFactory;
	}


	public GenericApplicationContext(@Nullable ApplicationContext parent) {
		this();
		setParent(parent);
	}


	public GenericApplicationContext(DefaultListableBeanFactory beanFactory, ApplicationContext parent) {
		this(beanFactory);
		setParent(parent);
	}


	@Override
	public void setParent(@Nullable ApplicationContext parent) {
		super.setParent(parent);
		this.beanFactory.setParentBeanFactory(getInternalParentBeanFactory());
	}

	public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
		this.beanFactory.setAllowBeanDefinitionOverriding(allowBeanDefinitionOverriding);
	}

	/**
	 * 设置是否允许bean之间的循环引用—并自动尝试解析它们。< p >默认是“真正的”。关闭此选项可在遇到循环引用时抛出异常，完全禁止循环引用
	 * @since 3.0
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowCircularReferences
	 */
	public void setAllowCircularReferences(boolean allowCircularReferences) {
		this.beanFactory.setAllowCircularReferences(allowCircularReferences);
	}

	/**
	 * 设置一个用于此上下文的ResourceLoader。如果设置了，上下文将把所有{@code getResource}调用委托给给定的ResourceLoader。
	 * 如果未设置，将应用默认资源加载。指定一个自定义ResourceLoader的主要原因是以一种特定的方式解析资源路径(没有URL前缀)。
	 * 默认行为是将此类路径解析为类路径位置。要将资源路径解析为文件系统位置，请在这里指定一个FileSystemResourceLoader。
	 * 你也可以传入一个完整的ResourcePatternResolver，它将被上下文自动检测到，并用于{@code getResources}调用。
	 * 否则，将应用默认的资源模式匹配。
	 */
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}


	//---------------------------------------------------------------------
	// ResourceLoader ResourcePatternResolver覆盖必要时
	//---------------------------------------------------------------------

	/**
	 * 如果设置了，此实现将委托给该上下文的ResourceLoader，返回到默认的超类行为else。
	 * @see #setResourceLoader
	 */
	@Override
	public Resource getResource(String location) {
		if (this.resourceLoader != null) {
			return this.resourceLoader.getResource(location);
		}
		return super.getResource(location);
	}

	/**
	 * 如果实现了ResourcePatternResolver接口，则该实现将委托给该上下文的ResourceLoader，退回到默认的超类行为else。
	 * @see #setResourceLoader
	 */
	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		if (this.resourceLoader instanceof ResourcePatternResolver) {
			return ((ResourcePatternResolver) this.resourceLoader).getResources(locationPattern);
		}
		return super.getResources(locationPattern);
	}

	@Override
	public void setClassLoader(@Nullable ClassLoader classLoader) {
		super.setClassLoader(classLoader);
		this.customClassLoader = true;
	}

	@Override
	@Nullable
	public ClassLoader getClassLoader() {
		if (this.resourceLoader != null && !this.customClassLoader) {
			return this.resourceLoader.getClassLoader();
		}
		return super.getClassLoader();
	}


	//---------------------------------------------------------------------
	// AbstractApplicationContext模板方法的实现
	//---------------------------------------------------------------------

	/**
	 * 什么都不做:我们持有一个内部BeanFactory，并依靠调用者通过我们的公共方法(或BeanFactory的)注册bean。
	 */
	@Override
	protected final void refreshBeanFactory() throws IllegalStateException {
		if (!this.refreshed.compareAndSet(false, true)) {
			throw new IllegalStateException(
					"GenericApplicationContext does not support multiple refresh attempts: just call 'refresh' once");
		}
		this.beanFactory.setSerializationId(getId());
	}

	@Override
	protected void cancelRefresh(BeansException ex) {
		this.beanFactory.setSerializationId(null);
		super.cancelRefresh(ex);
	}

	/**
	 * Not much to do: We hold a single internal BeanFactory that will never
	 * get released.
	 */
	@Override
	protected final void closeBeanFactory() {
		this.beanFactory.setSerializationId(null);
	}

	/**
	 * Return the single internal BeanFactory held by this context
	 * (as ConfigurableListableBeanFactory).
	 */
	@Override
	public final ConfigurableListableBeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * 返回此上下文的底层bean工厂，用于注册bean定义。
	 * 注意:<b>您需要调用{@link refresh()}来使用应用程序上下文语义初始化bean工厂及其包含的bean(自动检测BeanFactoryPostProcessors等)。
	 * @return the internal bean factory (as DefaultListableBeanFactory)
	 */
	public final DefaultListableBeanFactory getDefaultListableBeanFactory() {
		return this.beanFactory;
	}

	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		assertBeanFactoryActive();
		return this.beanFactory;
	}


	//---------------------------------------------------------------------
	// 实现BeanDefinitionRegistry
	//---------------------------------------------------------------------

	@Override
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
			throws BeanDefinitionStoreException {

		this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
	}

	@Override
	public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		this.beanFactory.removeBeanDefinition(beanName);
	}

	@Override
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		return this.beanFactory.getBeanDefinition(beanName);
	}

	@Override
	public boolean isBeanNameInUse(String beanName) {
		return this.beanFactory.isBeanNameInUse(beanName);
	}

	@Override
	public void registerAlias(String beanName, String alias) {
		this.beanFactory.registerAlias(beanName, alias);
	}

	@Override
	public void removeAlias(String alias) {
		this.beanFactory.removeAlias(alias);
	}

	@Override
	public boolean isAlias(String beanName) {
		return this.beanFactory.isAlias(beanName);
	}


	//---------------------------------------------------------------------
	// 注册单个bean的方便方法
	//---------------------------------------------------------------------

	/**
	 * 从给定的bean类注册一个bean，可选地提供显式构造函数参数，以便在自动装配过程中考虑。
	 * @param constructorArgs自定义参数值，将被输入到Spring的构造函数解析算法中，解析所有的参数或特定的参数，
	 *                                其余的将通过常规自动装配(可能是{@code null}或空)进行解析。
	 */
	public <T> void registerBean(Class<T> beanClass, Object... constructorArgs) {
		registerBean(null, beanClass, constructorArgs);
	}

	/**
	 * 从给定的bean类注册一个bean，可选地提供显式构造函数参数，以便在自动装配过程中考虑。
	 * @param beanName bean的名称(可能是{@code null}) @param beanClass bean的类@param constructorArgs自定义参数值，
	 *                    将被输入到Spring的构造函数解析算法中，解析所有的参数或特定的参数，其余的将通过常规自动装配进行解析
	 * @since 5.2 (since 5.0 on the AnnotationConfigApplicationContext subclass)
	 */
	public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, Object... constructorArgs) {
		registerBean(beanName, beanClass, (Supplier<T>) null,
				bd -> {
					for (Object arg : constructorArgs) {
						bd.getConstructorArgumentValues().addGenericArgumentValue(arg);
					}
				});
	}

	/**
	 * 从给定的bean类中注册一个bean，可选地定制其bean定义元数据(通常声明为lambda表达式)。
	 * @param beanClass bean的类(解析自动连接的公共构造函数，可能只是默认构造函数)@param定制一个或多个回调函数，
	 *                     用于定制工厂的{@link BeanDefinition}，例如设置lazy-init或主标志
	 * @see #registerBean(String, Class, Supplier, BeanDefinitionCustomizer...)
	 */
	public final <T> void registerBean(Class<T> beanClass, BeanDefinitionCustomizer... customizers) {
		registerBean(null, beanClass, null, customizers);
	}

	/**
	 *从给定的bean类中注册一个bean，可选地定制其bean定义元数据(通常声明为lambda表达式)。
	 * @param beanName bean的名称(可能是{@code null})
	 * @param beanClass bean的类(解析自动连接的公共构造函数，可能只是默认构造函数)
	 * @param customizers一个或多个自定义工厂的{@link BeanDefinition}的回调，例如设置lazy-init或主标志
	 * @since 5.0
	 * @see #registerBean(String, Class, Supplier, BeanDefinitionCustomizer...)
	 */
	public final <T> void registerBean(
			@Nullable String beanName, Class<T> beanClass, BeanDefinitionCustomizer... customizers) {

		registerBean(beanName, beanClass, null, customizers);
	}

	/**
	 * 从给定的bean类注册一个bean，使用给定的提供者获取一个新实例(通常声明为lambda表达式或方法引用)，可选地定制其bean定义元数据(同样通常声明为lambda表达式)。
	 * @param供应商一个用于创建bean实例的回调函数
	 * @param customizers一个或多个用于定制工厂的{@link BeanDefinition}的回调函数，例如设置lazy-init或primary标志
	 * @since 5.0
	 * @see #registerBean(String, Class, Supplier, BeanDefinitionCustomizer...)
	 */
	public final <T> void registerBean(
			Class<T> beanClass, Supplier<T> supplier, BeanDefinitionCustomizer... customizers) {

		registerBean(null, beanClass, supplier, customizers);
	}

	/**
	 * 从给定的bean类注册一个bean，使用给定的提供者获取一个新实例(通常声明为lambda表达式或方法引用)，
	 * 可选地定制其bean定义元数据(同样通常声明为lambda表达式)。这个方法可以被重写以适应所有{@code registerBean}方法的注册机制(因为它们都委托给这个方法)。
	 * @param beaname bean的名称(可能是{@code null}) @param beanClass bean的类@param供应商一个创建bean实例的回调(如果是{@code null}，
	 *                   解析一个公共构造函数以自动连接代替)@param customizers一个或多个自定义工厂的{@link BeanDefinition}的回调，
	 *                   例如设置一个lazy-init或主标志
	 * @since 5.0
	 */
	public <T> void registerBean(@Nullable String beanName, Class<T> beanClass,
			@Nullable Supplier<T> supplier, BeanDefinitionCustomizer... customizers) {

		ClassDerivedBeanDefinition beanDefinition = new ClassDerivedBeanDefinition(beanClass);
		if (supplier != null) {
			beanDefinition.setInstanceSupplier(supplier);
		}
		for (BeanDefinitionCustomizer customizer : customizers) {
			customizer.customize(beanDefinition);
		}

		String nameToUse = (beanName != null ? beanName : beanClass.getName());
		registerBeanDefinition(nameToUse, beanDefinition);
	}


	/**
	 * 基于注册的{@code registerBean}的{@link RootBeanDefinition}标记子类，对公共构造函数具有灵活的自动装配。
	 */
	@SuppressWarnings("serial")
	private static class ClassDerivedBeanDefinition extends RootBeanDefinition {

		public ClassDerivedBeanDefinition(Class<?> beanClass) {
			super(beanClass);
		}

		public ClassDerivedBeanDefinition(ClassDerivedBeanDefinition original) {
			super(original);
		}

		@Override
		@Nullable
		public Constructor<?>[] getPreferredConstructors() {
			Class<?> clazz = getBeanClass();
			Constructor<?> primaryCtor = BeanUtils.findPrimaryConstructor(clazz);
			if (primaryCtor != null) {
				return new Constructor<?>[] {primaryCtor};
			}
			Constructor<?>[] publicCtors = clazz.getConstructors();
			if (publicCtors.length > 0) {
				return publicCtors;
			}
			return null;
		}

		@Override
		public RootBeanDefinition cloneBeanDefinition() {
			return new ClassDerivedBeanDefinition(this);
		}
	}

}
