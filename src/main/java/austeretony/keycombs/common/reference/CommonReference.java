package austeretony.keycombs.common.reference;

import net.minecraftforge.common.MinecraftForge;

public class CommonReference {

    public static void registerForgeEvent(Object event) {
        MinecraftForge.EVENT_BUS.register(event);
    }
}
