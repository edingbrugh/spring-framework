package cn.nankong.service.impl;

import cn.nankong.bean.User;
import cn.nankong.service.DepartService;
import cn.nankong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: DepartServiceImpl
 * @Author NanKong
 * @Date 2022/10/10 11:09
 */
@Service
public class DepartServiceImpl implements DepartService {

	@Autowired
	private UserService userService;

	@Override
	public User getDepartInfo() {
		return new User("depart");
	}


	public User depart() {
		return userService.user();
	}
}
