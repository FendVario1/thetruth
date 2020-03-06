package eu.rationality.thetruth;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.util.JidUtil;


class WeechatRegular implements WeechatAPI {

	static ConcurrentHashMap<EntityBareJid, Server> server = new ConcurrentHashMap<>();
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


	
	// Weechat is singlethreaded: therefore we need a mechanism to transform
	// asynchronous operations into callbacks triggered from the main loop
	// Callback management for native callbacks that need to be synchronized
	private static ConcurrentLinkedQueue<Supplier<Integer>> pendingOperations =
			new ConcurrentLinkedQueue<Supplier<Integer>>();
	public  void register_pending_operation(Supplier<Integer> op) {
		pendingOperations.add(op);
		Weechat.trigger_pending_operations();
	}
	public int process_pending_operations() {
		Supplier<Integer> op;
		while((op = pendingOperations.poll()) != null) {
			op.get();
		}
		return Weechat.WEECHAT_RC_OK;
	}

	native public void trigger_pending_operations();
	

	// Write to buffer with native bufferid
	native public void print(long bufferid, String str);
	// Write to buffer with native bufferid with the specified weechat prefix
	native public void print_prefix(long bufferid, String prefix, String str);
	// Write with date (in seconds since epoch) and tags
	native public void print_date_tags(long bufferid, long date, String tags, String message);
	// Create a named buffer returning the native buffer id
	native public long buffer_new(String name);
	// Set a property for buffer bufferid
	native public void buffer_set(long bufferid, String property, String value);
	// Callback for input received
	public int buffer_input_callback(long bufferid, String data) {
		Buffer b = BufferManager.getinstance().byid(bufferid);
		if (b == null) {
			printerr(0, "Input callback received for buffer " + Long.toHexString(bufferid) +
					" which is not managed by the plugin");
			return Weechat.WEECHAT_RC_ERROR;
		}
		return b.handleInput(data);
	}
	// Callback for buffer close events
	public int buffer_close_callback(long bufferid) {
		BufferManager.getinstance().deregister(bufferid);
		return Weechat.WEECHAT_RC_OK;
	}

	// Nicklist related
	native public long nicklist_add_nick(long bufferid, String nick, String color, String prefix);
	native public void nicklist_remove_nick(long bufferid, long nickid);
	native public void nicklist_remove_all(long bufferid);
	native public void nicklist_nick_set(long bufferid, long nickid, String property, String value);

	public void printerr(long bufferid, String str) {
		print_prefix(bufferid, "error", str);
	}
	
	public void print_backtrace(Throwable t) {
		var frames = t.getStackTrace();
		printerr(0, "Backtrace:\n");
		for (var f : frames) {
			printerr(0,  "   " + f.toString());
		}
	}

	public void shutdown() {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for(Thread t : threadSet) {
			if(t.equals(Thread.currentThread()))
				continue;
			t.interrupt();
		}
	}

	public int initUser(String jidConf, String pw) {
		LOGGER.info("Java initUser");
		SmackConfiguration.DEBUG = true;
		try {
			EntityBareJid jid;
			final String user, domain;
			if (jidConf != null && JidUtil.isTypicalValidEntityBareJid(jidConf)) {
				jid = JidCreate.entityBareFrom(jidConf);
				user = jid.getLocalpart().toString();
				domain = jid.getDomain().toString();
			} else {
				LOGGER.warning("Invalid JID specified from configuration");
				return Weechat.WEECHAT_RC_ERROR;
			}
			Server s = new Server(domain, user, pw, null);

			server.put(jid, s);

			s.connect();
		} catch (IOException | SmackException | XMPPException | InterruptedException e) {
			LOGGER.log(Level.SEVERE, "Java Init failed", e);
			return Weechat.WEECHAT_RC_ERROR;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Unexpected Error in Java Init", e);
		}
		return Weechat.WEECHAT_RC_OK;
	}

	public int initiateChat(String ownJid, String partnerJid) {
		LOGGER.info("Java initiated");
		try {
			EntityBareJid oJid, pJid;
			if (ownJid != null && JidUtil.isTypicalValidEntityBareJid(ownJid)) {
				oJid = JidCreate.entityBareFrom(ownJid);
			} else {
				LOGGER.warning("Invalid ownJID specified");
				return -1;
			}
			if (partnerJid != null && JidUtil.isTypicalValidEntityBareJid(partnerJid)) {
				pJid = JidCreate.entityBareFrom(partnerJid);
			} else {
				LOGGER.warning("Invalid partnerJID specified");
				return -1;
			}
			server.get(oJid).getChat(pJid);
		} catch (Exception e) {
			printerr(0, "Java initChat failed");
			return Weechat.WEECHAT_RC_ERROR;
		}
		return Weechat.WEECHAT_RC_OK;
	}

	public void loadLibrary(String soname) {
		// First called function - setup Logger
		try{
			TruthLogger.setup();
		} catch (IOException e) {
			print(0, "Could not create log files");
		}
		try {
			System.load(soname);
		} catch (Exception e) {
			LOGGER.severe("Error while loading " + soname);
		}
		LOGGER.info("Successfully registered " + soname);
		// register logging handler as early as possible
		TruthLogger.initWeechatLogging(buffer_new("TheTruth Log"));
	}

	public int command_callback(long bufferid, String cmd, String[] args) {
		Buffer b = BufferManager.getinstance().byid(bufferid);
		if (b == null) {
			// For hooked commands: legit behaviour: hooked command invoked on other (e.g. irc) buffer
			return Weechat.WEECHAT_RC_OK;
		}
		return b.receiveCommand(cmd, args);
	}
	
	// Longs are native ids
	native public boolean config_boolean(long option);
	native public String config_color(long option);
	native public void config_free(long config_file);
	native public long config_get(String option_name);
	native public int config_integer(long option);
	native public long config_new(String name);
	public int config_reload_callback(long config_file_id) {
		// TODO
		return Weechat.WEECHAT_CONFIG_READ_OK;
	}
	/*
	 * weechat.config_new
	 * weechat.config_new_option
	 * weechat.config_new_section
	 * weechat.config_option_free
	 * weechat.config_option_set
	 * weechat.config_read
	 * weechat.config_reload
	 * weechat.config_search_option
	 * weechat.config_string
	 * weechat.config_write
	 * weechat.config_write_line
	 * weechat.config_write_option
	 */
}
