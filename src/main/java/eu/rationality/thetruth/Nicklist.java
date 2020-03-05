package eu.rationality.thetruth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntries;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;

public class Nicklist implements RosterEntries, RosterListener, PresenceListener {
	private ServerBuffer buffer;
	private Roster roster;
	private Map<BareJid, Nick> jid2nick = new HashMap<>();

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public Nicklist(ServerBuffer buffer, Roster roster) {
		this.buffer = buffer;
		this.roster = roster;
	}
	
	private Presence getPresence(BareJid jid) {
		return roster.getPresence(jid);
	}
	
	// Initialisation callback: gets the initial roster contents
	@Override
	public void rosterEntries(Collection<RosterEntry> rosterEntries) {
		for (RosterEntry e : rosterEntries) {
			LOGGER.log(Level.INFO, "adding entry for: " + e.getJid());
			Presence p = getPresence(e.getJid());
			Nick nick = new Nick(buffer, e.getJid(), e.getName(), p);
			jid2nick.put(e.getJid(), nick);
		}
		
	}


	@Override
	public void entriesAdded(Collection<Jid> addresses) {
		for (Jid j : addresses) {
			BareJid bare = j.asBareJid();
			if (jid2nick.containsKey(bare)) {
				// Debugging aid
				LOGGER.log(Level.WARNING, "entry " + bare + " added, but already present in nicklist");
			}
			RosterEntry e = roster.getEntry(j.asBareJid());
			Presence    p = getPresence(bare);
			Nick nick = new Nick(buffer, bare, e.getName(), p);
			jid2nick.put(bare, nick);
		}
		
	}

	@Override
	public void entriesUpdated(Collection<Jid> addresses) {
		for (Jid j : addresses) {
			BareJid bare = j.asBareJid();
			Nick nick = jid2nick.get(bare);
			if (nick == null) {
				LOGGER.log(Level.WARNING, "entry " + bare + " updated, but not present in nicklist");
				continue;
			}
			RosterEntry e = roster.getEntry(bare);
			Presence    p = getPresence(bare);
			nick.updateInfo(bare, e.getName()); // TODO e.getName() returns null
			nick.updatePresence(p);
		}
		
	}

	@Override
	public void entriesDeleted(Collection<Jid> addresses) {
		for (Jid j : addresses) {
			BareJid bare = j.asBareJid();
			Nick nick = jid2nick.get(bare);
			if (nick == null) {
				LOGGER.log(Level.WARNING, "entry " + bare + " deleted, but not present in nicklist");
				continue;
			}
			nick.destroy();
			jid2nick.remove(bare);
		}
		
	}

	@Override
	public void presenceChanged(Presence presence) {
		BareJid bare = presence.getFrom().asBareJid();
		Nick nick = jid2nick.get(bare);
		if (nick == null) {
			LOGGER.log(Level.WARNING, "presence update for " + bare + " received, but not present in nicklist");
				return;
		}
		nick.updatePresence(getPresence(bare));
	}

	@Override
	public void processPresence(Presence presence) {
		presenceChanged(presence);
	}

}
