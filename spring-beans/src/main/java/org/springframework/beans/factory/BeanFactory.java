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

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;


public interface BeanFactory {

	/**
	 * 用于解引用{@link FactoryBean}实例，并将其与FactoryBean创建的<i> <i>的bean区分开来。例如，
	 * 如果名为{@code myJndiObject}的bean是一个FactoryBean，那么获取{@code &myJndiObject}将返回工厂，而不是工厂返回的实例。
	 */
	String FACTORY_BEAN_PREFIX = "&";


	/**
	 * 返回指定bean的一个实例，该实例可以是共享的，也可以是独立的。该方法允许使用Spring BeanFactory作为单例或原型设计模式的替换。
	 * 在单例bean的情况下，调用者可以保留对返回对象的引用。<p>将别名翻译回相应的规范bean名称。<p>将询问父工厂是否在该工厂实例中找不到bean。
	 */
	Object getBean(String name) throws BeansException;

	/**
	 * 返回指定bean的一个实例，该实例可以是共享的，也可以是独立的。<p>的行为与{@link getBean(String)}相同，但如果bean不是所需的类型，
	 * 则抛出BeanNotOfRequiredTypeException，从而提供类型安全度量。这意味着不能像{@link getBean(String)}那样，在正确地转换结果时抛出ClassCastException。
	 * <p>将别名翻译回相应的规范bean名称。<p>将询问父工厂是否在该工厂实例中找不到bean。
	 */
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * <p>Allows for specifying explicit constructor arguments / factory method arguments,
	 * overriding the specified default arguments (if any) in the bean definition.
	 * @param name the name of the bean to retrieve
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 2.5
	 */
	Object getBean(String name, Object... args) throws BeansException;

	/**
	 * Return the bean instance that uniquely matches the given object type, if any.
	 * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
	 * but may also be translated into a conventional by-name lookup based on the name
	 * of the given type. For more extensive retrieval operations across sets of beans,
	 * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return an instance of the single bean matching the required type
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 * @throws BeansException if the bean could not be created
	 * @since 3.0
	 * @see ListableBeanFactory
	 */
	<T> T getBean(Class<T> requiredType) throws BeansException;


	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

	/**
	 * 返回指定bean的提供程序，允许延迟按需检索实例，包括可用性和惟一性选项。
	 */
	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

	/**
	 * 返回指定bean的提供程序，允许延迟按需检索实例，包括可用性和惟一性选项。@param requiredType bean必须匹配的类型;可以是泛型类型声明。
	 * 注意，与反射注入点相比，这里不支持集合类型。要以编程方式检索匹配特定类型的bean列表，请在这里指定实际的bean类型作为参数，
	 * 然后使用{@link ObjectProviderorderedStream()}或其lazy streamingiteration选项。
	 */
	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);

	boolean containsBean(String name);

	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;


	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	@Nullable
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 确定具有给定名称的bean的类型。更具体地说，确定{@link getBean}将为给定名称返回的对象类型。<p>对于{@link FactoryBean}，
	 * 返回FactoryBean创建的对象类型，正如{@link FactoryBeangetObjectType()}所公开的那样。根据{@code allowFactoryBeanInit}标志的不同，
	 * 如果没有可用的早期类型信息，这可能导致初始化先前未初始化的{@code FactoryBean}。<p>将别名翻译回相应的规范bean名称。
	 * <p>将询问父工厂是否在该工厂实例中找不到bean。
	 */
	@Nullable
	Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException;

	/**
	 * 返回给定bean名的别名(如果有的话)。当在{@link getBean}调用中使用时，所有这些别名都指向同一个bean。如果给定的名称是一个别名，
	 * 则将返回相应的原始bean名称和其他别名(如果有)，原始bean名称是数组中的第一个元素。<p>将询问父工厂是否在该工厂实例中找不到bean。
	 */
	String[] getAliases(String name);

}
