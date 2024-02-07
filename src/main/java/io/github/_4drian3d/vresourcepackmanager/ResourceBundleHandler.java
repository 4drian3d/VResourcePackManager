package io.github._4drian3d.vresourcepackmanager;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.Singleton;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public final class ResourceBundleHandler {
    private final Multimap<UUID, ResourcePackInfo> resourceMap = Multimaps
            .newListMultimap(new HashMap<>(), ArrayList::new);

    public void add(UUID uuid, ResourcePackInfo pack) {
        resourceMap.put(uuid, pack);
    }

    public ResourcePackRequest request(UUID uuid) {
        var packs = resourceMap.removeAll(uuid);
        if (packs.isEmpty()) {
            return null;
        }
        return ResourcePackRequest.resourcePackRequest()
                .packs(packs)
                .build();
    }
}
