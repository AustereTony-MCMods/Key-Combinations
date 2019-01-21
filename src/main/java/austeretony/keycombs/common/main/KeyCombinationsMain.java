package austeretony.keycombs.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import austeretony.keycombs.client.keybindings.KeyBindingProperty;
import austeretony.keycombs.client.reference.ClientReference;
import austeretony.keycombs.common.reference.CommonReference;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = KeyCombinationsMain.MODID, name = KeyCombinationsMain.NAME, version = KeyCombinationsMain.VERSION)
public class KeyCombinationsMain {

    public static final String 
    MODID = "keycombs",
    NAME = "Key Combinations",
    VERSION = "1.1.3",
    GAME_VERSION = "1.8.9",
    VERSIONS_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Key-Combinations/info/versions.json",
    PROJECT_LOCATION = "minecraft.curseforge.com",
    PROJECT_URL = "https://minecraft.curseforge.com/projects/key-combinations";

    public static final Logger LOGGER = LogManager.getLogger("Key Combinations");

    @SideOnly(Side.CLIENT)
    public static KeyBinding keyBindingQuit, keyBindingHideHUD, keyBindingDebugScreen, keyBindingDisableShader;

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientReference.registerKeyBinding(keyBindingQuit = new KeyBinding("key.quit", Keyboard.KEY_ESCAPE, "key.categories.misc"));
        ClientReference.registerKeyBinding(keyBindingHideHUD = new KeyBinding("key.hideHUD", Keyboard.KEY_F1, "key.categories.misc"));
        ClientReference.registerKeyBinding(keyBindingDebugScreen = new KeyBinding("key.debugScreen", Keyboard.KEY_F3, "key.categories.misc"));
        ClientReference.registerKeyBinding(keyBindingDisableShader = new KeyBinding("key.disableShader", Keyboard.KEY_F4, "key.categories.misc"));
        KeyBindingProperty.setKeysConflictContext();
        UpdateChecker updateChecker = new UpdateChecker();
        CommonReference.registerForgeEvent(updateChecker);
        new Thread(updateChecker, "Key Combinations Update Check").start();
        LOGGER.info("Update check started...");    
    }
}