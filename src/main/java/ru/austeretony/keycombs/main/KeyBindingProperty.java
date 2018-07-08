package ru.austeretony.keycombs.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IntHashMap;

public class KeyBindingProperty {
        
    public static IntHashMap keyBindingsHash;
    
    public static final Map<KeyBinding, EnumKeyModifier> KEY_MODIFIERS = new HashMap<KeyBinding, EnumKeyModifier>();
    
    public static final Map<KeyBinding, EnumKeyModifier> DEFAULT_KEY_MODIFIERS = new HashMap<KeyBinding, EnumKeyModifier>();
	 
    public static final Map<KeyBinding, EnumKeyConflictContext> CONFLICT_CONTEXT = new HashMap<KeyBinding, EnumKeyConflictContext>();
    
    public static final Multimap<EnumKeyModifier, KeyBinding> MODIFIERS = HashMultimap.<EnumKeyModifier, KeyBinding>create();
	
	public static EnumKeyModifier getKeyModifier(KeyBinding key) {
		
		return KEY_MODIFIERS.get(key);
	}
	
	public static void setKeyModifier(KeyBinding key, EnumKeyModifier keyModifier) {
		
		KEY_MODIFIERS.put(key, keyModifier);
	}
	
	public static EnumKeyModifier getDefaultKeyModifier(KeyBinding key) {
		
		return DEFAULT_KEY_MODIFIERS.get(key);
	}
	
	public static EnumKeyConflictContext getKeyConflictContext(KeyBinding key) {
		
		return CONFLICT_CONTEXT.get(key);
	}
	
	public static void setKeyConflictContext(KeyBinding key, EnumKeyConflictContext keyConflictContext) {
		
		CONFLICT_CONTEXT.put(key, keyConflictContext);
	}
	
    public static void setKeyModifierAndCode(KeyBinding key, EnumKeyModifier keyModifier, int keyCode) {
    	
        key.setKeyCode(keyCode);
        
        if (keyModifier.match(keyCode)) {
        	
            keyModifier = EnumKeyModifier.NONE;
        }
        
        MODIFIERS.remove(getKeyModifier(key), key);
        keyBindingsHash.removeObject(keyCode);
        setKeyModifier(key, keyModifier);
        MODIFIERS.put(keyModifier, key);
        keyBindingsHash.addKey(keyCode, key);
    }
    
    public static void setToDefault(KeyBinding key) {
    	
        setKeyModifierAndCode(key, getDefaultKeyModifier(key), key.getKeyCodeDefault());
    }

    public static boolean isSetToDefaultValue(KeyBinding key) {
    	
        return key.getKeyCode() == key.getKeyCodeDefault() && getKeyModifier(key) == getDefaultKeyModifier(key);
    }
    
	public static boolean isKeyDown(KeyBinding key) {
		
		EnumKeyConflictContext conflictContext = getKeyConflictContext(key);
		
        return key.pressed && conflictContext.isActive() && getKeyModifier(key).isActive(conflictContext);
	}
	
	public static KeyBinding lookupActive(int keyCode) {
		
        EnumKeyModifier activeModifier = EnumKeyModifier.getActiveModifier();
        
        if (!activeModifier.match(keyCode)) {
        	
            KeyBinding key = getBinding(keyCode, activeModifier);
            
            if (key != null) {
            	
                return key;
            }
        }
        
        return getBinding(keyCode, EnumKeyModifier.NONE);
	}
	
    private static KeyBinding getBinding(int keyCode, EnumKeyModifier keyModifier) {
    	
        Collection<KeyBinding> keys = MODIFIERS.get(keyModifier);
        
        if (keys != null) {
        	
            for (KeyBinding key : keys) {
            	
                if (isActiveAndMatch(key, keyCode)) {
                	
                    return key;
                }
            }
        }
        
        return null;
    }
    
    public static List<KeyBinding> lookupAll(int keyCode) {
    	
        List<KeyBinding> matchingBindings = new ArrayList<KeyBinding>();
        
        for (KeyBinding key : MODIFIERS.values()) {
        	            
            if (key.getKeyCode() == keyCode) {
            	
                matchingBindings.add(key);
            }
        }
        
        return matchingBindings;
    }
    
    public static boolean isActiveAndMatch(KeyBinding key, int keyCode) {
    	
    	EnumKeyConflictContext conflictContext = getKeyConflictContext(key);
    	
        return keyCode != 0 && keyCode == key.getKeyCode() && conflictContext.isActive() && getKeyModifier(key).isActive(conflictContext);
    }
	
    public static boolean conflicts(KeyBinding currentKey, KeyBinding other) {
    	
        if (getKeyConflictContext(currentKey).conflicts(getKeyConflictContext(other)) || getKeyConflictContext(other).conflicts(getKeyConflictContext(currentKey))) {
        	
            EnumKeyModifier 
            keyModifier = getKeyModifier(currentKey),
            otherKeyModifier = getKeyModifier(other);
            
            if (keyModifier.match(other.getKeyCode()) || otherKeyModifier.match(currentKey.getKeyCode())) {
            	
                return true;
            }
            
            else if (currentKey.getKeyCode() == other.getKeyCode()) {
            	
                return keyModifier == otherKeyModifier || (getKeyConflictContext(currentKey).conflicts(EnumKeyConflictContext.IN_GAME) && (keyModifier == EnumKeyModifier.NONE || otherKeyModifier == EnumKeyModifier.NONE));
            }
        }
        
        return false;
    }
    
    public static boolean hasKeyCodeModifierConflict(KeyBinding currentKey, KeyBinding other) {
    	
        if (getKeyConflictContext(currentKey).conflicts(getKeyConflictContext(other)) || getKeyConflictContext(other).conflicts(getKeyConflictContext(currentKey))) {
        	
            if (getKeyModifier(currentKey).match(other.getKeyCode()) || getKeyModifier(other).match(currentKey.getKeyCode())) {
            	
                return true;
            }
        }
        
        return false;
    }
    
    public static String getDisplayName(KeyBinding key) {
    	
        return getKeyModifier(key).getLocalizedName(key.getKeyCode());
    }
    
    public static void setKeysConflictContext() {
    	
    	GameSettings gameSetings = Minecraft.getMinecraft().gameSettings;
    	
    	EnumKeyConflictContext inGame = EnumKeyConflictContext.IN_GAME;
        
        setKeyConflictContext(gameSetings.keyBindForward, inGame);
        setKeyConflictContext(gameSetings.keyBindLeft, inGame);
        setKeyConflictContext(gameSetings.keyBindBack, inGame);
        setKeyConflictContext(gameSetings.keyBindRight, inGame);
        setKeyConflictContext(gameSetings.keyBindJump, inGame);
        setKeyConflictContext(gameSetings.keyBindSneak, inGame);
        setKeyConflictContext(gameSetings.keyBindSprint, inGame);
        setKeyConflictContext(gameSetings.keyBindAttack, inGame);
        setKeyConflictContext(gameSetings.keyBindChat, inGame);
        setKeyConflictContext(gameSetings.keyBindPlayerList, inGame);
        setKeyConflictContext(gameSetings.keyBindCommand, inGame);
        setKeyConflictContext(gameSetings.keyBindTogglePerspective, inGame);
        setKeyConflictContext(gameSetings.keyBindSmoothCamera, inGame);
        
        setKeyConflictContext(KeyCombsMain.Registry.KEY_QUIT, inGame);
        setKeyConflictContext(KeyCombsMain.Registry.KEY_HIDE_HUD, inGame);
        setKeyConflictContext(KeyCombsMain.Registry.KEY_DEBUG_SCREEN, inGame);
        setKeyConflictContext(KeyCombsMain.Registry.KEY_DISABLE_SHADER, inGame);
    }
}

