package cn.nankong.service.impl;

import cn.nankong.bean.User;
import cn.nankong.service.DepartService;
import cn.nankong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @Description: UserServiceImpl
 * @Author NanKong
 * @Date 2022/10/10 11:09
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private DepartService departService;

	@Override
	public User getUser() {
		return departService.getDepartInfo();
	}


	public User user() {
		return new User("user");
	}
}
