package cn.nankong.mainEntry;

import cn.nankong.bean.User;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * @Description: factoryBean 测试
 * @Author NanKong
 * @Date 2022/10/18 10:11
 */
@Component
public class FactoryBeanTest implements FactoryBean<User> {


	@Override
	public User getObject() throws Exception {
		return new User();
	}

	@Override
	public Class<?> getObjectType() {
		return User.class;
	}

	@Override
	public boolean isSingleton() {
		return FactoryBean.super.isSingleton();
	}


	public static void main(String[] args) {

	}
}
