
void the_truth_config_init(void);
int the_truth_config_update_pass1(const char *);
int the_truth_config_update_jid1(const char *);
int the_truth_config_update_pass2(const char *);
int the_truth_config_update_jid2(const char *);

int the_truth_config_user_write_callback (const void *, void *, struct t_config_file *, const char *);

void the_truth_config_write(void);
void the_truth_config_free(void);
int the_truth_config_read(void);

void the_truth_initialize_user(void);