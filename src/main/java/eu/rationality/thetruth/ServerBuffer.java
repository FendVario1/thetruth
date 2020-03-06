package eu.rationality.thetruth;

import java.util.regex.Pattern;

import org.jivesoftware.smack.roster.Roster;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import eu.rationality.thetruth.Weechat.WeechatCallException;

public class ServerBuffer extends Buffer {
	private Server server;
	private Nicklist nicklist;
	
	public ServerBuffer(Server server, Roster roster) throws WeechatCallException {
		super(server.getJID());
		Weechat.getAPIInstance().buffer_set(nativeid, "title", "Account: " + server.getJID());
		Weechat.getAPIInstance().buffer_set(nativeid, "nicklist", "1");
		Weechat.getAPIInstance().buffer_set(nativeid, "display", "auto");
		this.server = server;
		this.nicklist = new Nicklist(this, roster);
	}
	
	public Nicklist getNicklist() {
		return nicklist;
	}
	
	public void printSelfMessage(String receiver, String msg) {
		server.getServerbuffer().printMsgDateTags(0, "me", receiver + ": " + msg, "notify_msg,self_msg,log1");
	}
	
	@Override
	public int handleInput(String input) {
		// TODO keep serverbuffer chat functionality?
		// probably remove, as it can not support MUC chats
		String[] split = Pattern.compile(":\\s+").split(input, 2);
		if (split.length != 2) {
			printErr("Failed to determine receiver for message: " + input);
			return Weechat.WEECHAT_RC_ERROR;
		}
		try {
			EntityBareJid jid = JidCreate.entityBareFrom(split[0]);
			ChatBuffer b = server.getChat(jid);
			Weechat.getAPIInstance().buffer_set(b.getNativeId(), "display", "auto");
			b.handleInput(split[1]);
		} catch (XmppStringprepException e) {
			printErr(split[0] + " does not constitute a valid jid");
			return Weechat.WEECHAT_RC_ERROR;
		}
		return Weechat.WEECHAT_RC_OK;
	}
	
	@Override
	public int receiveCommand(String cmd, String[] args) {
		switch(cmd) {
		case "query":
			if (args.length != 2) {
				printErr("Query expects one parameter: /query <jid>");
				return Weechat.WEECHAT_RC_ERROR;
			}
			try {
				EntityBareJid jid = JidCreate.entityBareFrom(args[1]);
				server.getChat(jid);
			} catch (XmppStringprepException e) {
				printErr(args[1] + " does not constitute a valid jid");
				return Weechat.WEECHAT_RC_ERROR;
			}
			break;
		case "join":
			if (args.length < 3 || args.length > 4) {
				printErr("Join expects three parameters: /join <jid> <nickname> [password]");
				return Weechat.WEECHAT_RC_ERROR;
			}
			try {
				String pass = null;
				if (args.length == 4) {
					pass = args[3];
				}
				EntityBareJid jid = JidCreate.entityBareFrom(args[1]);
				server.getMuc(jid, args[2], pass);
			} catch (XmppStringprepException e) {
				printErr(args[1] + " does not constitute a valid jid");
				return Weechat.WEECHAT_RC_ERROR;
			}
			break;
		}
		return super.receiveCommand(cmd, args);
	}

	@Override
	public void closeCallback() {
		super.closeCallback();
		server.disconnect();
	}
}
