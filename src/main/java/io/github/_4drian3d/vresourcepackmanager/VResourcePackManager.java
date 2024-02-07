package io.github._4drian3d.vresourcepackmanager;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerResourcePackStatusEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
	id = "vresourcepackmanager",
	name = "VResourcePack;anager",
	description = "A Velocity Resource Pack Manager",
	version = Constants.VERSION,
	authors = { "4drian3d" }
)
public final class VResourcePackManager {
	@Inject
	private Logger logger;
	@Inject
	private Injector injector;
	
	@Subscribe
	void onProxyInitialization(final ProxyInitializeEvent event) {
		logger.info("Starting VResourcePackManager");
		this.injector.getInstance(MainCommand.class).register();
		logger.info("Started VResourcePackManager");
	}

	@Subscribe
	void onResourcePackResponse(final PlayerResourcePackStatusEvent event) {
		logger.info("Player {} sent {} resource pack status",
				event.getPlayer().getUsername(), event.getStatus());
	}
}