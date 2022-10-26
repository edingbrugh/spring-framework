/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.beans.factory.xml;

import org.w3c.dom.Document;

import org.springframework.beans.factory.BeanDefinitionStoreException;

/**
 * 用于解析包含Spring bean定义的XML文档的SPI。由{@link XmlBeanDefinitionReader}用于实际解析DOM文档。<p>实例化每个文档来解析:
 * 实现可以在{@code registerBeanDefinitions}方法&mdash执行期间在实例变量中保存状态;例如，为文档中的所有bean定义的全局设置。
 */
public interface BeanDefinitionDocumentReader {

	/**
	 * 从给定的DOM文档读取bean定义，并将它们注册到给定阅读器上下文中的注册中心。
	 */
	void registerBeanDefinitions(Document doc, XmlReaderContext readerContext)
			throws BeanDefinitionStoreException;

}
