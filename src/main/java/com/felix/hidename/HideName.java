package com.felix.hidename;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;

@Mod(HideName.MODID)
public class HideName {
    public static final String MODID = "hidename";
    public static final Logger LOGGER = LogUtils.getLogger();

    public HideName() {
        // Главный класс мода
    }
}