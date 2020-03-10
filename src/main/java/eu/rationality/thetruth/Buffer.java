package eu.rationality.thetruth;

import eu.rationality.thetruth.Weechat.WeechatCallException;

public abstract class Buffer {
	enum Tags {
		MUC,
		PRIVMSG,
		SELFMSG,
		ACTION,
	};
	
	protected long nativeId;
	private String name;

	public Long getNativeId() {
		return nativeId;
	}

	public void print(String str) {
		Weechat.getAPIInstance().print(getNativeId(), str);
	}
	
	public void printErr(String str) {
		Weechat.getAPIInstance().print_prefix(getNativeId(), "error", str);
	}
	
	public void printPrefix(String prefix, String msg) {
		Weechat.getAPIInstance().print_prefix(getNativeId(), prefix, msg);
	}
	
	public void printMsgDateTags(long time, String sender, String data, String tags) {
		Weechat.getAPIInstance().print_date_tags(nativeId, time, tags + ",nick_"+sender+",host_"+sender,
				sender + "\t" + data);
	}
	
	public void sendMsg() {
		
	}
	
	public int receiveCommand(String cmd, String[] args) {
		return Weechat.WEECHAT_RC_OK;
	}
	
	public int handleInput(String input) {
		print("echo: " + input);
		return Weechat.WEECHAT_RC_OK;
	}
	
	public Buffer(String name) throws WeechatCallException {
		this.name = name;
		this.nativeId = createNativeBuffer(name);
		BufferManager.getInstance().register(this);
	}
	
	static long createNativeBuffer(String name) throws WeechatCallException {
		long nativeId = Weechat.getAPIInstance().buffer_new(name);
		if (nativeId == 0) {
			throw new Weechat.WeechatCallException();
		}
		return nativeId;
	}

	public void closeCallback() {
	}
}
