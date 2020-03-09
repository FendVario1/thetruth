package eu.rationality.thetruth;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageWithBodiesFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MucBuffer extends Buffer  {
    private EntityBareJid chatJid;
    private Server localServer;
    private MultiUserChat chat;
    private MessageListener messageListener;
    private Occupants occupantsList;

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public MucBuffer (String jidStringTo, String servername, String nickname, String password,
                      Server localServer) throws Weechat.WeechatCallException, XmppStringprepException {
        super(servername);
        chatJid = JidCreate.from(jidStringTo).asEntityBareJidIfPossible();
        try {
            ServiceDiscoveryManager sm = ServiceDiscoveryManager.getInstanceFor(localServer.getCon());
            boolean ret = sm.supportsFeature(chatJid, "http://jabber.org/protocol/muc");
            if (!ret) {
                Weechat.getAPIInstance().print(localServer.getServerbuffer().getNativeId(), jidStringTo + " is not a valid MUC!");
            }
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException |
                SmackException.NotConnectedException | InterruptedException e) {
            LOGGER.log(Level.WARNING, "could not get supported Features of MUC");
            closeCallback();
            return;
        }


        this.localServer = localServer;


        MultiUserChatManager cm = MultiUserChatManager.getInstanceFor(localServer.getCon());
        chat = cm.getMultiUserChat(chatJid);
        // get user is already joined?
        Set<EntityBareJid> joinedMucs = cm.getJoinedRooms();
        if (!joinedMucs.contains(chatJid)) {
            try {
                Resourcepart name = Resourcepart.fromOrNull(nickname);
                if (password == null) {
                    chat.join(name);
                } else {
                    chat.join(name, password);
                }
            } catch (MultiUserChatException.NotAMucServiceException e) {
                Weechat.getAPIInstance().print(localServer.getServerbuffer().nativeid, jidStringTo + " is not a valid MUC!");
            } catch (XMPPException.XMPPErrorException | InterruptedException | SmackException.NoResponseException |
                    SmackException.NotConnectedException e) {
                LOGGER.log(Level.WARNING, "could not join MUC " + jidStringTo, e);
                closeCallback();
                return;
            }
        }
        messageListener = new MessageListener() {
            @Override
            public void processMessage(Message message) {
                if(!MessageWithBodiesFilter.INSTANCE.accept(message)) {
                    return;
                }
                String tll = message.getFrom().getResourceOrEmpty().toString();;
                if(tll.equals("")) {
                    tll= message.getFrom().asDomainFullJidIfPossible().toString();
                }
                final String username = tll;
                Weechat.getAPIInstance().register_pending_operation(() -> {
                    printMsgDateTags(System.currentTimeMillis() / 1000L,
                            username, message.getBody(), "notify_private,log1");
                    return Weechat.WEECHAT_RC_OK;
                });
            }
        };
        chat.addMessageListener(messageListener);
        Weechat.getAPIInstance().buffer_set(nativeid, "title", "Chatroom: " + chat.getRoom()); // TODO get other roomname?
        Weechat.getAPIInstance().buffer_set(nativeid, "nicklist", "1");
        Weechat.getAPIInstance().buffer_set(nativeid, "display", "auto");

        this.occupantsList = new Occupants(this, chat);
        chat.addParticipantListener(occupantsList);
        chat.addParticipantStatusListener(occupantsList);
    }

    @Override
    public int handleInput(String input) {
        XMPPTCPConnection con = localServer.getCon();
        try{
            MultiUserChat chat = MultiUserChatManager.getInstanceFor(con).getMultiUserChat(chatJid);
            chat.sendMessage(input);
            // printMsgDateTags(0, "me", input, "notify_msg,self_msg,log1");
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            LOGGER.log(Level.WARNING, "could not handle outbound MUC message", e);
            return Weechat.WEECHAT_RC_ERROR;
        }
        return Weechat.WEECHAT_RC_OK;
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
                localServer.getServerbuffer().receiveCommand(cmd, args);
                break;
        }
        return super.receiveCommand(cmd, args);
    }

    @Override
    public void closeCallback() {
        super.closeCallback();
        occupantsList.remove();
        localServer.removeFromMucBuffer(chatJid);
        chat.removeMessageListener(messageListener);
        chat.removeParticipantListener(occupantsList);
        chat.removeParticipantStatusListener(occupantsList);
        Weechat.getAPIInstance().buffer_close_callback(nativeid);
        try {
            chat.leave();
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            LOGGER.log(Level.WARNING, "could not leave chat " + chatJid, e);
        }
    }
}
