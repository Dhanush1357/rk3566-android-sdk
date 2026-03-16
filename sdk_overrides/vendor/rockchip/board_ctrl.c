#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>

#define RELAY_GPIO 97
#define BUZZER_GPIO 99

void gpio_export(int gpio)
{
    char buf[64];
    int fd = open("/sys/class/gpio/export", O_WRONLY);
    if(fd < 0) return;

    sprintf(buf, "%d", gpio);
    write(fd, buf, strlen(buf));
    close(fd);
}

void gpio_dir_out(int gpio)
{
    char path[64];
    int fd;

    sprintf(path, "/sys/class/gpio/gpio%d/direction", gpio);
    fd = open(path, O_WRONLY);
    if(fd < 0) return;

    write(fd, "out", 3);
    close(fd);
}

void gpio_write(int gpio, int val)
{
    char path[64];
    int fd;

    sprintf(path, "/sys/class/gpio/gpio%d/value", gpio);
    fd = open(path, O_WRONLY);
    if(fd < 0) return;

    if(val)
        write(fd, "1", 1);
    else
        write(fd, "0", 1);

    close(fd);
}

int main(int argc, char *argv[])
{
    if(argc < 3)
    {
        printf("Usage: board_ctrl relay|buzzer on|off\n");
        return -1;
    }

    gpio_export(RELAY_GPIO);
    gpio_export(BUZZER_GPIO);

    gpio_dir_out(RELAY_GPIO);
    gpio_dir_out(BUZZER_GPIO);

    if(strcmp(argv[1],"relay")==0)
    {
        if(strcmp(argv[2],"on")==0)
            gpio_write(RELAY_GPIO,1);
        else
            gpio_write(RELAY_GPIO,0);
    }
    else if(strcmp(argv[1],"buzzer")==0)
    {
        if(strcmp(argv[2],"on")==0)
            gpio_write(BUZZER_GPIO,1);
        else
            gpio_write(BUZZER_GPIO,0);
    }

    return 0;
}
