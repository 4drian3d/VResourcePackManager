package io.github._4drian3d.vresourcepackmanager;

import com.google.inject.Inject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.resource.ResourcePackRequestLike;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public final class MainCommand {
    @Inject
    private CommandManager commandManager;
    @Inject
    private VResourcePackManager plugin;
    @Inject
    private ResourceBundleHandler resourceHandler;

    void register() {
        final var node = BrigadierCommand.literalArgumentBuilder("vrpm")
                .requires(src -> src instanceof Player && src.hasPermission("vrpm.command"))
            .then(BrigadierCommand.literalArgumentBuilder("clear")
                .then(BrigadierCommand.literalArgumentBuilder("all")
                    .executes(ctx -> {
                        final Player player = (Player) ctx.getSource();
                        player.clearResourcePacks();
                        player.sendMessage(miniMessage().deserialize("Cleared all resource packs"));
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(BrigadierCommand.literalArgumentBuilder("first")
                    .executes(ctx -> {
                        final Player player = (Player) ctx.getSource();
                        final Collection<ResourcePackInfo> packs = player.getAppliedResourcePacks();
                        if (packs.isEmpty()) {
                            player.sendMessage(miniMessage().deserialize("You have no resource pack applied"));
                        } else {
                            final ResourcePackInfo pack = packs.iterator().next();
                            player.removeResourcePacks((ResourcePackRequestLike) pack);
                            player.sendMessage(miniMessage().deserialize("Cleared first resource pack"));
                        }
                        return Command.SINGLE_SUCCESS;
                    })
                )
            )
            .then(BrigadierCommand.literalArgumentBuilder("session")
                .then(BrigadierCommand.literalArgumentBuilder("add")
                    .then(BrigadierCommand.requiredArgumentBuilder("packUrl", StringArgumentType.string())
                        .executes(ctx -> {
                            final Player player = (Player) ctx.getSource();

                            try {
                                final URI resourcePackURI = new URI(ctx.getArgument("packUrl", String.class));
                                resourceHandler.add(player.getUniqueId(), net.kyori.adventure.resource.ResourcePackInfo
                                        .resourcePackInfo()
                                        .id(UUID.randomUUID())
                                        .uri(resourcePackURI)
                                        .build());
                            } catch (URISyntaxException e) {
                                player.sendMessage(miniMessage().deserialize("Invalid URI provided"));
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                    )

                )
            )
            .then(BrigadierCommand.literalArgumentBuilder("apply")
                .then(BrigadierCommand.literalArgumentBuilder("bundle")
                    .executes(ctx -> {
                        final Player player = (Player) ctx.getSource();
                        final var request = resourceHandler.request(player.getUniqueId());
                        if (request == null) {
                            player.sendMessage(miniMessage().deserialize("Packs not found"));
                        } else {
                            player.sendResourcePacks(request);
                            player.sendMessage(miniMessage().deserialize("Applied packs"));
                        }
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(BrigadierCommand.literalArgumentBuilder("single")
                    .then(BrigadierCommand.requiredArgumentBuilder("packUrl", StringArgumentType.string())
                        .executes(ctx -> {
                            final Player player = (Player) ctx.getSource();
                            try {
                                final URI resourcePackURI = new URI(ctx.getArgument("packUrl", String.class));
                                final ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
                                        .packs(net.kyori.adventure.resource.ResourcePackInfo.resourcePackInfo()
                                                .id(UUID.randomUUID())
                                                .uri(resourcePackURI)
                                                .build())
                                        .build();
                                player.sendResourcePacks(request);
                            } catch (URISyntaxException e) {
                                player.sendMessage(miniMessage().deserialize("Invalid URI provided"));
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
            )
            .then(BrigadierCommand.literalArgumentBuilder("list")
                    .executes(ctx -> {
                        final Player player = (Player) ctx.getSource();
                        final var resourceList = player.getAppliedResourcePacks()
                                .stream()
                                .map(ResourcePackInfo::toString)
                                .collect(Collectors.joining(", "));

                        player.sendMessage(miniMessage().deserialize("Player resource packs: " + resourceList));
                        return Command.SINGLE_SUCCESS;
                    })
            );

        final BrigadierCommand command = new BrigadierCommand(node);
        commandManager.register(command);
    }
}
