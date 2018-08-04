package ru.austeretony.keycombs.core;

import java.io.PrintWriter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import ru.austeretony.keycombs.main.EnumKeyModifier;
import ru.austeretony.keycombs.main.KeyBindingProperty;
import ru.austeretony.keycombs.main.KeyCombinationsMain;

public class KeyCombinationsHooks {
					
	public static int getQuitKeyCode() {
		
		return KeyCombinationsMain.keyBindingQuit.getKeyCode();
	}

	public static boolean isQuitKeyPressed(int key) {
		
		return KeyBindingProperty.get(KeyCombinationsMain.keyBindingQuit).isActiveAndMatch(key);
	}

	public static boolean isHideHUDKeyPressed(int key) {
		
		return KeyBindingProperty.get(KeyCombinationsMain.keyBindingHideHUD).isActiveAndMatch(key);
	}
	
	public static int getDebugScreenKeyCode() {
				
		return KeyBindingProperty.get(KeyCombinationsMain.keyBindingDebugScreen).isActiveAndMatch(KeyCombinationsMain.keyBindingDebugScreen.getKeyCode()) ? KeyCombinationsMain.keyBindingDebugScreen.getKeyCode() : 0;
	}
	
	public static int getDisableShaderKeyCode() {
		
		return KeyCombinationsMain.keyBindingDisableShader.isPressed() ? KeyCombinationsMain.keyBindingDisableShader.getKeyCode() : 0;
	}
	
	public static void createPropertry(KeyBinding keyBinding) {
				
		KeyBindingProperty.create(keyBinding);
	}
	
	public static void setKeyModifierAndCode(KeyBinding key, EnumKeyModifier keyModifier, int keyCode) {
				
		KeyBindingProperty.get(key).setKeyModifierAndCode(keyModifier, keyCode);
	}
	
	public static KeyBinding resetKeyBinding(KeyBinding key, int keyCode) {
		
		if (!EnumKeyModifier.isKeyCodeModifier(keyCode))
			return null;
		
		return key;
	}
	
	public static void drawCuiControlsKeyEntry(GuiButton changeKeyButton, GuiButton resetButton, KeyBinding key, boolean flag, int par2, int par3, int par7, int par8) {
		
		KeyBindingProperty 
		property = KeyBindingProperty.get(key),
		otherProperty;

        resetButton.xPosition = par2 + 210;
        resetButton.yPosition = par3;
        resetButton.enabled = !property.isSetToDefaultValue();
        resetButton.drawButton(Minecraft.getMinecraft(), par7, par8);
                   
        changeKeyButton.width = 95;
        changeKeyButton.xPosition = par2 + 105;
        changeKeyButton.yPosition = par3;
        changeKeyButton.displayString = property.getDisplayName();
        
        boolean 
        flag1 = false,
		keyCodeModifierConflict = true;
		
        if (key.getKeyCode() != 0) {
        	
            for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
            	
            	otherProperty = KeyBindingProperty.get(keyBinding);
            	
                if (keyBinding != key && property.conflicts(otherProperty)) {
                	
                    flag1 = true;
                    
                    keyCodeModifierConflict &= property.hasKeyCodeModifierConflict(otherProperty);
                }
            }
        }

        if (flag)     	
        	changeKeyButton.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + changeKeyButton.displayString + EnumChatFormatting.WHITE + " <";
        else if (flag1)        	
        	changeKeyButton.displayString = (keyCodeModifierConflict ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + changeKeyButton.displayString;
        
        changeKeyButton.drawButton(Minecraft.getMinecraft(), par7, par8);
	}

	public static void setResetButtonState(GuiButton resetAllButton) {
				
		boolean state = false;
		
        for (KeyBinding key : Minecraft.getMinecraft().gameSettings.keyBindings) {
        	        	
            if (!KeyBindingProperty.get(key).isSetToDefaultValue()) {
            	
            	state = true;
            	
                break;
            }
        }
        
    	resetAllButton.enabled = state;
	}
	
	public static void setToDefault(KeyBinding key) {
				
		KeyBindingProperty.get(key).setToDefault();
	}
	
	public static void resetAllKeys() {
		
		KeyBindingProperty property;
		
        for (KeyBinding key : Minecraft.getMinecraft().gameSettings.keyBindings)       	        	
        	KeyBindingProperty.get(key).setToDefault();

        KeyBinding.resetKeyBindingArrayAndHash();
	}
	
	public static boolean loadControlsFromOptionsFile(String[] data) {
				
		if (Minecraft.getMinecraft().gameSettings != null) {
			
			KeyBindingProperty property;

	        for (KeyBinding key : Minecraft.getMinecraft().gameSettings.keyBindings) {
			
		        if (data[0].equals("key_" + key.getKeyDescription())) {
		        	
		        	property = KeyBindingProperty.get(key);
		        	
			        if (data[1].indexOf('&') != - 1) {
			        	
			            String[] keySettings = data[1].split("&");
			            
			            property.setKeyModifierAndCode(EnumKeyModifier.valueFromString(keySettings[1]), Integer.parseInt(keySettings[0]));
			        } 
			        
			        else {
			        	
			        	property.setKeyModifierAndCode(EnumKeyModifier.NONE, Integer.parseInt(data[1]));
			        }
		        }
	        }
		}
        
        return false;
	}
	
	public static boolean saveControlsToOptionsFile(PrintWriter writer) {
		
		if (Minecraft.getMinecraft().gameSettings != null) {
			
			KeyBindingProperty property;
			
	        for (KeyBinding key : Minecraft.getMinecraft().gameSettings.keyBindings) {
	        	
	        	property = KeyBindingProperty.get(key);
		
		        String keyString = "key_" + key.getKeyDescription() + ":" + key.getKeyCode();
		        	        
		        writer.println(property.getKeyModifier() != EnumKeyModifier.NONE ? keyString + "&" + property.getKeyModifier().toString() : keyString);
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

            	if (key != null)	        	
            		key.pressed = state;  
            }
        }
	}
	
	public static boolean isKeyPressed(KeyBinding key) {
				
		return KeyBindingProperty.get(key).isKeyDown();
	}
}
