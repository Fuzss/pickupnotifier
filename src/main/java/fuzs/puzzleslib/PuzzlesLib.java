package fuzs.puzzleslib;

import fuzs.puzzleslib.core.EnvTypeExecutor;
import fuzs.puzzleslib.proxy.ClientProxy;
import fuzs.puzzleslib.proxy.IProxy;
import fuzs.puzzleslib.proxy.ServerProxy;

public class PuzzlesLib {
    @SuppressWarnings("Convert2MethodRef")
    public static final IProxy PROXY = EnvTypeExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
}
