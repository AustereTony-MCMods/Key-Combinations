package ru.austeretony.keycombs.main;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import ru.austeretony.keycombs.event.KeyCombsEvents;

@Mod(modid = KeyCombsMain.MODID, name = KeyCombsMain.NAME, version = KeyCombsMain.VERSION)
public class KeyCombsMain {
	
    public static final String 
	MODID = "keycombs",
    NAME = "Key Combinations",
    VERSION = "1.0.0",
    GAME_VERSION = "1.7.10",
    VERSIONS_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Key-Combinations/info/versions.json",
    PROJECT_URL = "https://www.curseforge.com/minecraft/mc-mods/key-combinations";
    
    public static final KeyBindingProperty PROPERTY = new KeyBindingProperty();
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	    		    	    	
    	Registry.register();
    }
    
    public static class Registry {
    	
        public static final KeyBinding 
        KEY_QUIT = new KeyBinding("key.quit", 1, "key.categories.misc"),
        KEY_HIDE_HUD = new KeyBinding("key.hideHUD", 59, "key.categories.misc"),
        KEY_DEBUG_SCREEN = new KeyBinding("key.debugScreen", 61, "key.categories.misc"),
        KEY_DISABLE_SHADER = new KeyBinding("key.disableShader", 62, "key.categories.misc");
    	
        public static void register() {
        	
        	ClientRegistry.registerKeyBinding(KEY_QUIT);
        	ClientRegistry.registerKeyBinding(KEY_HIDE_HUD);
        	ClientRegistry.registerKeyBinding(KEY_DEBUG_SCREEN);
        	ClientRegistry.registerKeyBinding(KEY_DISABLE_SHADER);
        	
        	KeyBindingProperty.setKeysConflictContext();
    		
        	MinecraftForge.EVENT_BUS.register(new KeyCombsEvents());
        }
    }
}