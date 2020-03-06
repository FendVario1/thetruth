package eu.rationality.thetruth;

import org.jivesoftware.smackx.muc.MUCAffiliation;
import org.jivesoftware.smackx.muc.MUCRole;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;
import org.jxmpp.jid.EntityFullJid;

public class TruthOccupant {
    private long nativeID;
    private MucBuffer buffer;
    private EntityFullJid jid;
    private Occupant occ;
    private MUCAffiliation affiliation;
    private MultiUserChat chat;
    private Boolean hasVoice; // TODO get voice from somewhere?...
    private MUCRole role;

    private final String NONE           = "x";
    private final String VISITOR        = "v";
    private final String PARTICIPANT    = "p";
    private final String MODERATOR      = "m";
    private final String OWNER          = "o";
    private final String ADMIN          = "a";

    public TruthOccupant(MucBuffer buffer, EntityFullJid jid, MultiUserChat chat) {
        this.buffer = buffer;
        this.jid = jid;
        this.chat = chat;

        updateUser();
    }

    public void updateUser() {
        occ = chat.getOccupant(jid);
        loadRole();
    }

    private void loadRole() {
        loadNickname();
        role = occ.getRole();
        affiliation = occ.getAffiliation();
        String prefix;
        String prefixcolor;

        if (role == MUCRole.moderator) {
            prefix = MODERATOR;
            if (affiliation == MUCAffiliation.admin) {
                prefix = ADMIN;
            } else if (affiliation == MUCAffiliation.owner) {
                prefix = OWNER;
            }
            prefixcolor = "green";
        } else if (role == MUCRole.participant) {
            prefix = PARTICIPANT;
            prefixcolor = "yellow";
        } else if (role == MUCRole.visitor) {
            prefix = VISITOR;
            prefixcolor = "red";
        } else {
            prefix = NONE;
            prefixcolor = "white";
        }

        Weechat.getAPIInstance().nicklist_nick_set(buffer.getNativeId(), nativeID, "prefix", prefix + " ");
        Weechat.getAPIInstance().nicklist_nick_set(buffer.getNativeId(), nativeID, "prefixcolor", prefixcolor);
    }

    private void loadNickname() {
        Weechat.getAPIInstance().nicklist_remove_nick(buffer.getNativeId(), nativeID);
        this.nativeID = Weechat.getAPIInstance().nicklist_add_nick(buffer.getNativeId(), jid.getResourceOrEmpty().toString(), "", "");
    }

    public void destroy() {
        Weechat.getAPIInstance().nicklist_remove_nick(buffer.getNativeId(), nativeID);
        nativeID = 0;
    }
}
