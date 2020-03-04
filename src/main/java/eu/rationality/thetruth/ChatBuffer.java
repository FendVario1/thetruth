package eu.rationality.thetruth;

import eu.rationality.thetruth.Weechat.WeechatCallException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.regex.Pattern;

public class ChatBuffer extends Buffer {
	private String jidStringFrom, jidStringTo;
	private EntityBareJid jidTo;
	private Server server;

	public ChatBuffer(String jidStringFrom, String jidStringTo, Server server) throws WeechatCallException, XmppStringprepException {
		super(jidStringTo); // + " (" + jidStringFrom + ")"

		jidTo = JidCreate.from(jidStringTo).asEntityBareJidIfPossible();
		Weechat.buffer_set(nativeid, "title", "Chat: " + jidStringTo + " (From: " + jidStringFrom + ")");
		Weechat.buffer_set(nativeid, "nicklist", "1");
		Weechat.buffer_set(nativeid, "display", "auto");
		this.jidStringFrom = jidStringFrom;
		this.jidStringTo = jidStringTo;
		this.server = server;
	}

	@Override
	public int handleInput(String input) {
		XMPPTCPConnection con = server.getCon();
		try{
			Chat chat = ChatManager.getInstanceFor(con).chatWith(jidTo);
			chat.send(input);
			printMsgDateTags(0, "me", input, "notify_msg,self_msg,log1");
		} catch (Exception e) {
			// TODO Logging
			return Weechat.WEECHAT_RC_ERROR;
		}
		return Weechat.WEECHAT_RC_OK;
	}

	@Override
	public void printMsgDateTags(long time, String sender, String data, String tags) {
		String local = sender.split("@", 2)[0];
		Weechat.print_date_tags(nativeid, time, tags + ",nick_"+sender+",host_"+sender, local + "\t" + data);
	}

	@Override
	public int receiveCommand(String cmd, String[] args) {
		switch(cmd) {
			case "close":
				BufferManager bm = BufferManager.getinstance();
				bm.deregister(nativeid);
				break;
		}
		return super.receiveCommand(cmd, args);
	}

	@Override
	public void closeCallback() {
		super.closeCallback();
		server.removeChatBuffer(jidTo); // TODO close in smack??
		Weechat.buffer_close_callback(nativeid);
	}
}
