package ru.austeretony.keycombs.main;

import java.io.PrintStream;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = KeyCombinationsMain.MODID, name = KeyCombinationsMain.NAME, version = KeyCombinationsMain.VERSION)
public class KeyCombinationsMain {
	
    public static final String 
	MODID = "keycombs",
    NAME = "Key Combinations",
    VERSION = "1.1.0",
    GAME_VERSION = "1.6.4",
    VERSIONS_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Key-Combinations/info/versions.json",
    PROJECT_URL = "https://minecraft.curseforge.com/projects/key-combinations";
           
	public static final PrintStream LOGGER = System.out;

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	    		    
    	KeyCombinationsKeyHandler.init();
    		
        KeyBindingProperty.setKeysConflictContext();
        	
        MinecraftForge.EVENT_BUS.register(new UpdateChecker());
    }
}