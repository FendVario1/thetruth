package eu.rationality.thetruth;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.util.JidUtil;

import java.io.IOException;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;

public interface WeechatAPI {

    void register_pending_operation(Supplier<Integer> op);

    int process_pending_operations();

    void trigger_pending_operations();

    // Write to buffer with native bufferid
    void print(long bufferid, String str);

    // Write to buffer with native bufferid with the specified weechat prefix
    void print_prefix(long bufferid, String prefix, String str);

    // Write with date (in seconds since epoch) and tags
    void print_date_tags(long bufferid, long date, String tags, String message);

    // Create a named buffer returning the native buffer id
    long buffer_new(String name);

    // Set a property for buffer bufferid
    void buffer_set(long bufferid, String property, String value);

    // Callback for input received
    int buffer_input_callback(long bufferid, String data);

    // Callback for buffer close events
    int buffer_close_callback(long bufferid);

    // Nicklist related
    long nicklist_add_nick(long bufferid, String nick, String color, String prefix);

    void nicklist_remove_nick(long bufferid, long nickid);

    void nicklist_remove_all(long bufferid);

    void nicklist_nick_set(long bufferid, long nickid, String property, String value);

    void printerr(long bufferid, String str);

    void print_backtrace(Throwable t);

    void shutdown();

    int initUser(String jidConf, String pw);

    int initiateChat(String ownJid, String partnerJid);

    void loadLibrary(String soname);

    int command_callback(long bufferid, String cmd, String[] args);

    // Longs are native ids
    boolean config_boolean(long option);

    String config_color(long option);

    void config_free(long config_file);

    long config_get(String option_name);

    int config_integer(long option);

    long config_new(String name);

    int config_reload_callback(long config_file_id);
}
