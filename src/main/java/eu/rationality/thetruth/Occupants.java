package eu.rationality.thetruth;

import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.muc.UserStatusListener;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO UserStatusListener?
public class Occupants implements PresenceListener, ParticipantStatusListener {
    private MucBuffer buffer;
    private MultiUserChat chat;
    private Map<Jid, TruthOccupant> jid2nick = new HashMap<>();

    public Occupants(MucBuffer buffer, MultiUserChat chat) {
        this.buffer = buffer;
        this.chat = chat;
        List<EntityFullJid> occupants = chat.getOccupants();
        for (EntityFullJid occ : occupants) {
            TruthOccupant occupant = new TruthOccupant(buffer, occ, chat);
            jid2nick.put(occ, occupant);
        }
    }

    @Override
    public void processPresence(Presence presence) {
        // TODO implement
    }

    @Override
    public void joined(EntityFullJid participant) {
        TruthOccupant occupant = new TruthOccupant(buffer, participant, chat);
        jid2nick.put(participant, occupant);
    }

    @Override
    public void left(EntityFullJid participant) {
        jid2nick.get(participant).destroy();
        jid2nick.remove(participant);
    }

    @Override
    public void kicked(EntityFullJid participant, Jid actor, String reason) {
        jid2nick.get(participant).destroy();
        jid2nick.remove(participant);
    }

    @Override
    public void voiceGranted(EntityFullJid participant) {
        jid2nick.get(participant).updateUser();
    }

    @Override
    public void voiceRevoked(EntityFullJid participant) {
        jid2nick.get(participant).updateUser();
    }

    @Override
    public void banned(EntityFullJid participant, Jid actor, String reason) {
        jid2nick.get(participant).destroy();
        jid2nick.remove(participant);
    }

    @Override
    public void membershipGranted(EntityFullJid participant) {
        jid2nick.get(participant).updateUser();
    }

    @Override
    public void membershipRevoked(EntityFullJid participant) {
        jid2nick.get(participant).updateUser();
    }

    @Override
    public void moderatorGranted(EntityFullJid participant) {
        jid2nick.get(participant).updateUser();
    }

    @Override
    public void moderatorRevoked(EntityFullJid participant) {
        jid2nick.get(participant).updateUser();
    }

    @Override
    public void ownershipGranted(EntityFullJid participant) {
        jid2nick.get(participant).updateUser();
    }

    @Override
    public void ownershipRevoked(EntityFullJid participant) {
        jid2nick.get(participant).updateUser();
    }

    @Override
    public void adminGranted(EntityFullJid participant) {
        jid2nick.get(participant).updateUser();
    }

    @Override
    public void adminRevoked(EntityFullJid participant) {
        jid2nick.get(participant).updateUser();
    }

    @Override
    public void nicknameChanged(EntityFullJid participant, Resourcepart newNickname) {
        jid2nick.get(participant).updateUser();
    }

    public void remove() {

    }
}
