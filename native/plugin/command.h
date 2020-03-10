
#define THE_TRUTH_COMMAND_CALLBACK(__command)                           \
    int                                                                 \
    the_truth_command_##__command (const void *pointer, void *data,     \
                             struct t_gui_buffer *buffer,               \
                             int argc, char **argv, char **argv_eol)

void the_truth_command_init(void);
int the_truth_command_change_user1_cb (const void *, void *,
	struct t_gui_buffer *, int , char **, char **);
int the_truth_command_change_user2_cb (const void *, void *,
	struct t_gui_buffer *, int , char **, char **);