package austeretony.keycombs.common.core;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@Name("Key Combinations Core")
@MCVersion("1.7.10")
@TransformerExclusions({"austeretony.keycombs.common.core"})
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
