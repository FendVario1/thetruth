#include <weechat-plugin.h>

#include "xmpp.h"
#include "config.h"
#include "command.h"

THE_TRUTH_COMMAND_CALLBACK(change_user1_cb) {
	int retVal;
	(void) pointer;
	(void) data;
	(void) buffer;
	(void) argv_eol;

	weechat_printf(NULL, "in callback %s\n", __func__);

	if(argc != 3){
		return WEECHAT_RC_ERROR;
	}
	retVal = the_truth_config_update_jid1(argv[1]);//weechat_config_option_set(the_truth_config_user_id, argv[1], 0);
	if(retVal == WEECHAT_CONFIG_OPTION_SET_ERROR) {
		weechat_printf(NULL, "user id not set in %s\n", __func__);
		return WEECHAT_RC_ERROR;
	}

	retVal = the_truth_config_update_pass1(argv[2]);//weechat_config_option_set(the_truth_config_user_pass, argv[2], 0);
	if(retVal == WEECHAT_CONFIG_OPTION_SET_ERROR) {
		weechat_printf(NULL, "pass not set in %s\n", __func__);
		return WEECHAT_RC_ERROR;
	}



	return WEECHAT_RC_OK;
}

THE_TRUTH_COMMAND_CALLBACK(change_user2_cb) {
	int retVal;
	(void) pointer;
	(void) data;
	(void) buffer;
	(void) argv_eol;

	weechat_printf(NULL, "in callback %s\n", __func__);

	if(argc != 3){
		return WEECHAT_RC_ERROR;
	}
	retVal = the_truth_config_update_jid2(argv[1]);//weechat_config_option_set(the_truth_config_user_id, argv[1], 0);
	if(retVal == WEECHAT_CONFIG_OPTION_SET_ERROR) {
		weechat_printf(NULL, "user id not set in %s\n", __func__);
		return WEECHAT_RC_ERROR;
	}

	retVal = the_truth_config_update_pass2(argv[2]);//weechat_config_option_set(the_truth_config_user_pass, argv[2], 0);
	if(retVal == WEECHAT_CONFIG_OPTION_SET_ERROR) {
		weechat_printf(NULL, "pass not set in %s\n", __func__);
		return WEECHAT_RC_ERROR;
	}



	return WEECHAT_RC_OK;
}

void the_truth_command_init() {
	weechat_hook_command("changeuser1",
		N_("add new user credentials for jabber"),
		N_("<Jabber_ID> <Jabber_Pass>"),
		N_("Jabber_ID: JabberID to use\n"
			"Jabber_Pass: Password for JabberID"),
		NULL, &the_truth_command_change_user1_cb, NULL, NULL);
	weechat_hook_command("changeuser2",
		N_("add new user credentials for jabber"),
		N_("<Jabber_ID> <Jabber_Pass>"),
		N_("Jabber_ID: JabberID to use\n"
			"Jabber_Pass: Password for JabberID"),
		NULL, &the_truth_command_change_user2_cb, NULL, NULL);
	weechat_hook_command("query",
		N_("start chatting with a user"),
		N_("<Jabber_ID>"),
		N_("Jabber_ID: Jabber_ID of your chatpartner"),
		NULL, &xmpp_command_cb, "query", NULL);
	weechat_hook_command("join",
		N_("join a MUC chat"),
		N_("<Jabber_ID> <Nickname> [Password]"),
		N_("Jabber_ID: JabberID of MUC\n"
			"Nickname: Your nickname on the server\n"
			"Password: the servers password"),
		NULL, &xmpp_command_cb, "join", NULL);
	weechat_hook_command("add",
		N_("add a user to your roster"),
		N_("<Jabber_ID> <Nickname> [Group1] [Group2] [GroupX]"),
		N_("Jabber_ID: JabberID of user\n"
			"Nickname: nickname for roster entry\n"
			"Group: group for entry"),
		NULL, &xmpp_command_cb, "add", NULL);
	weechat_hook_command("remove",
		N_("remove a user from your roster"),
		N_("<Nickname>"),
		N_("Nickname: nickname of roster entry"),
		NULL, &xmpp_command_cb, "remove", NULL);
	weechat_hook_command("bookmarkAdd",
		N_("add a new bookmark"),
		N_("<conferenceName> <Jid> <Autojoin> <Nickname> [Password]"),
		N_("conferenceName: Name of the conference\n"
			"Jid: Jid of conference\n"
			"Autojoin: Autojoin setting for conference\n"
			"Nickname: Nickname for conference\n"
			"Password: Password for conference"),
		NULL, &xmpp_command_cb, "bookmarkAdd", NULL);
	weechat_hook_command("bookmarkEdit",
		N_("edit a bookmark"),
		N_("<conferenceName> <Jid> <Autojoin> <Nickname> [Password]"),
		N_("conferenceName: Name of the conference\n"
			"Jid: Jid of bookmarked conference\n"
			"Autojoin: Autojoin setting for conference\n"
			"Nickname: Nickname for conference\n"
			"Password: Password for conference"),
		NULL, &xmpp_command_cb, "bookmarkEdit", NULL);
	weechat_hook_command("bookmarkRemove",
		N_("remove a conference from your bookmarks"),
		N_("<jid>"),
		N_("jid: jid of bookmarked conference"),
		NULL, &xmpp_command_cb, "bookmarkRemove", NULL);
	weechat_hook_command("bookmarks",
		N_("shows all currently saved bookmarks"),
		N_(""),
		N_(""),		
		NULL, &xmpp_command_cb, "rosterRequests", NULL);
    weechat_hook_command("rosterRequests",
        N_("shows all currently available add to roster requests"),
        N_(""),
        N_(""),
        NULL, &xmpp_command_cb, "rosterRequests", NULL);
	weechat_hook_command("rosterAdd",
		N_("Add a user to your roster"),
		N_("<RequestId>"),
		N_("RequestId: id of request provided by /rosterRequests"),
		NULL, &xmpp_command_cb, "rosterAdd", NULL);
}