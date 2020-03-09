package eu.rationality.thetruth;

import org.jivesoftware.smack.packet.Presence;
import org.jxmpp.jid.BareJid;

public class Nick {
	private long nativeID;
	private Buffer buffer;
	private BareJid jid;
	private String name;
	
	private final String AVAILABLE = "o";// "🗨";
	private final String DND       = "n";// "⛔";
	private final String OFFLINE   = "x";// "❌";
	
	
	public Nick(Buffer buffer, BareJid jid, String name, Presence presence) {
		this.buffer = buffer;
		this.jid = jid;
		this.name = name;
		this.nativeID = Weechat.getAPIInstance().nicklist_add_nick(buffer.getNativeId(), jid.toString(), "", "");
		updatePresence(presence);
	}
	
	public void updateInfo(BareJid jid, String name) {
		this.jid  = jid;
		this.name = name;
		Weechat.getAPIInstance().nicklist_remove_nick(buffer.getNativeId(), nativeID);
		this.nativeID = Weechat.getAPIInstance().nicklist_add_nick(buffer.getNativeId(), jid.toString(), "", "");
	}

	public void updatePresence(Presence presence) {
		String prefix;
		String prefixcolor;
		if (presence.isAway()) {
			prefix = DND;
			prefixcolor = "yellow";
			buffer.print_prefix("network", name + " (" + jid + ") is now away");
		} else if (presence.isAvailable()) {
			prefix = AVAILABLE;
			prefixcolor = "green";
			buffer.print_prefix("join", name + " (" + jid + ") connected");
		} else {
			prefix = OFFLINE;
			prefixcolor = "red";
			buffer.print_prefix("quit", name + " (" + jid + ") disconnected");
		}
		
		Weechat.getAPIInstance().nicklist_nick_set(buffer.getNativeId(), nativeID, "prefix", prefix + " ");
		Weechat.getAPIInstance().nicklist_nick_set(buffer.getNativeId(), nativeID, "prefixcolor", prefixcolor);
	}
	
	public void destroy() {
		Weechat.getAPIInstance().nicklist_remove_nick(buffer.getNativeId(), nativeID);
		nativeID = 0;
	}
}
