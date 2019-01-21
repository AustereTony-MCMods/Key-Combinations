package austeretony.keycombs.common.main;

import java.io.PrintStream;

import austeretony.keycombs.client.keybindings.KeyBindingProperty;
import austeretony.keycombs.common.reference.CommonReference;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = KeyCombinationsMain.MODID, name = KeyCombinationsMain.NAME, version = KeyCombinationsMain.VERSION)
public class KeyCombinationsMain {

    public static final String 
    MODID = "keycombs",
    NAME = "Key Combinations",
    VERSION = "1.1.3",
    GAME_VERSION = "1.6.4",
    VERSIONS_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Key-Combinations/info/versions.json",
    PROJECT_LOCATION = "minecraft.curseforge.com",
    PROJECT_URL = "https://minecraft.curseforge.com/projects/key-combinations";

    public static final PrintStream LOGGER = System.out;

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void init(FMLInitializationEvent event) {
        KeyCombinationsKeyHandler.init();
        KeyBindingProperty.setKeysConflictContext();
        UpdateChecker updateChecker = new UpdateChecker();
        CommonReference.registerForgeEvent(updateChecker);    		
        new Thread(updateChecker, "Key Combinations Update Check").start();
        LOGGER.println("[Key Combinations][INFO] Update check started...");   
    }
}