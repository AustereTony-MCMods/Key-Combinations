package ru.austeretony.keycombs.main;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import ru.austeretony.keycombs.event.KeyCombinationsEvents;

@Mod(modid = KeyCombinationsMain.MODID, name = KeyCombinationsMain.NAME, version = KeyCombinationsMain.VERSION)
public class KeyCombinationsMain {
	
    public static final String 
	MODID = "keycombs",
    NAME = "Key Combinations",
    VERSION = "1.1.0",
    GAME_VERSION = "1.7.10",
    VERSIONS_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Key-Combinations/info/versions.json",
    PROJECT_URL = "https://minecraft.curseforge.com/projects/key-combinations";
            
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	    		    
    	if (event.getSide() == Side.CLIENT)
    		Registry.register();    	
    }
    
    public static class Registry {
    	
        public static final KeyBinding 
        KEY_QUIT = new KeyBinding("key.quit", Keyboard.KEY_ESCAPE, "key.categories.misc"),
        KEY_HIDE_HUD = new KeyBinding("key.hideHUD", Keyboard.KEY_F1, "key.categories.misc"),
        KEY_DEBUG_SCREEN = new KeyBinding("key.debugScreen", Keyboard.KEY_F3, "key.categories.misc"),
        KEY_DISABLE_SHADER = new KeyBinding("key.disableShader", Keyboard.KEY_F4, "key.categories.misc");
    	
        public static void register() {
        	
        	ClientRegistry.registerKeyBinding(KEY_QUIT);
        	ClientRegistry.registerKeyBinding(KEY_HIDE_HUD);
        	ClientRegistry.registerKeyBinding(KEY_DEBUG_SCREEN);
        	ClientRegistry.registerKeyBinding(KEY_DISABLE_SHADER);
        	
        	KeyBindingProperty.setKeysConflictContext();
    		        	
        	MinecraftForge.EVENT_BUS.register(new KeyCombinationsEvents());
        }
    }
}