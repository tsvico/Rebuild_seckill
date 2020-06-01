package cn.peoplevip.common.Utils;

import cn.peoplevip.common.domain.MiaoshaUser;

public class UserContext {

	/**
	 * 存储拦截的User，在同一个线程里
	 */
	private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();

	public static void setUser(MiaoshaUser user) {
		userHolder.set(user);
	}

	public static MiaoshaUser getUser() {
		return userHolder.get();
	}
	public static void removeUser(){
		userHolder.remove();
	}

}
