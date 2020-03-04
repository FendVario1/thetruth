package eu.rationality.thetruth;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.Set;

public class MucBuffer extends Buffer  {
    private EntityBareJid chatJid;
    private Server localServer;
    private MultiUserChat chat;
    private MessageListener messageListener;

    public MucBuffer (String jidStringTo, String servername, String nickname, String password,
                      Server localServer) throws Weechat.WeechatCallException, XmppStringprepException {
        super(servername);
        chatJid = JidCreate.from(jidStringTo).asEntityBareJidIfPossible();
        try {
            ServiceDiscoveryManager sm = ServiceDiscoveryManager.getInstanceFor(localServer.getCon());
            boolean ret = sm.supportsFeature(chatJid, "http://jabber.org/protocol/muc");
            if (!ret) {
                Weechat.print(localServer.getServerbuffer().getNativeId(), jidStringTo + " is not a valid MUC!");
            }
        } catch (Exception e) {
            // TODO logging
            closeCallback();
            return;
        }


        this.localServer = localServer;


        MultiUserChatManager cm = MultiUserChatManager.getInstanceFor(localServer.getCon());
        chat = cm.getMultiUserChat(chatJid);
        // TODO get user is already joined?
        Set<EntityBareJid> joinedMucs = cm.getJoinedRooms();
        if (!joinedMucs.contains(chatJid)) { // TODO does this work?
            try {
                Resourcepart name = Resourcepart.fromOrNull(nickname);
                if (password == null) {
                    chat.join(name);
                } else {
                    chat.join(name, password);
                }
            } catch (MultiUserChatException.NotAMucServiceException e) {
                Weechat.print(localServer.getServerbuffer().nativeid, jidStringTo + " is not a valid MUC!");
            } catch (Exception e) {
                // TODO logging
                closeCallback();
                return;
            }
        }
        messageListener = new MessageListener() {
            @Override
            public void processMessage(Message message) {
                // remove "null" messages:
                // if (message.getBody().equals("null")) return;

                // TODO filter own messages!
                Weechat.register_pending_operation(() -> {
                    printMsgDateTags(System.currentTimeMillis() / 1000L,
                            message.getFrom().asEntityBareJidIfPossible().asEntityBareJidString(),
                            message.getBody(), "notify_private,log1");
                    return Weechat.WEECHAT_RC_OK;
                });
            }
        };
        chat.addMessageListener(messageListener);
        // TODO member listener
        Weechat.buffer_set(nativeid, "title", "Chatroom: " + chat.getRoom()); // TODO get other roomname?
        Weechat.buffer_set(nativeid, "nicklist", "1");
        Weechat.buffer_set(nativeid, "display", "auto");
    }

    @Override
    public int handleInput(String input) {
        XMPPTCPConnection con = localServer.getCon();
        try{
            MultiUserChat chat = MultiUserChatManager.getInstanceFor(con).getMultiUserChat(chatJid);
            chat.sendMessage(input);
            printMsgDateTags(0, "me", input, "notify_msg,self_msg,log1");
        } catch (Exception e) {
            // TODO Logging
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
        }
        return super.receiveCommand(cmd, args);
    }

    @Override
    public void closeCallback() {
        super.closeCallback();
        localServer.removeFromMucBuffer(chatJid);
        chat.removeMessageListener(messageListener);
        Weechat.buffer_close_callback(nativeid);
        try {
            chat.leave();
        } catch (Exception e) {
            // TODO logging
        }
    }
}
