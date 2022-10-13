package cn.nankong.annotation;

import cn.nankong.bean.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: annotationApplication 配置累
 * @Author NanKong
 * @Date 2022/10/10 17:15
 */

@Configuration
public class AnnotationContextConfig {

	@Bean(name = "user")
	public User user() {
		return new User("user1");
	}

	@Bean(name = "user2")
	public User user2() {
		return new User("user2");
	}

	@Bean(name = "user3")
	public User user3() {
		return new User("user3");
	}
}
