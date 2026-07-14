package cz.miki85.translucentleaves;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import org.slf4j.Logger;

@Mod("translucentleaves")
public class TranslucentLeaves {
    private static final Logger LOGGER = LogUtils.getLogger();

    public TranslucentLeaves() {
        boolean isClient = FMLLoader.getDist() == Dist.CLIENT;
        LOGGER.info("Translucent Leaves loaded on {}. Dynamic leaf light transparency active.", isClient ? "client" : "server");
    }
}