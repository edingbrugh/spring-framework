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

package org.springframework.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@code java.io的{@link资源}实现。File}和{@code java.nio.file。使用一个文件系统目标。支持解析为{@code文件}和{@code URL}。
 * 实现扩展的{@link WritableResource}接口。注:从Spring Framework 5.0开始，这个{@link Resource}实现使用了NIO.2 API进行读写交互。
 * 从5.1开始，它可以用{@link java.nio.file来构造。Path}句柄，在这种情况下，它将通过NIO.2执行所有文件系统交互，只诉诸于{@link getFile()}上的{@link file}。
 */
public class FileSystemResource extends AbstractResource implements WritableResource {

	private final String path;

	@Nullable
	private final File file;

	private final Path filePath;


	/**
	 * 从文件路径创建一个新的{@code FileSystemResource}。注意:当通过{@link createRelative}构建相对资源时，这里指定的资源基路径是否以斜杠结尾是有区别的。
	 * 在“C:dir1”的情况下，相对路径将在该根下构建:例如，相对路径“dir2”->“C:dir1dir2”。在“C:dir1”的情况下，
	 * 相对路径将应用于同一目录级别:相对路径“dir2”->“C:dir2”。
	 */
	public FileSystemResource(String path) {
		Assert.notNull(path, "Path must not be null");
		this.path = StringUtils.cleanPath(path);
		this.file = new File(path);
		this.filePath = this.file.toPath();
	}

	/**
	 * 从{@link File}句柄创建一个新的{@code FileSystemResource}。<p>注意:当通过{@link createRelative}构建相对资源时，
	 * 相对路径将适用于<i>在同一目录级别<i>:例如，new File("C:dir1")，相对路径"dir2" -> "C:dir2"!如果希望在给定的根目录下构建相对路径，
	 * 可以使用带有文件路径}的{@link FileSystemResource(String)构造函数在根路径后面追加一个斜杠:"C:dir1"，这表示该目录是所有相对路径的根目录。
	 */
	public FileSystemResource(File file) {
		Assert.notNull(file, "File must not be null");
		this.path = StringUtils.cleanPath(file.getPath());
		this.file = file;
		this.filePath = file.toPath();
	}

	/**
	 * 从{@link Path}句柄创建一个新的{@code FileSystemResource}，通过NIO.2而不是{@link file}执行所有文件系统交互。
	 * 与{@link PathResource}相比，该变体严格遵循一般的{@link FileSystemResource}约定，特别是在路径清理和{@link createRelative(String)}处理方面。
	 * <p>注意:当通过{@link createRelative}构建相对资源时，相对路径将应用于<i>在同一目录级别<i>:例如Paths.get("C:dir1")，
	 * 相对路径"dir2" -> "C:dir2"!如果希望在给定的根目录下构建相对路径，可以使用带有文件路径}的{@link FileSystemResource(String)构造函数在根路径后面追加一个斜杠:"C:dir1"，
	 * 这表示该目录是所有相对路径的根目录。或者，考虑使用{@link PathResourcePathResource(Path)}作为{@code java.nio.path。
	 * {@code createRelative}中的路径}解析，始终嵌套相对路径。@param filePath文件的路径句柄@see FileSystemResource(file) @since 5.1
	 */
	public FileSystemResource(Path filePath) {
		Assert.notNull(filePath, "Path must not be null");
		this.path = StringUtils.cleanPath(filePath.toString());
		this.file = null;
		this.filePath = filePath;
	}

	/**
	 * 从{@link FileSystem}句柄创建一个新的{@code FileSystemResource}，定位指定的路径。
	 * <p>这是{@link FileSystemResource(String)}的替代方案，通过NIO.2执行所有文件系统交互，而不是{@link file}。
	 */
	public FileSystemResource(FileSystem fileSystem, String path) {
		Assert.notNull(fileSystem, "FileSystem must not be null");
		Assert.notNull(path, "Path must not be null");
		this.path = StringUtils.cleanPath(path);
		this.file = null;
		this.filePath = fileSystem.getPath(this.path).normalize();
	}



	public final String getPath() {
		return this.path;
	}


	@Override
	public boolean exists() {
		return (this.file != null ? this.file.exists() : Files.exists(this.filePath));
	}


	@Override
	public boolean isReadable() {
		return (this.file != null ? this.file.canRead() && !this.file.isDirectory() :
				Files.isReadable(this.filePath) && !Files.isDirectory(this.filePath));
	}


	@Override
	public InputStream getInputStream() throws IOException {
		try {
			return Files.newInputStream(this.filePath);
		} catch (NoSuchFileException ex) {
			throw new FileNotFoundException(ex.getMessage());
		}
	}


	@Override
	public boolean isWritable() {
		return (this.file != null ? this.file.canWrite() && !this.file.isDirectory() :
				Files.isWritable(this.filePath) && !Files.isDirectory(this.filePath));
	}


	@Override
	public OutputStream getOutputStream() throws IOException {
		return Files.newOutputStream(this.filePath);
	}


	@Override
	public URL getURL() throws IOException {
		return (this.file != null ? this.file.toURI().toURL() : this.filePath.toUri().toURL());
	}


	@Override
	public URI getURI() throws IOException {
		return (this.file != null ? this.file.toURI() : this.filePath.toUri());
	}


	@Override
	public boolean isFile() {
		return true;
	}


	@Override
	public File getFile() {
		return (this.file != null ? this.file : this.filePath.toFile());
	}


	@Override
	public ReadableByteChannel readableChannel() throws IOException {
		try {
			return FileChannel.open(this.filePath, StandardOpenOption.READ);
		} catch (NoSuchFileException ex) {
			throw new FileNotFoundException(ex.getMessage());
		}
	}


	@Override
	public WritableByteChannel writableChannel() throws IOException {
		return FileChannel.open(this.filePath, StandardOpenOption.WRITE);
	}


	@Override
	public long contentLength() throws IOException {
		if (this.file != null) {
			long length = this.file.length();
			if (length == 0L && !this.file.exists()) {
				throw new FileNotFoundException(getDescription() +
						" cannot be resolved in the file system for checking its content length");
			}
			return length;
		} else {
			try {
				return Files.size(this.filePath);
			} catch (NoSuchFileException ex) {
				throw new FileNotFoundException(ex.getMessage());
			}
		}
	}


	@Override
	public long lastModified() throws IOException {
		if (this.file != null) {
			return super.lastModified();
		} else {
			try {
				return Files.getLastModifiedTime(this.filePath).toMillis();
			} catch (NoSuchFileException ex) {
				throw new FileNotFoundException(ex.getMessage());
			}
		}
	}


	@Override
	public Resource createRelative(String relativePath) {
		String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
		return (this.file != null ? new FileSystemResource(pathToUse) :
				new FileSystemResource(this.filePath.getFileSystem(), pathToUse));
	}


	@Override
	public String getFilename() {
		return (this.file != null ? this.file.getName() : this.filePath.getFileName().toString());
	}


	@Override
	public String getDescription() {
		return "file [" + (this.file != null ? this.file.getAbsolutePath() : this.filePath.toAbsolutePath()) + "]";
	}


	@Override
	public boolean equals(@Nullable Object other) {
		return (this == other || (other instanceof FileSystemResource &&
				this.path.equals(((FileSystemResource) other).path)));
	}


	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

}
