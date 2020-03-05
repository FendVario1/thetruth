package eu.rationality.thetruth;

import java.nio.Buffer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.util.JidUtil;
*/

class Weechat {
    public static String teststring = "";
    @SuppressWarnings("serial")
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

    //TODO static ConcurrentHashMap<EntityBareJid, Server> server = new ConcurrentHashMap<>();



    // Weechat is singlethreaded: therefore we need a mechanism to transform
    // asynchronous operations into callbacks triggered from the main loop
    // Callback management for native callbacks that need to be synchronized
    private static ConcurrentLinkedQueue<Supplier<Integer>> pendingOperations =
            new ConcurrentLinkedQueue<Supplier<Integer>>();
    public  static void register_pending_operation(Supplier<Integer> op) {
        pendingOperations.add(op);
        trigger_pending_operations();
    }
    public static int process_pending_operations() {
        Supplier<Integer> op;
        while((op = pendingOperations.poll()) != null) {
            op.get();
        }
        return WEECHAT_RC_OK;
    }
    static void trigger_pending_operations() {
        //TODO stub
    }


    // Write to buffer with native bufferid
    static void print(long bufferid, String str) {
       // BufferTest.tester+=bufferid + "/t"; //TODO stub
    }
    // Write to buffer with native bufferid with the specified weechat prefix
    static void print_prefix(long bufferid, String prefix, String str) {
        //TODO stub
    }
    // Write with date (in seconds since epoch) and tags
    static void print_date_tags(long bufferid, long date, String tags, String message) {
        //TODO stub
    }
    // Create a named buffer returning the native buffer id
    static long buffer_new(String name) {
        if (name.equals("0")) return 0;
        else return 1;
        //TODO stub
    }
    // Set a property for buffer bufferid
    static void buffer_set(long bufferid, String property, String value) {
        //TODO stub
    }
    // Callback for input received
    public static int buffer_input_callback(long bufferid, String data) {
        //TODO stub
        return 0;
    }
    // Callback for buffer close events
    public static int buffer_close_callback(long bufferid) {
        //TODO stub
        return 0;
    }

    // Nicklist related
    static long nicklist_add_nick(long bufferid, String nick, String color, String prefix) {
        //TODO stub
        return 0;
    }
    static void nicklist_remove_nick(long bufferid, long nickid) {
        //TODO stub
    }
    static void nicklist_remove_all(long bufferid) {
        //TODO stub
    }
    static void nicklist_nick_set(long bufferid, long nickid, String property, String value) {
        //TODO stub
    }

    public static void printerr(long bufferid, String str) {
        print_prefix(bufferid, "error", str);
    }

    public static void print_backtrace(Throwable t) {
        //TODO stub
    }

    public static void test(int a) {
        //TODO stub
    }

    public static void shutdown() {
        //TODO stub
    }

    public static int initUser(String jidConf, String pw) {
        //TODO stub
        return 0;
    }

    public static int initiateChat(String ownJid, String partnerJid) {
        //TODO stub
        return 0;
    }

    public static void loadLibrary(String soname) {
        System.out.println("this is the new Weechat.java, to be used for tests");


        try {
            System.load(soname);
        } catch (Exception e) {
            print(0, "Error while loading " + soname);
        }
        print(0, "Successfully registered " + soname);
    }

    public static int command_callback(long bufferid, String cmd, String[] args) {
        //TODO stub
        return 0;
    }


    static boolean config_boolean(long option) {
        //TODO stub
        return true;
    }
    static String config_color(long option){
        //TODO stub
        return "foo";
    }
    static void config_free(long config_file) {
        //TODO stub
    }
    static long config_get(String option_name) {
        //TODO stub
        return 0;
    }
    static int config_integer(long option) {
        //TODO stub
        return 0;
    }
    static long config_new(String name) {
        //TODO stub
        return 0;
    }

    public static int config_reload_callback(long config_file_id) {
        // TODO
        return WEECHAT_CONFIG_READ_OK;
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
