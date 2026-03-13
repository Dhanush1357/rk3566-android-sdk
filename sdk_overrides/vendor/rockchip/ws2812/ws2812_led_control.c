
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdint.h>
#include <string.h>
#include <linux/spi/spidev.h>
#include <sys/ioctl.h>

#define SPI_DEV "/dev/spidev3.0"
#define LEDS 8

uint8_t encode_bit(uint8_t bit)
{
    return bit ? 0b110 : 0b100;
}

int main(int argc, char *argv[])
{
    if(argc < 2)
    {
        printf("Usage: %s red|green|blue|white|off\n", argv[0]);
        return -1;
    }

    uint8_t G=0,R=0,B=0;

    if(strcmp(argv[1],"red")==0) R=0xFF;
    else if(strcmp(argv[1],"green")==0) G=0xFF;
    else if(strcmp(argv[1],"blue")==0) B=0xFF;
    else if(strcmp(argv[1],"white")==0) R=G=B=0xFF;
    else if(strcmp(argv[1],"off")==0) R=G=B=0x00;
    else
    {
        printf("Invalid color\n");
        return -1;
    }

    int fd=open(SPI_DEV,O_WRONLY);
    if(fd<0)
    {
        perror("SPI open failed");
        return -1;
    }

    uint8_t mode=SPI_MODE_0;
    uint8_t bits=8;
    uint32_t speed=2400000;

    ioctl(fd,SPI_IOC_WR_MODE,&mode);
    ioctl(fd,SPI_IOC_WR_BITS_PER_WORD,&bits);
    ioctl(fd,SPI_IOC_WR_MAX_SPEED_HZ,&speed);

    /* reset */
    uint8_t reset[100]={0};
    write(fd,reset,sizeof(reset));
    usleep(100);

    uint8_t spi_data[LEDS*72];
    int index=0;

    for(int led=0;led<LEDS;led++)
    {
        uint8_t color[3]={G,R,B};

        for(int i=0;i<3;i++)
        {
            for(int bit=7;bit>=0;bit--)
            {
                uint8_t val=(color[i]>>bit)&1;
                spi_data[index++]=encode_bit(val);
            }
        }
    }

    write(fd,spi_data,index);

    write(fd,reset,sizeof(reset));
    usleep(100);

    close(fd);

    return 0;
}


