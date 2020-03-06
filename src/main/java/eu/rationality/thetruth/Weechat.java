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

class Weechat {
    static class WeechatCallException extends Exception {
    };

    /* return codes for plugin functions */
    static final int WEECHAT_RC_OK     =  0;
    static final int WEECHAT_RC_OK_EAT =  1;
    static final int WEECHAT_RC_ERROR  = -1;

    /* return codes for config read functions/callbacks */
    static final int WEECHAT_CONFIG_READ_OK              =  0;
    static final int WEECHAT_CONFIG_READ_MEMORY_ERROR    = -1;
    static final int WEECHAT_CONFIG_READ_FILE_NOT_FOUND  = -2;

    /* return codes for config write functions/callbacks */
    static final int WEECHAT_CONFIG_WRITE_OK             =  0;
    static final int WEECHAT_CONFIG_WRITE_ERROR          = -1;
    static final int WEECHAT_CONFIG_WRITE_MEMORY_ERROR   = -2;

    /* null value for option */
    static final String WEECHAT_CONFIG_OPTION_NULL       = "null";

    /* return codes for config option set */
    static final int WEECHAT_CONFIG_OPTION_SET_OK_CHANGED       =  2;
    static final int WEECHAT_CONFIG_OPTION_SET_OK_SAME_VALUE    =  1;
    static final int WEECHAT_CONFIG_OPTION_SET_ERROR            =  0;
    static final int WEECHAT_CONFIG_OPTION_SET_OPTION_NOT_FOUND = -1;

    /* return codes for config option unset */
    static final int WEECHAT_CONFIG_OPTION_UNSET_OK_NO_RESET    =  0;
    static final int WEECHAT_CONFIG_OPTION_UNSET_OK_RESET       =  1;
    static final int WEECHAT_CONFIG_OPTION_UNSET_OK_REMOVED     =  2;
    static final int WEECHAT_CONFIG_OPTION_UNSET_ERROR          = -1;

    static ConcurrentHashMap<EntityBareJid, Server> server = new ConcurrentHashMap<>();
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);



    // Weechat is singlethreaded: therefore we need a mechanism to transform
    // asynchronous operations into callbacks triggered from the main loop
    // Callback management for native callbacks that need to be synchronized
    private static ConcurrentLinkedQueue<Supplier<Integer>> pendingOperations =
            new ConcurrentLinkedQueue<Supplier<Integer>>();

    private static WeechatAPI instance = null;
    static synchronized WeechatAPI getAPIInstance() {
        if (instance == null) {
            instance = new WeechatRegular();
        }
        return instance;
    }

    // ONLY FOR TESTS!
    static void setAPIInstance(WeechatAPI instance) {
        Weechat.instance = instance;

    }

    static void register_pending_operation(Supplier<Integer> op) {
        getAPIInstance().register_pending_operation(op);
    }

    static int process_pending_operations() {
        return getAPIInstance().process_pending_operations();
    }

    static void trigger_pending_operations() {
        getAPIInstance().trigger_pending_operations();
    }

    // Write to buffer with native bufferid
    static void print(long bufferid, String str) {
        getAPIInstance().print(bufferid, str);
    }

    // Write to buffer with native bufferid with the specified weechat prefix
    static void print_prefix(long bufferid, String prefix, String str) {
        getAPIInstance().print_prefix(bufferid, prefix, str);
    }

    // Write with date (in seconds since epoch) and tags
    static void print_date_tags(long bufferid, long date, String tags, String message) {
        getAPIInstance().print_date_tags(bufferid, date, tags, message);
    }

    // Create a named buffer returning the native buffer id
    static long buffer_new(String name) {
        return getAPIInstance().buffer_new(name);
    }

    // Set a property for buffer bufferid
    static void buffer_set(long bufferid, String property, String value) {
        getAPIInstance().buffer_set(bufferid, property, value);
    }

    // Callback for input received
    static int buffer_input_callback(long bufferid, String data) {
        return getAPIInstance().buffer_input_callback(bufferid, data);
    }

    // Callback for buffer close events
    static int buffer_close_callback(long bufferid) {
        return getAPIInstance().buffer_close_callback(bufferid);
    }

    // Nicklist related
    static long nicklist_add_nick(long bufferid, String nick, String color, String prefix) {
        return getAPIInstance().nicklist_add_nick(bufferid, nick, color, prefix);
    }

    static void nicklist_remove_nick(long bufferid, long nickid) {
        getAPIInstance().nicklist_remove_nick(bufferid, nickid);
    }

    static void nicklist_remove_all(long bufferid) {
        getAPIInstance().nicklist_remove_all(bufferid);
    }

    static void nicklist_nick_set(long bufferid, long nickid, String property, String value) {
        getAPIInstance().nicklist_nick_set(bufferid, nickid, property, value);
    }

    static void printerr(long bufferid, String str) {
        getAPIInstance().printerr(bufferid, str);
    }

    static void print_backtrace(Throwable t) {
        getAPIInstance().print_backtrace(t);
    }

    static void shutdown() {
        getAPIInstance().shutdown();
    }

    static int initUser(String jidConf, String pw) {
        return getAPIInstance().initUser(jidConf, pw);
    }

    static int initiateChat(String ownJid, String partnerJid) {
        return getAPIInstance().initiateChat(ownJid, partnerJid);
    }

    static void loadLibrary(String soname) {
        getAPIInstance().loadLibrary(soname);
    }

    static int command_callback(long bufferid, String cmd, String[] args) {
        return getAPIInstance().command_callback(bufferid, cmd, args);
    }

    // Longs are native ids
    static boolean config_boolean(long option) {
        return getAPIInstance().config_boolean(option);
    }

    static String config_color(long option) {
        return getAPIInstance().config_color(option);
    }

    static void config_free(long config_file) {
        getAPIInstance().config_free(config_file);
    }

    static long config_get(String option_name) {
        return getAPIInstance().config_get(option_name);
    }

    static int config_integer(long option) {
        return getAPIInstance().config_integer(option);
    }

    static long config_new(String name) {
        return getAPIInstance().config_new(name);
    }

    static int config_reload_callback(long config_file_id) {
        return getAPIInstance().config_reload_callback(config_file_id);
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
