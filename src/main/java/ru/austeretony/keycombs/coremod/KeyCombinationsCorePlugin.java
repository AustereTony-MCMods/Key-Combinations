package ru.austeretony.keycombs.coremod;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions({"ru.austeretony.keycombs.coremod"})
public class KeyCombinationsCorePlugin implements IFMLLoadingPlugin {
	
    private static boolean isObfuscated;
		
    @Override
    public String[] getASMTransformerClass() {
    	
        return new String[] {KeyCombinationsClassTransformer.class.getName()};
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
    	
    	isObfuscated = (boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
    	
        return null;
    }
    
    public static boolean isObfuscated() {
    	
    	return isObfuscated;
    }
}
