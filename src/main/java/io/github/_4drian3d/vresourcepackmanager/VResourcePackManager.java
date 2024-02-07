package io.github._4drian3d.vresourcepackmanager;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
	id = "velocityplugin",
	name = "VelocityPlugin",
	description = "A Velocity Plugin Template",
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
}