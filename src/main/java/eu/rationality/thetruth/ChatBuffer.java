package eu.rationality.thetruth;

import eu.rationality.thetruth.Weechat.WeechatCallException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.mam.element.MamPrefsIQ;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatBuffer extends Buffer {
	private String jidStringFrom, jidStringTo, fromNickname;
	private EntityBareJid jidTo;
	private Server server;

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public ChatBuffer(String jidStringFrom, String jidStringTo, Server server) throws WeechatCallException, XmppStringprepException {
		super(jidStringTo); // + " (" + jidStringFrom + ")"

		jidTo = JidCreate.from(jidStringTo).asEntityBareJidIfPossible();
		Weechat.getAPIInstance().buffer_set(nativeid, "title", "Chat: " + jidStringTo + " (From: " + jidStringFrom + ")");
		Weechat.getAPIInstance().buffer_set(nativeid, "nicklist", "1");
		Weechat.getAPIInstance().buffer_set(nativeid, "display", "auto");
		server.getServerbuffer().getNicklist().registerBuffer(this);

		this.jidStringFrom = jidStringFrom;
		this.jidStringTo = jidStringTo;
		fromNickname = jidStringFrom.split("@", 2)[0];
		this.server = server;

		MamManager mamMan = MamManager.getInstanceFor(server.getCon());
		try {
			if (mamMan.isSupported()) { // TODO set mam catchup enabled?
				MamManager.MamQueryArgs mamQueryArgs = MamManager.MamQueryArgs.builder().limitResultsToJid(jidTo).
						setResultPageSizeTo(20).queryLastPage().build();
				MamManager.MamQuery mamQuery = mamMan.queryArchive(mamQueryArgs);
				LinkedList<Forwarded> list = new LinkedList<>();
				boolean isComplete = false;
				int count = 1;
				do {
					MamManager.MamQueryPage a = mamQuery.getPage();
					list.addAll(a.getForwarded());
					if(!mamQuery.isComplete())
						mamQuery.pageNext(++count);
					else
						isComplete = true;
				} while (!isComplete);
				for (Forwarded forward: list) {
					Message mes = Forwarded.extractMessagesFrom(Collections.singletonList(forward)).get(0);
					String body = mes.getBody();
					DelayInformation a = forward.getDelayInformation();
					if(body != null)
						printMsgDateTags(a.getStamp().toInstant().getEpochSecond(), mes.getFrom().toString(), mes.getBody(), "");
				}
			}
		} catch (SmackException.NotConnectedException | SmackException.NoResponseException |
				XMPPException.XMPPErrorException | InterruptedException | SmackException.NotLoggedInException e) {
			LOGGER.log(Level.WARNING, "could not setup mam catchup");
		}
	}

	@Override
	public int handleInput(String input) {
		XMPPTCPConnection con = server.getCon();
		try{
			Chat chat = ChatManager.getInstanceFor(con).chatWith(jidTo);
			chat.send(input);
			printMsgDateTags(0, "me", input, "notify_msg,self_msg,log1");
		} catch (SmackException.NotConnectedException | InterruptedException e) {
			LOGGER.log(Level.WARNING, "could not handle outbound message", e);
			return Weechat.WEECHAT_RC_ERROR;
		}
		return Weechat.WEECHAT_RC_OK;
	}

	@Override
	public void printMsgDateTags(long time, String sender, String data, String tags) {
		String local = sender.split("@", 2)[0];
		if (local.equals(fromNickname))
			local = "me";
		Weechat.getAPIInstance().print_date_tags(nativeid, time, tags + ",nick_"+sender+",host_"+sender, local + "\t" + data);
	}

	@Override
	public int receiveCommand(String cmd, String[] args) {
		switch(cmd) {
			case "close":
				BufferManager bm = BufferManager.getinstance();
				bm.deregister(nativeid);
				break;
			case "query":
			case "join":
				server.getServerbuffer().receiveCommand(cmd, args);
				break;
		}
		return super.receiveCommand(cmd, args);
	}

	@Override
	public void closeCallback() {
		super.closeCallback();
		server.getServerbuffer().getNicklist().deregisterBuffer(this);
		server.removeChatBuffer(jidTo); // TODO close in smack??
		Weechat.getAPIInstance().buffer_close_callback(nativeid);
	}
}
