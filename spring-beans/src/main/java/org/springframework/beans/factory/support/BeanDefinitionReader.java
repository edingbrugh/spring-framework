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

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/**
 * bean定义阅读器的简单接口。用“资源”和“字符串”位置参数指定加载方法。具体的bean定义阅读器当然可以为bean定义添加额外的加载和注册方法，具体取决于它们的bean定义格式。
 * <p>注意，bean定义阅读器不一定要实现这个接口。它仅作为希望遵循标准命名约定的bean定义读者的建议
 */
public interface BeanDefinitionReader {

	BeanDefinitionRegistry getRegistry();

	@Nullable
	ResourceLoader getResourceLoader();

	@Nullable
	ClassLoader getBeanClassLoader();

	BeanNameGenerator getBeanNameGenerator();

	int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException;

	int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException;

	int loadBeanDefinitions(String location) throws BeanDefinitionStoreException;


	int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException;

}
