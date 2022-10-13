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

package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/**
 * 独立的XML应用程序上下文，从文件系统或url获取上下文定义文件，将普通路径解释为相对的文件系统位置(例如。“mydirmyfile.txt”)。
 * 对于测试工具和独立环境都很有用。注意:<b>普通路径将始终被解释为相对于当前VM工作目录，即使它们以斜杠开头。
 * (这与Servlet容器中的语义一致)<b>使用显式的“file:”前缀强制文件绝对路径。配置位置的默认值可以通过{@link getConfigLocations}来覆盖，
 * 配置位置既可以表示具体的文件，如"myfilescontext.xml"，也可以表示ant风格的模式，如"myfiles-context.xml"(参见{@link org.springframework.util.xml)
 * 。AntPathMatcher} javadoc用于模式细节)。注意:在多个配置位置的情况下，以后的bean定义将覆盖早期加载文件中定义的。可以利用这一点，
 * 通过额外的XML文件故意覆盖某些bean定义。这是一个简单的，一站式商店方便的ApplicationContext。考虑将{@link GenericApplicationContext}类
 * 与{@link org.springframework.beans.factory.xml结合使用。XmlBeanDefinitionReader}用于更灵活的上下文设置
 */
public class FileSystemXmlApplicationContext extends AbstractXmlApplicationContext {


	public FileSystemXmlApplicationContext() {
	}


	public FileSystemXmlApplicationContext(ApplicationContext parent) {
		super(parent);
	}


	public FileSystemXmlApplicationContext(String configLocation) throws BeansException {
		this(new String[] {configLocation}, true, null);
	}


	public FileSystemXmlApplicationContext(String... configLocations) throws BeansException {
		this(configLocations, true, null);
	}


	public FileSystemXmlApplicationContext(String[] configLocations, ApplicationContext parent) throws BeansException {
		this(configLocations, true, parent);
	}


	public FileSystemXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
		this(configLocations, refresh, null);
	}


	public FileSystemXmlApplicationContext(
			String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
			throws BeansException {

		super(parent);
		setConfigLocations(configLocations);
		if (refresh) {
			refresh();
		}
	}



	@Override
	protected Resource getResourceByPath(String path) {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return new FileSystemResource(path);
	}

}
