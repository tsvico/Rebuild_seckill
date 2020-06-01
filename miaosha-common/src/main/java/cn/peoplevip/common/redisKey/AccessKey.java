package cn.peoplevip.common.redisKey;

public class AccessKey extends BasePrefix {

	private AccessKey( int expireSeconds, String prefix) {
		// 不需要随机时间
		super(expireSeconds, prefix,"");
	}

	public static AccessKey withExpire(int expireSeconds) {
		return new AccessKey(expireSeconds, "access");
	}

}
