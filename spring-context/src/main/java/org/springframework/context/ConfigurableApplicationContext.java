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

package org.springframework.context;

import java.io.Closeable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.lang.Nullable;

/**
 * 由大多数(如果不是所有)应用程序上下文实现的SPI接口。除了{@link org.springframework.context中的应用程序上下文客户端方法外，
 * 还提供配置应用程序上下文的工具。ApplicationContext}接口。这里封装了配置和生命周期方法，以避免它们对ApplicationContext客户机代码明显。
 * 目前的方法应该只用于启动和关闭代码。
 */
public interface ConfigurableApplicationContext extends ApplicationContext, Lifecycle, Closeable {

	/**
	 * 这些字符的任意数量都被认为是单个String值中多个上下文配置路径之间的分隔符。
	 */
	String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

	/**
	 * 工厂中的ConversionService bean的名称。如果没有提供，则应用默认转换规则。
	 */
	String CONVERSION_SERVICE_BEAN_NAME = "conversionService";

	/**
	 * 工厂中的LoadTimeWeaver bean的名称。如果提供了这样一个bean，上下文将使用一个临时ClassLoader进行类型匹配，以便允许LoadTimeWeaver处理所有实际的bean类。
	 */
	String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";

	/**
	 * 工厂中的{@link Environment} bean的名称。
	 */
	String ENVIRONMENT_BEAN_NAME = "environment";

	/**
	 *工厂中的System属性bean的名称。
	 */
	String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";

	/**
	 * 工厂中的系统环境bean的名称。
	 */
	String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";


	String SHUTDOWN_HOOK_THREAD_NAME = "SpringContextShutdownHook";


	void setId(String id);

	/**
	 * 设置此应用程序上下文的父级。注意，不应该更改父类:只有当创建该类的对象时它不可用时，才应该在构造函数外部设置父类，
	 * 例如在WebApplicationContext设置的情况下。@param parent父上下文
	 */
	void setParent(@Nullable ApplicationContext parent);


	void setEnvironment(ConfigurableEnvironment environment);


	@Override
	ConfigurableEnvironment getEnvironment();

	/**
	 * 添加一个新的BeanFactoryPostProcessor，它将在刷新时应用于该应用程序上下文的内部bean工厂，然后再计算任何bean定义。
	 * 在上下文配置期间调用。@param postProcessor注册工厂处理器
	 */
	void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

	/**
	 * 添加一个新的ApplicationListener，它将在上下文事件(如上下文刷新和上下文关闭)时收到通知。
	 * 注意，在这里注册的任何ApplicationListener将在刷新时应用，如果上下文还未激活，
	 * 或者在当前事件多播程序运行时应用，如果上下文已经激活。注册ApplicationListener
	 */
	void addApplicationListener(ApplicationListener<?> listener);

	/**
	 * 指定装入类路径资源和bean类的ClassLoader。这个上下文类装入器将被传递到内部bean工厂。
	 */
	void setClassLoader(ClassLoader classLoader);

	/**
	 * 在此应用程序上下文中注册给定的协议解析器，允许处理额外的资源协议。任何这样的解析器都将在此上下文的标准解析规则之前被调用。
	 * 因此，它也可以覆盖任何默认规则。
	 * @since 4.3
	 */
	void addProtocolResolver(ProtocolResolver resolver);

	/**
	 * 加载或刷新配置的持久表示，它可能来自基于java的配置、XML文件、属性文件、关系数据库模式或其他格式。
	 * 因为这是一个启动方法，如果失败，它应该销毁已经创建的单例，以避免资源悬空。换句话说，在调用此方法之后，
	 * 应该实例化所有或根本不实例化单例。如果已经初始化且不支持多次刷新尝试，则抛出IllegalStateException
	 */
	void refresh() throws BeansException, IllegalStateException;

	void registerShutdownHook();

	@Override
	void close();


	boolean isActive();

	/**
	 * 返回此应用程序上下文的内部bean工厂。可用于访问底层工厂的特定功能。<p>注意:不要使用这个来后处理bean工厂;单例之前已经实例化过了。
	 * 在接触bean之前，使用BeanFactoryPostProcessor来拦截BeanFactory设置过程。通常，这个内部工厂只有在上下文是活动的时候才可以访问，
	 * 也就是说，在{@link refresh()}和{@link close()}之间。{@link isActive()}标志可用于检查上下文是否处于适当的状态。
	 * @see #addBeanFactoryPostProcessor
	 */
	ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

}
