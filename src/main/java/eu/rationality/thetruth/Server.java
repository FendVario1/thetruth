package eu.rationality.thetruth;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.ReconnectionManager.ReconnectionPolicy;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.SubscribeListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jivesoftware.smackx.carbons.CarbonCopyReceivedListener;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension.Direction;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

public class Server {
	private XMPPTCPConnection con;

	private String domain;
	private String user;
	private String password;
	private Integer port;
	private String postfix;
	private ServerBuffer serverBuffer;
	private ConcurrentHashMap<EntityBareJid, ChatBuffer> chatBuffer = new ConcurrentHashMap<>();
	private ConcurrentHashMap<EntityBareJid, MucBuffer> mucBuffer = new ConcurrentHashMap<>();

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public Server(String domain, String user, String password, Integer port, String postfix) {
		super();
		this.domain = domain;
		this.user = user;
		this.password = password;
		this.port = port;
		this.postfix = postfix;
	}

	public String getPostfix() {
		return postfix;
	}

	public XMPPTCPConnection getCon() {
		return con;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public ServerBuffer getServerBuffer() {
		return serverBuffer;
	}

	public ConcurrentHashMap<EntityBareJid, ChatBuffer> getChatBuffer() {
		return chatBuffer;
	}

	public void removeChatBuffer(EntityBareJid id) {
		chatBuffer.remove(id);
	}
	public void removeFromMucBuffer(EntityBareJid id) {
		mucBuffer.remove(id);
	}

	public String getJID() {
		return user + "@" + domain;
	}

	private XMPPTCPConnectionConfiguration buildConfig() throws XmppStringprepException {
		var builder = XMPPTCPConnectionConfiguration.builder().setXmppDomain(domain).setUsernameAndPassword(user,
				password);
		if (port != null) {
			builder = builder.setPort(port);
		}
		return builder.build();
	}

	public void connect()
			throws Weechat.WeechatCallException, SmackException, IOException, XMPPException, InterruptedException {
		var conf = buildConfig();
		con = new XMPPTCPConnection(conf);
		Roster roster = Roster.getInstanceFor(con);
		if (this.serverBuffer == null) {
			this.serverBuffer = new ServerBuffer(this, roster);
		}
		// TODO make this configurable via weechat config?
		if(roster.getSubscriptionMode() != Roster.SubscriptionMode.manual)
			roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
		Weechat.getAPIInstance().print(0, "Created serverbuffer");
		serverBuffer.print("Connecting to server");
		con.addConnectionListener(new ConnectionListener() {
			@Override
			public void connectionClosedOnError(Exception e) {
				Weechat.getAPIInstance().register_pending_operation(() -> {
					serverBuffer.printErr("Connection closed on error(" + e.getClass() + "): " + e.getMessage());
					return Weechat.WEECHAT_RC_OK;
				});
			}

			@Override
			public void connectionClosed() {
				Weechat.getAPIInstance().register_pending_operation(() -> {
					serverBuffer.print("Connection to server closed");
					return Weechat.WEECHAT_RC_OK;
				});

			}

			@Override
			public void connected(XMPPConnection connection) {
				Weechat.getAPIInstance().register_pending_operation(() -> {
					serverBuffer.print("Connection to server " + connection.getHost() + " on port "
							+ connection.getPort() + " succeeded");
					return Weechat.WEECHAT_RC_OK;
				});

			}

			@Override
			public void authenticated(XMPPConnection connection, boolean resumed) {
				Weechat.getAPIInstance().register_pending_operation(() -> {
					serverBuffer.print("Authenticated as " + connection.getUser());
					return Weechat.WEECHAT_RC_OK;
				});
			}
		});
		ChatManager.getInstanceFor(con).addIncomingListener(new IncomingChatMessageListener() {

			@Override
			public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
				Weechat.getAPIInstance().register_pending_operation(() -> {
					Localpart lp = from.getLocalpartOrNull();
					if (lp == null) {
						serverBuffer.printMsgDateTags(System.currentTimeMillis() / 1000L, from.asEntityBareJidString(),
								message.getBody(), "notify_private,log1");
					}
					else { // is User / MUC chat??
						EntityBareJid fromJid = from.asEntityBareJid();
						boolean isChatOpened = isChatOpen(fromJid);
						ChatBuffer chatbuffer = getChat(fromJid);
						if(isChatOpened)
							chatbuffer.printMsgDateTags(System.currentTimeMillis() / 1000L, from.asEntityBareJidString(),
									message.getBody(), "notify_private,log1");
					}
					return Weechat.WEECHAT_RC_OK;
				});

			}
		});
		// Ping server every 60s
		PingManager.getInstanceFor(con).setPingInterval(60);
		// Stream management
		con.setUseStreamManagement(true);
		con.setUseStreamManagementResumption(true);
		// Automatic reconnection
		ReconnectionManager rm = ReconnectionManager.getInstanceFor(con);
		rm.setReconnectionPolicy(ReconnectionPolicy.RANDOM_INCREASING_DELAY);
		rm.enableAutomaticReconnection();
		rm.addReconnectionListener(new ReconnectionListener() {
			@Override
			public void reconnectionFailed(Exception e) {
				Weechat.getAPIInstance().register_pending_operation(() -> {
					serverBuffer.printErr("Failed reconnection attempt");
					return Weechat.WEECHAT_RC_OK;
				});
			}
			
			@Override
			public void reconnectingIn(int seconds) {
				Weechat.getAPIInstance().register_pending_operation(() -> {
					serverBuffer.printErr("Reconnection in " + seconds + " seconds");
					return Weechat.WEECHAT_RC_OK;
				});
			}
		});
		// Roster setup
		try {
			RosterListener rl = (RosterListener) WeechatDelayedExectorInvocationHandler.createProxy(serverBuffer.getNicklist(), new Class[] {RosterListener.class});
			// Invokes Nicklist.rosterEntries() once for the initial setup of nicklist synchronously
			// and invoke all subsequent roster updates asynchronously but guarded by the proxy above
			roster.getEntriesAndAddListener(rl, serverBuffer.getNicklist());
		} catch (Exception e) {
			Weechat.getAPIInstance().print(0, "Failed to add RoosterListener: " + e.toString());
			Weechat.getAPIInstance().print_backtrace(e);
		}
		roster.addSubscribeListener(serverBuffer.getNicklist());
		// Actually login
		con.connect().login();
		
		// From CarbonManager documentation:
		//   https://download.igniterealtime.org/smack/docs/latest/javadoc/org/jivesoftware/smackx/carbons/CarbonManager.html
		//   "You should call enableCarbons() before sending your first undirected presence (aka. the "initial presence").
		CarbonManager cm = CarbonManager.getInstanceFor(con);
		if (cm.isSupportedByServer()) {
			cm.enableCarbons();
			cm.addCarbonCopyReceivedListener(new CarbonCopyReceivedListener() {
				@Override
				public void onCarbonCopyReceived(Direction direction, Message carbonCopy, Message wrappingMessage) {
					Weechat.getAPIInstance().register_pending_operation(() -> {
						// TODO: I'am not sure why you use asEntityBareJidIfPossible() here, but if you just want
						// to strip the resourcepart, simply use asBareJid() (which does not produce null values)
						String from = carbonCopy.getFrom().asEntityBareJidIfPossible().toString();
						if (from == null) {
							from = carbonCopy.getFrom().toString();
						}
						serverBuffer.printSelfMessage(from, carbonCopy.getBody());
						return Weechat.WEECHAT_RC_OK;
					});
				}
			});
		}
		
		Presence presence = new Presence(Presence.Type.available);
		presence.setStatus("Started up and running");
		// Send the stanza (assume we have an XMPPConnection instance called "con").
		con.sendStanza(presence);

		BookmarkManager bm = BookmarkManager.getBookmarkManager(con);
		List<BookmarkedConference> bookmarks = bm.getBookmarkedConferences();
		for (BookmarkedConference b : bookmarks ) {
			if (!b.isAutoJoin())
				break;
			EntityBareJid a = b.getJid();
			Resourcepart e = b.getNickname();
			String c = "";
			if (e == null) {
				LOGGER.log(Level.WARNING, "Nickname could not be loaded from bookmark, could not connect to "
						+ a.asEntityBareJidString());
				break;
			} else {
				c = e.toString();
			}
			String d = b.getPassword();
			getMuc(a, c, d);
		}
	}
	
	public void send(EntityBareJid jid, String message) throws NotConnectedException, InterruptedException {
		Chat chat = ChatManager.getInstanceFor(con).chatWith(jid);
		chat.send(message);
	}

	public ChatBuffer getChat(EntityBareJid jid){
		ChatBuffer chatbuffer = chatBuffer.get(jid);
		if(chatbuffer == null) {
			chatbuffer = openChat(jid);
		}
		return chatbuffer;
	}

	private ChatBuffer openChat(EntityBareJid jid) {
		if(jid.getDomain().toString().equals(domain) && jid.getLocalpart().toString().equals(user)){
			Weechat.getAPIInstance().print(getServerBuffer().nativeId, "open a chat with your own Jid is not allowed");
			return null;
		}

		ChatBuffer b;
		try {
			b = new ChatBuffer(getJID(), jid.asEntityBareJidString(), this);
		} catch (Weechat.WeechatCallException | XmppStringprepException e) {
			LOGGER.log(Level.WARNING, "could not open chat " + jid.asEntityBareJidString() + " with user " +
					jid.asEntityBareJidString(), e);
			return null;
		}
		chatBuffer.put(jid, b);
		// add to nicklists
		return b;
	}

	private boolean isChatOpen(EntityBareJid jid) {
		ChatBuffer chatbuffer = chatBuffer.get(jid);
		return chatbuffer != null;
	}

	public void getMuc(EntityBareJid jid, String nickname, String password) {
		// discover room
		MucBuffer b = mucBuffer.get(jid);
		if(b == null) {
			openMuc(jid, nickname, password);
		}
	}

	private void openMuc(EntityBareJid jid, String nickname, String password) {
		MucBuffer b;
		String servername;
		MultiUserChatManager cm = MultiUserChatManager.getInstanceFor(con);
		try {
			// TODO if(password == null) {
			RoomInfo roomInfo = cm.getRoomInfo(jid);
			// get roomInfo works only on password unprotected Chats; TODO get info for password protected rooms & then update buffername
			b = new MucBuffer(jid.asEntityBareJidString(), roomInfo.getName(), nickname, password, this);
		} catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | NotConnectedException |
				InterruptedException | Weechat.WeechatCallException | XmppStringprepException e) {
				LOGGER.log(Level.WARNING, "could not open MUC " + jid.asEntityBareJidString(), e);
			return;
		}
		mucBuffer.put(jid, b);
	}

	public void disconnect() {
		// TODO: IIRC you don't have to check if the connection is connected.
		if (con.isConnected()) {
			con.disconnect();
		}
	}

	public int addBookmark(String name, EntityBareJid jid, boolean autojoin, String  nickname, String password, Long bufferId) {
		try {
			BookmarkManager bm = BookmarkManager.getBookmarkManager(getCon());
			Resourcepart nick = Resourcepart.from(nickname);
			bm.addBookmarkedConference(name, jid, autojoin, nick, password);
			getMuc(jid, nickname, password);
		} catch (SmackException.NoResponseException | SmackException.NotConnectedException |
				XmppStringprepException | XMPPException.XMPPErrorException | InterruptedException e) {
			LOGGER.log(Level.WARNING, "could not create bookmark", e);
			return Weechat.WEECHAT_RC_ERROR;
		}
		Weechat.getAPIInstance().print(bufferId, "bookmark saved.");
		return Weechat.WEECHAT_RC_OK;
	}
}
