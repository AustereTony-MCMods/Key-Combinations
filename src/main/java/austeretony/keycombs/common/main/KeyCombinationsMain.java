package austeretony.keycombs.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import austeretony.keycombs.client.keybindings.KeyBindingWrapper;
import austeretony.keycombs.client.reference.ClientReference;
import austeretony.keycombs.common.reference.CommonReference;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

@Mod(
        modid = KeyCombinationsMain.MODID, 
        name = KeyCombinationsMain.NAME, 
        version = KeyCombinationsMain.VERSION,
        certificateFingerprint = "@FINGERPRINT@")
public class KeyCombinationsMain {

    public static final String 
    MODID = "keycombs",
    NAME = "Key Combinations",
    VERSION = "1.1.4",
    GAME_VERSION = "1.7.10",
    VERSIONS_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Key-Combinations/info/versions.json",
    PROJECT_LOCATION = "minecraft.curseforge.com",
    PROJECT_URL = "https://minecraft.curseforge.com/projects/key-combinations";

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @SideOnly(Side.CLIENT)
    public static KeyBinding keyBindingQuit, keyBindingHideHUD, keyBindingDebugScreen, keyBindingDisableShader;

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientReference.registerKeyBinding(keyBindingQuit = new KeyBinding("key.quit", Keyboard.KEY_ESCAPE, "key.categories.misc"));
        ClientReference.registerKeyBinding(keyBindingHideHUD = new KeyBinding("key.hideHUD", Keyboard.KEY_F1, "key.categories.misc"));
        ClientReference.registerKeyBinding(keyBindingDebugScreen = new KeyBinding("key.debugScreen", Keyboard.KEY_F3, "key.categories.misc"));
        ClientReference.registerKeyBinding(keyBindingDisableShader = new KeyBinding("key.disableShader", Keyboard.KEY_F4, "key.categories.misc"));
        KeyBindingWrapper.setKeysConflictContext();
        UpdateChecker updateChecker = new UpdateChecker();
        CommonReference.registerForgeEvent(updateChecker);    		
        new Thread(updateChecker, "Key Combinations Update Check").start();
    }
}