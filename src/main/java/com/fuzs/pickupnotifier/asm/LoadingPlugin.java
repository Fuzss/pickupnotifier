package com.fuzs.pickupnotifier.asm;

import com.fuzs.pickupnotifier.PickUpNotifier;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.Name(PickUpNotifier.NAME)
@IFMLLoadingPlugin.TransformerExclusions("com.fuzs.pickupnotifier.asm")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1001) // run after runtime deobfuscation
public class LoadingPlugin implements IFMLLoadingPlugin {

    public static boolean runtimeDeobfuscationEnabled = false;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{ "com.fuzs.pickupnotifier.asm.ClassTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        runtimeDeobfuscationEnabled = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}