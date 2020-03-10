#include <string.h>

#include <weechat-plugin.h>

#include "xmpp.h"
#include "config.h"

#define THE_TRUTH_CONFIG_NAME "thetruth"


struct t_config_file *the_truth_config_file = NULL;
struct t_config_section *the_truth_config_user_section = NULL;
struct t_config_option *the_truth_config_user_id = NULL;
struct t_config_option *the_truth_config_user_pass = NULL;



// !TODO get user changes
int the_truth_config_user_write_callback (const void *pointer, void *data, struct t_config_file *config_file,
	const char *section_name) {
	(void) pointer;
	(void) data;

	if (!weechat_config_write_line(config_file, section_name, NULL)){
		return WEECHAT_CONFIG_WRITE_ERROR;
	}
	//if(!weechat_config_write_line(config_file, "user",  ))

	return 0;
}

void the_truth_config_init(){

	// Setting up config file
	the_truth_config_file = weechat_config_new(THE_TRUTH_CONFIG_NAME, NULL // TODO callback
		, NULL, NULL);

	// Setting up config sections
	// TODO setup callbacks (before write), to remove all users, before changes are applied
	the_truth_config_user_section = weechat_config_new_section(the_truth_config_file, "user", 0, 0, NULL //TODO callback?
		, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

	// Setting up config options
	the_truth_config_user_pass = weechat_config_new_option(the_truth_config_file, the_truth_config_user_section, 
		"Jabber_Pass", "string", N_("Jabber Password"), NULL, 0, 0, "", NULL, 0, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

	the_truth_config_user_id = weechat_config_new_option(the_truth_config_file, the_truth_config_user_section, 
		"Jabber_ID", "string", N_("Jabber ID"), NULL, 0, 0, "", NULL, 0, 
		NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);


}

int the_truth_config_read() {
	int rc;
	rc = weechat_config_read(the_truth_config_file);

	if(rc != WEECHAT_CONFIG_READ_OK) {
		weechat_printf(NULL, "config could not be read%s\n", __func__);
	}
	return rc;
}

void the_truth_config_write() {
	if(!weechat_config_write(the_truth_config_file) == WEECHAT_CONFIG_WRITE_OK) {
		weechat_printf(NULL, "config write failed in %s\n", __func__);
	}
}


void the_truth_config_free(){
	weechat_config_free(the_truth_config_file);
}

int the_truth_config_update_pass(const char *value){ // TODO config_write_line??
	return weechat_config_option_set(the_truth_config_user_pass, value, 0);
}

int the_truth_config_update_jid(const char *value){
	return weechat_config_option_set(the_truth_config_user_id, value, 0);
}

void the_truth_initialize_user() {
	const char *user = weechat_config_string(the_truth_config_user_id);
	const char *pass = weechat_config_string(the_truth_config_user_pass);

	if(!strcmp(user, "") || !strcmp(pass, "")){
		weechat_printf(NULL, "No user credentials have been found, please add a user with /changeuser.", "");
		return;
	}

	weechat_java_initialize_user(user, pass);
}