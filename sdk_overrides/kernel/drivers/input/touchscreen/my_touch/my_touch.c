#include <linux/kernel.h>
#include <linux/hrtimer.h>
#include <linux/i2c.h>
#include <linux/input.h>
#include <linux/module.h>
#include <linux/delay.h>
#include <linux/i2c.h>
#include <linux/proc_fs.h>
#include <linux/string.h>
#include <linux/uaccess.h>
#include <linux/vmalloc.h>
#include <linux/interrupt.h>
#include <linux/io.h>
#include <linux/of_gpio.h>
#include <linux/gpio.h>
#include <linux/slab.h>
#include <linux/timer.h>
#include <linux/input/mt.h>
#include <linux/random.h>

#if 1
#define MY_DEBUG(fmt,arg...)  printk("MY_TOUCH:%s %d "fmt"",__FUNCTION__,__LINE__,##arg);
#else
#define MY_DEBUG(fmt,arg...)
#endif
struct input_dev *input_dev;

static struct timer_list my_timer;

void my_timer_callback(struct timer_list *timer)
{
    unsigned int x, y;  // 定义无符号整型变量 x 和 y
    static bool isDown = false;  // 定义静态布尔变量 isDown，并初始化为 false

    // 生成随机数 x，取模得到的值在 [0, 1279] 范围内
    get_random_bytes(&x, sizeof(x));
    x %= 1280;

    // 生成随机数 y，取模得到的值在 [0, 1279] 范围内
    get_random_bytes(&y, sizeof(y));
    y %= 800;

    // 打印调试信息，包括 isDown 的值、x 和 y 的值
    MY_DEBUG("isDown:%d x:%d y:%d!\n", isDown, x, y);

    // 设定输入设备的触摸槽位
    input_mt_slot(input_dev, 0);

    // 报告输入设备的触摸槽位状态，MT_TOOL_FINGER 表示手指状态，isDown 表示是否按下
    input_mt_report_slot_state(input_dev, MT_TOOL_FINGER, isDown);

    // 翻转 isDown 的值模仿手抬起和按下
    isDown = !isDown;

    // 报告输入设备的绝对位置信息：x、y 坐标，触摸面积，触摸宽度
    input_report_abs(input_dev, ABS_MT_POSITION_X, x);
    input_report_abs(input_dev, ABS_MT_POSITION_Y, y);
    input_report_abs(input_dev, ABS_MT_TOUCH_MAJOR, 10);
    input_report_abs(input_dev, ABS_MT_WIDTH_MAJOR, 10);

    // 报告输入设备的指针仿真信息
    input_mt_report_pointer_emulation(input_dev, true);

    // 同步输入事件
    input_sync(input_dev);

    // 重新设置定时器，2 秒后再次触发
    mod_timer(timer, jiffies + msecs_to_jiffies(200));
}

static int my_touch_ts_probe(struct i2c_client *client,
            const struct i2c_device_id *id)
{
    int ret;

    // 打印调试信息
    MY_DEBUG("locat");

    // 分配输入设备对象
    input_dev = devm_input_allocate_device(&client->dev);
    if (!input_dev) {
        dev_err(&client->dev, "Failed to allocate input device.\n");
        return -ENOMEM;
    }

    // 设置输入设备的名称和总线类型
    input_dev->name = "my touch screen";
    input_dev->id.bustype = BUS_I2C;

    /*设置触摸 x 和 y 的最大值*/
    // 设置输入设备的绝对位置参数
    input_set_abs_params(input_dev, ABS_MT_POSITION_X, 0, 1280, 0, 0);
    input_set_abs_params(input_dev, ABS_MT_POSITION_Y, 0, 800, 0, 0);

    // 初始化多点触摸设备的槽位
    ret = input_mt_init_slots(input_dev, 5, INPUT_MT_DIRECT);
    if (ret) {
        dev_err(&client->dev, "Input mt init error\n");
        return ret;
    }

    // 注册输入设备
    ret = input_register_device(input_dev);
    if (ret)
        return ret;

    // 初始化定时器
    timer_setup(&my_timer, my_timer_callback, 0);

    // 设置定时器，5 秒后第一次触发
    mod_timer(&my_timer, jiffies + msecs_to_jiffies(5000));

    return 0;
}

static int my_touch_ts_remove(struct i2c_client *client)
{
    MY_DEBUG("locat");
    return 0;
}

static const struct of_device_id my_touch_of_match[] = {
    { .compatible = "my,touch", },
    { /* sentinel */ }
};
MODULE_DEVICE_TABLE(of, my_touch_of_match);


static struct i2c_driver my_touch_ts_driver = {
    .probe      = my_touch_ts_probe,
    .remove     = my_touch_ts_remove,
    .driver = {
        .name     = "my-touch",
	 .of_match_table = of_match_ptr(my_touch_of_match),
    },
};

static int __init my_ts_init(void)
{
    MY_DEBUG("locat");
    return i2c_add_driver(&my_touch_ts_driver);
}

static void __exit my_ts_exit(void)
{
    MY_DEBUG("locat");
	i2c_del_driver(&my_touch_ts_driver);
}

module_init(my_ts_init);
module_exit(my_ts_exit);

MODULE_LICENSE("GPL");
MODULE_DESCRIPTION("My touch driver");
MODULE_AUTHOR("wucaicheng@qq.com");