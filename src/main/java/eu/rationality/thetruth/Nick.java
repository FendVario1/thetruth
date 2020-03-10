package eu.rationality.thetruth;

import org.jivesoftware.smack.packet.Presence;
import org.jxmpp.jid.BareJid;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Nick {
	private ServerBuffer buffer;
	private BareJid jid;
	private String name;
	private CopyOnWriteArrayList<BufferNick> buffers;
	String prefix;
	String prefixColor;

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private final String AVAILABLE = "o";// "🗨";
	private final String DND       = "n";// "⛔";
	private final String OFFLINE   = "x";// "❌";

	//!TODO check logging

	public Nick(ServerBuffer buffer, BareJid jid, String name, Presence presence) {
		buffers = new CopyOnWriteArrayList<>();
		this.buffer = buffer;
		this.jid = jid;
		this.name = name;
		Long nativeId = Weechat.getAPIInstance().nicklist_add_nick(buffer.getNativeId(), jid.toString(), "", "");
		BufferNick bufNick = new BufferNick(buffer, nativeId);
		buffers.add(bufNick);
		updatePresence(presence);
	}

	public void registerBuffer(Buffer buffer) {
		Long nativeId = Weechat.getAPIInstance().nicklist_add_nick(buffer.getNativeId(), jid.toString(), "", "");
		BufferNick bufNick = new BufferNick(buffer, nativeId);
		buffers.add(bufNick);
		updateBuffers();
	}

	public void deregisterBuffer(Buffer buffer) {
		buffers.removeIf((b) -> b.getBuf().nativeid == buffer.nativeid);
	}
	
	public void updateInfo(BareJid jid, String name) {
		this.jid  = jid;
		this.name = name;
		WeechatAPI api = Weechat.getAPIInstance();
		for (BufferNick bufferNick : buffers) {
			api.nicklist_remove_nick(bufferNick.getBuf().getNativeId(), bufferNick.getNatId());
			Long newId = api.nicklist_add_nick(bufferNick.getBuf().getNativeId(), jid.toString(), "", "");
			bufferNick.setNatId(newId);
		}
	}

	public void updatePresence(Presence presence) {
		if (presence.isAway()) {
			prefix = DND;
			prefixColor = "yellow";
			buffer.print_prefix("network", name + " (" + jid + ") is now away");
		} else if (presence.isAvailable()) {
			prefix = AVAILABLE;
			prefixColor = "green";
			buffer.print_prefix("join", name + " (" + jid + ") connected");
		} else {
			prefix = OFFLINE;
			prefixColor = "red";
			buffer.print_prefix("quit", name + " (" + jid + ") disconnected");
		}
		LOGGER.log(Level.INFO, "in updatePresence - new prefix: " + prefix + " user: " + jid);
		updateBuffers();
	}

	public void updateBuffers() {
		WeechatAPI api = Weechat.getAPIInstance();
		for (BufferNick bufferNick : buffers) {
			Weechat.getAPIInstance().nicklist_nick_set(bufferNick.getBuf().getNativeId(), bufferNick.getNatId(),
					"prefix", prefix + " ");
			api.nicklist_nick_set(bufferNick.getBuf().getNativeId(), bufferNick.getNatId(), "prefixcolor",
					prefixColor);
		}
	}
	
	public void destroy() {
		for (BufferNick bufferNick : buffers) {
			Weechat.getAPIInstance().nicklist_remove_nick(bufferNick.getBuf().getNativeId(), bufferNick.getNatId());
		}
		buffers.clear();
	}

	private static class BufferNick {
		private Buffer buf;
		private Long natId;
		public BufferNick(Buffer bu, Long id) {
			buf = bu;
			natId = id;
		}

		public Buffer getBuf() {
			return buf;
		}

		public Long getNatId() {
			return natId;
		}

		public void setBuf(Buffer buf) {
			this.buf = buf;
		}

		public void setNatId(Long natId) {
			this.natId = natId;
		}
	}
}
