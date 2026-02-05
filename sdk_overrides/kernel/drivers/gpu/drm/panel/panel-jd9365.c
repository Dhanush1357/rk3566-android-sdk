// SPDX-License-Identifier: GPL-2.0
/*
 * JD9365 MIPI-DSI LCD panel driver
 *
 * Converted from panel-ilitek-ili9881c.c
 * Panel: XHT070BS056A0-31C (1024x600)
 */

#include <linux/delay.h>
#include <linux/gpio/consumer.h>
#include <linux/module.h>
#include <linux/of.h>
#include <linux/of_device.h>

#include <drm/drm_mipi_dsi.h>
#include <drm/drm_modes.h>
#include <drm/drm_panel.h>

struct jd9365 {
	struct drm_panel panel;
	struct mipi_dsi_device *dsi;
	struct gpio_desc *reset;
	bool prepared;
};

static inline struct jd9365 *panel_to_jd9365(struct drm_panel *panel)
{
	return container_of(panel, struct jd9365, panel);
}

/* ------------------------------------------------------------------------- */
/* DCS helpers */
/* ------------------------------------------------------------------------- */

static int jd9365_dcs_write(struct jd9365 *ctx,
			    const void *data, size_t len)
{
	int ret;

	ret = mipi_dsi_dcs_write_buffer(ctx->dsi, data, len);
	if (ret < 0)
		dev_err(&ctx->dsi->dev,
			"JD9365 DCS write failed: %d\n", ret);

	return ret;
}

#define JD9365_CMD(...)						\
	do {							\
		u8 d[] = { __VA_ARGS__ };			\
		jd9365_dcs_write(ctx, d, ARRAY_SIZE(d));	\
	} while (0)

/* ------------------------------------------------------------------------- */
/* Panel init / exit */
/* ------------------------------------------------------------------------- */

static int jd9365_panel_on(struct jd9365 *ctx)
{
	/* Page 0 */
	JD9365_CMD(0x30, 0x00);
	JD9365_CMD(0xF7, 0x49, 0x61, 0x02, 0x00);

	/* Page 1 */
	JD9365_CMD(0x30, 0x01);
	JD9365_CMD(0x04, 0x0C);
	JD9365_CMD(0x05, 0x08);
	JD9365_CMD(0x0B, 0x13); /* 4-lane DSI */
	JD9365_CMD(0x1F, 0x00);
	JD9365_CMD(0x23, 0x38);
	JD9365_CMD(0x28, 0x18);
	JD9365_CMD(0x29, 0x29);
	JD9365_CMD(0x2A, 0x01);
	JD9365_CMD(0x2B, 0x29);
	JD9365_CMD(0x2C, 0x01);

	/* Page 2 */
	JD9365_CMD(0x30, 0x02);
	JD9365_CMD(0x00, 0x05);
	JD9365_CMD(0x01, 0x22);
	JD9365_CMD(0x02, 0x08);
	JD9365_CMD(0x03, 0x12);
	JD9365_CMD(0x04, 0x16);
	JD9365_CMD(0x05, 0x64);
	JD9365_CMD(0x06, 0x00);
	JD9365_CMD(0x07, 0x00);
	JD9365_CMD(0x08, 0x78);
	JD9365_CMD(0x09, 0x00);
	JD9365_CMD(0x0A, 0x04);

	/* Partial gamma (enough for bring-up) */
	JD9365_CMD(0x0B, 0x16, 0x17, 0x0B, 0x0D, 0x0D, 0x0D,
			 0x11, 0x10, 0x07, 0x07, 0x09);
	JD9365_CMD(0x0C, 0x09, 0x1E, 0x1E, 0x1C, 0x1C, 0x0D,
			 0x0D, 0x0D, 0x0D, 0x0D, 0x0D);

	/* Exit sleep */
	mipi_dsi_dcs_exit_sleep_mode(ctx->dsi);
	msleep(150);

	/* Display ON */
	mipi_dsi_dcs_set_display_on(ctx->dsi);
	msleep(50);

	return 0;
}

static int jd9365_panel_off(struct jd9365 *ctx)
{
	mipi_dsi_dcs_set_display_off(ctx->dsi);
	msleep(50);
	mipi_dsi_dcs_enter_sleep_mode(ctx->dsi);
	msleep(150);

	return 0;
}

/* ------------------------------------------------------------------------- */
/* DRM panel hooks */
/* ------------------------------------------------------------------------- */

static int jd9365_prepare(struct drm_panel *panel)
{
	struct jd9365 *ctx = panel_to_jd9365(panel);
	int ret;

	if (ctx->prepared)
		return 0;

	if (ctx->reset) {
		gpiod_set_value_cansleep(ctx->reset, 0);
		msleep(20);
		gpiod_set_value_cansleep(ctx->reset, 1);
		msleep(120);
	}

	ret = jd9365_panel_on(ctx);
	if (ret)
		return ret;

	ctx->prepared = true;
	return 0;
}

static int jd9365_unprepare(struct drm_panel *panel)
{
	struct jd9365 *ctx = panel_to_jd9365(panel);

	if (!ctx->prepared)
		return 0;

	jd9365_panel_off(ctx);
	ctx->prepared = false;

	return 0;
}

static int jd9365_get_modes(struct drm_panel *panel)
{
	struct drm_display_mode *mode;
	struct drm_connector *connector = panel->connector;

	if (!connector)
		return -EINVAL;

	mode = drm_mode_duplicate(connector->dev,
		&(struct drm_display_mode) {
			.clock = 51200,
			.hdisplay = 1024,
			.hsync_start = 1024 + 160,
			.hsync_end = 1024 + 160 + 24,
			.htotal = 1024 + 160 + 24 + 136,
			.vdisplay = 600,
			.vsync_start = 600 + 12,
			.vsync_end = 600 + 12 + 2,
			.vtotal = 600 + 12 + 2 + 21,
			.width_mm = 154,
			.height_mm = 86,
		});

	if (!mode)
		return -ENOMEM;

	mode->type = DRM_MODE_TYPE_DRIVER | DRM_MODE_TYPE_PREFERRED;
	drm_mode_set_name(mode);
	drm_mode_probed_add(connector, mode);

	return 1;
}

static const struct drm_panel_funcs jd9365_panel_funcs = {
	.prepare = jd9365_prepare,
	.unprepare = jd9365_unprepare,
	.get_modes = jd9365_get_modes,
};

/* ------------------------------------------------------------------------- */
/* Probe / remove */
/* ------------------------------------------------------------------------- */

static int jd9365_probe(struct mipi_dsi_device *dsi)
{
	struct jd9365 *ctx;
	int ret;

	ctx = devm_kzalloc(&dsi->dev, sizeof(*ctx), GFP_KERNEL);
	if (!ctx)
		return -ENOMEM;

	ctx->dsi = dsi;

	ctx->reset = devm_gpiod_get_optional(&dsi->dev,
					     "reset", GPIOD_OUT_HIGH);
	if (IS_ERR(ctx->reset))
		return PTR_ERR(ctx->reset);

	mipi_dsi_set_drvdata(dsi, ctx);

	dsi->lanes = 4;
	dsi->format = MIPI_DSI_FMT_RGB888;
	dsi->mode_flags = MIPI_DSI_MODE_VIDEO |
			  MIPI_DSI_MODE_VIDEO_BURST |
			  MIPI_DSI_MODE_LPM;

	drm_panel_init(&ctx->panel, &dsi->dev,
		       &jd9365_panel_funcs,
		       DRM_MODE_CONNECTOR_DSI);

	drm_panel_add(&ctx->panel);

	ret = mipi_dsi_attach(dsi);
	if (ret) {
		drm_panel_remove(&ctx->panel);
		return ret;
	}

	return 0;
}

static void jd9365_remove(struct mipi_dsi_device *dsi)
{
	struct jd9365 *ctx = mipi_dsi_get_drvdata(dsi);

	mipi_dsi_detach(dsi);
	drm_panel_remove(&ctx->panel);
}

/* ------------------------------------------------------------------------- */
/* OF match */
/* ------------------------------------------------------------------------- */

static const struct of_device_id jd9365_of_match[] = {
	{ .compatible = "jd,jd9365" },
	{ }
};
MODULE_DEVICE_TABLE(of, jd9365_of_match);

static struct mipi_dsi_driver jd9365_driver = {
	.probe = jd9365_probe,
	.remove = jd9365_remove,
	.driver = {
		.name = "panel-jd9365",
		.of_match_table = jd9365_of_match,
	},
};

module_mipi_dsi_driver(jd9365_driver);

MODULE_AUTHOR("Narmadha");
MODULE_DESCRIPTION("JD9365 MIPI DSI Panel Driver");
MODULE_LICENSE("GPL");

