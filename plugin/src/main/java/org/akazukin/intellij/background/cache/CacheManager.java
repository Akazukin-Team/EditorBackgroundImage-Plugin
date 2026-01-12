package org.akazukin.intellij.background.cache;

import org.akazukin.service.data.IServiceHolder;
import org.akazukin.service.manager.single.ASingleServiceManager;
import org.jetbrains.annotations.NotNull;

public final class CacheManager extends ASingleServiceManager<ICache> implements ICacheManager {
    public CacheManager() {
        super(ICache.class);
    }

    @Override
    public <T extends ICache> T getCache(@NotNull final Class<T> service) {
        return this.getServiceByInterfaceClass(service);
    }

    @Override
    public ICache[] getCaches() {
        return this.services.stream()
            .map(IServiceHolder::getImplementation)
            .toArray(ICache[]::new);
    }

    @Override
    public void registerCaches() {
        this.registerService(new FileCache());
        this.registerService(new UrlCache());
    }
}
