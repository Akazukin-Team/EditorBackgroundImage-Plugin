package org.akazukin.intellij.background.cache;

import org.akazukin.service.manager.single.SingleServiceManager;
import org.akazukin.service.registry.SingleServiceRegistry;
import org.jetbrains.annotations.NotNull;

public final class CacheManager extends SingleServiceManager<ICache> implements ICacheManager {
    public CacheManager(final SingleServiceRegistry<ICache> reg) {
        super(reg);
    }

    @Override
    public <T extends ICache> T getCache(@NotNull final Class<T> service) {
        return this.getServiceByInterfaceClass(service);
    }

    @Override
    public ICache[] getCaches() {
        return this.getAllServices();
    }
}
