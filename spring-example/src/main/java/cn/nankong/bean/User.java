package cn.nankong.bean;

/**
 * @Description: user bean
 * @Author NanKong
 * @Date 2022/10/10 10:16
 */
public class User {

	private String name = "nankong";

	public User() {
		System.out.println("hello spring!");
	}

	public User(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
