package ru.austeretony.keycombs.coremod;

import java.io.PrintWriter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IntHashMap;
import ru.austeretony.keycombs.main.EnumKeyConflictContext;
import ru.austeretony.keycombs.main.EnumKeyModifier;
import ru.austeretony.keycombs.main.KeyBindingProperty;
import ru.austeretony.keycombs.main.KeyCombsMain;

public class KeyCombsHooks {
	
	private static Minecraft mc = Minecraft.getMinecraft();

	public static int getQuitKeyCode() {
		
		return KeyCombsMain.Registry.KEY_QUIT.getKeyCode();
	}

	public static int getHideHUDKeyCode() {
		
		return KeyCombsMain.Registry.KEY_HIDE_HUD.isPressed() ? KeyCombsMain.Registry.KEY_HIDE_HUD.getKeyCode() : 0;
	}
	
	public static int getDebugMenuKeyCode() {
		
		return KeyCombsMain.Registry.KEY_DEBUG_SCREEN.isPressed() ? KeyCombsMain.Registry.KEY_DEBUG_SCREEN.getKeyCode() : 0;
	}
	
	public static int getDisableShaderKeyCode() {
		
		return KeyCombsMain.Registry.KEY_DISABLE_SHADER.isPressed() ? KeyCombsMain.Registry.KEY_DISABLE_SHADER.getKeyCode() : 0;
	}
	
	public static void getKeyBindingsHash(IntHashMap hash) {
		
		if (KeyBindingProperty.keyBindingsHash == null) {
						
			KeyBindingProperty.keyBindingsHash = hash;
		}
	}
	
	public static void storeKeybinding(KeyBinding keyBinding) {
				
		KeyBindingProperty.setKeyModifier(keyBinding, EnumKeyModifier.NONE);
		
		KeyBindingProperty.DEFAULT_KEY_MODIFIERS.put(keyBinding, EnumKeyModifier.NONE);
		
		KeyBindingProperty.setKeyConflictContext(keyBinding, EnumKeyConflictContext.UNIVERSAL);
		
		KeyBindingProperty.MODIFIERS.put(EnumKeyModifier.NONE, keyBinding);
	}
	
	public static void setKeyModifierAndCode(KeyBinding key, EnumKeyModifier keyModifier, int keyCode) {
		
		KeyBindingProperty.setKeyModifierAndCode(key, keyModifier, keyCode);
	}
	
	public static KeyBinding resetKeyBinding(KeyBinding key, int keyCode) {
		
		if (!EnumKeyModifier.isKeyCodeModifier(keyCode))
		return null;
		
		return key;
	}
	
	public static void setKeyBindingButtonDispalyString(GuiButton keyButton, GuiButton resetButton, KeyBinding key, boolean flag) {
				
		resetButton.enabled = !KeyBindingProperty.isSetToDefaultValue(key);
		
		keyButton.displayString = KeyBindingProperty.getDisplayName(key);
		
        boolean 
        flag1 = false,
		keyCodeModifierConflict = true;
		
        if (key.getKeyCode() != 0) {
        	
            for (KeyBinding keyBinding : mc.gameSettings.keyBindings) {
            	
                if (keyBinding != key && KeyBindingProperty.conflicts(keyBinding, key)) {
                	
                    flag1 = true;
                    
                    keyCodeModifierConflict &= KeyBindingProperty.hasKeyCodeModifierConflict(keyBinding, key);
                }
            }
        }

        if (flag) {
        	
        	keyButton.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + keyButton.displayString + EnumChatFormatting.WHITE + " <";
        }
        
        else if (flag1) {
        	
        	keyButton.displayString = (keyCodeModifierConflict ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + keyButton.displayString;
        }
	}
	
	public static void drawCuiControlsKeyEntry(GuiButton changeKeyButton, GuiButton resetButton, KeyBinding key, boolean flag, int par2, int par3, int par7, int par8) {
		
        resetButton.xPosition = par2 + 210;
        resetButton.yPosition = par3;
        resetButton.enabled = !KeyBindingProperty.isSetToDefaultValue(key);
        resetButton.drawButton(mc, par7, par8);
                   
        changeKeyButton.width = 95;
        changeKeyButton.xPosition = par2 + 105;
        changeKeyButton.yPosition = par3;
        changeKeyButton.displayString = KeyBindingProperty.getDisplayName(key);
        
        boolean 
        flag1 = false,
		keyCodeModifierConflict = true;
		
        if (key.getKeyCode() != 0) {
        	
            for (KeyBinding keyBinding : mc.gameSettings.keyBindings) {
            	
                if (keyBinding != key && KeyBindingProperty.conflicts(keyBinding, key)) {
                	
                    flag1 = true;
                    
                    keyCodeModifierConflict &= KeyBindingProperty.hasKeyCodeModifierConflict(keyBinding, key);
                }
            }
        }

        if (flag) {
        	
        	changeKeyButton.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + changeKeyButton.displayString + EnumChatFormatting.WHITE + " <";
        }
        
        else if (flag1) {
        	
        	changeKeyButton.displayString = (keyCodeModifierConflict ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + changeKeyButton.displayString;
        }
        
        changeKeyButton.drawButton(mc, par7, par8);
	}

	public static void setResetButtonState(GuiButton resetAllButton) {
		
		boolean state = false;
		
        for (KeyBinding key : mc.gameSettings.keyBindings) {
        	
            if (!KeyBindingProperty.isSetToDefaultValue(key)) {
            	
            	state = true;
            	
                break;
            }
        }
        
    	resetAllButton.enabled = state;
	}
	
	public static void setToDefault(KeyBinding key) {
		
		KeyBindingProperty.setToDefault(key);
	}
	
	public static void resetAllKeys() {
		
        for (KeyBinding key : mc.gameSettings.keyBindings) {
        	
        	KeyBindingProperty.setToDefault(key);
        }

        KeyBinding.resetKeyBindingArrayAndHash();
	}
	
	public static boolean loadControlsFromOptionsFile(String[] data) {
				
		if (mc.gameSettings != null) {

	        for (KeyBinding key : mc.gameSettings.keyBindings) {
			
		        if (data[0].equals("key_" + key.getKeyDescription())) {
		        	
			        if (data[1].indexOf('&') != - 1) {
			        	
			            String[] keySettings = data[1].split("&");
			            
			            KeyBindingProperty.setKeyModifierAndCode(key, EnumKeyModifier.valueFromString(keySettings[1]), Integer.parseInt(keySettings[0]));
			        } 
			        
			        else {
			        	
			        	KeyBindingProperty.setKeyModifierAndCode(key, EnumKeyModifier.NONE, Integer.parseInt(data[1]));
			        }
		        }
	        }
		}
        
        return false;
	}
	
	public static boolean saveControlsToOptionsFile(PrintWriter writer) {
		
		if (mc.gameSettings != null) {

			EnumKeyModifier keyModifier;
			
	        for (KeyBinding key : mc.gameSettings.keyBindings) {
	        	
		        keyModifier = KeyBindingProperty.getKeyModifier(key);
		
		        String keyString = "key_" + key.getKeyDescription() + ":" + key.getKeyCode();
		        	        
		        writer.println(keyModifier != EnumKeyModifier.NONE ? keyString + "&" + keyModifier : keyString);
			}
		}
        
        return false;
	}
	
	public static KeyBinding lookupActive(int keyCode) {
		
		return KeyBindingProperty.lookupActive(keyCode);
	}
	
	public static void setKeybindingsState(int keyCode, boolean state) {
		
        if (keyCode != 0) {
        	
            for (KeyBinding key : KeyBindingProperty.lookupAll(keyCode)) {

            	if (key != null) {
	        	
            		key.pressed = state;
            	}  
            }
        }
	}
	
	public static boolean isKeyPressed(KeyBinding key) {
		
		return KeyBindingProperty.isKeyDown(key);
	}
}
