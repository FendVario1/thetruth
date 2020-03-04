#include <weechat-plugin.h>

#include "xmpp.h"
#include "config.h"
#include "command.h"

THE_TRUTH_COMMAND_CALLBACK(adduser_cb) {
	int retVal;
	(void) pointer;
	(void) data;
	(void) buffer;
	(void) argv_eol;

	weechat_printf(NULL, "in callback %s\n", __func__);

	if(argc != 3){
		return WEECHAT_RC_ERROR;
	}
	retVal = the_truth_config_update_jid(argv[1]);//weechat_config_option_set(the_truth_config_user_id, argv[1], 0);
	if(retVal == WEECHAT_CONFIG_OPTION_SET_ERROR) {
		weechat_printf(NULL, "user id not set in %s\n", __func__);
		return WEECHAT_RC_ERROR;
	}

	retVal = the_truth_config_update_pass(argv[2]);//weechat_config_option_set(the_truth_config_user_pass, argv[2], 0);
	if(retVal == WEECHAT_CONFIG_OPTION_SET_ERROR) {
		weechat_printf(NULL, "pass not set in %s\n", __func__);
		return WEECHAT_RC_ERROR;
	}



	return WEECHAT_RC_OK;
}

void the_truth_command_init() {
	weechat_hook_command("tadduser", 
		N_("add new user credentials for jabber"),
		N_("<Jabber_ID> <Jabber_Pass>"),
		N_("Jabber_ID: JabberID to use"
			"Jabber_Pass: Password for JabberID"),
		NULL, &the_truth_command_adduser_cb, NULL, NULL);
	weechat_hook_command("query",
		N_("start chatting with a user"),
		N_("<Jabber_ID>"),
		N_("Jabber_ID: Jabber_ID of your chatpartner"),
		NULL, &xmpp_command_cb, "query", NULL);
	weechat_hook_command("join",
		N_("join a MUC chat"),
		N_("<Jabber_ID> <Nickname> [Password]"),
		N_("Jabber_ID: JabberID of MUC"
			"Nickname: Your nickname on the server"
			"Password: the servers password"),
		NULL, &xmpp_command_cb, "join", NULL);
}