
#define THE_TRUTH_COMMAND_CALLBACK(__command)                           \
    int                                                                 \
    the_truth_command_##__command (const void *pointer, void *data,     \
                             struct t_gui_buffer *buffer,               \
                             int argc, char **argv, char **argv_eol)

void the_truth_command_init(void);