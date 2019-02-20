package austeretony.keycombs.common.core;

import java.io.PrintWriter;

import org.lwjgl.input.Keyboard;

import austeretony.keycombs.client.keybindings.EnumKeyModifier;
import austeretony.keycombs.client.keybindings.KeyBindingWrapper;
import austeretony.keycombs.client.reference.ClientReference;
import austeretony.keycombs.common.main.KeyCombinationsMain;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;

public class KeyCombinationsHooks {

    public static int getQuitKeyCode() {		
        return KeyCombinationsMain.keyBindingQuit.getKeyCode();
    }

    public static boolean isQuitKeyPressed(int key) {		
        return KeyBindingWrapper.get(KeyCombinationsMain.keyBindingQuit).isActiveAndMatch(key);
    }

    public static boolean isHideHUDKeyPressed(int key) {		
        return KeyBindingWrapper.get(KeyCombinationsMain.keyBindingHideHUD).isActiveAndMatch(key);
    }

    public static int getDebugScreenKeyCode() {				
        return KeyBindingWrapper.get(KeyCombinationsMain.keyBindingDebugScreen).isActiveAndMatch(KeyCombinationsMain.keyBindingDebugScreen.getKeyCode()) ? KeyCombinationsMain.keyBindingDebugScreen.getKeyCode() : 0;
    }

    public static int getDisableShaderKeyCode() {		
        return KeyCombinationsMain.keyBindingDisableShader.isPressed() ? KeyCombinationsMain.keyBindingDisableShader.getKeyCode() : 0;
    }

    public static void wrapKeyBinding(KeyBinding keyBinding) {				
        KeyBindingWrapper.create(keyBinding);
    }

    public static void setKeyModifierAndCode(KeyBinding key, EnumKeyModifier keyModifier, int keyCode) {				
        KeyBindingWrapper.get(key).setKeyModifierAndCode(keyModifier, keyCode);
    }

    public static boolean isMineMenuKeyPressed(KeyBinding keyBinding) {
        return KeyBindingWrapper.get(keyBinding).isActiveAndMatch(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey());
    }

    public static KeyBinding resetKeyBinding(KeyBinding key, int keyCode) {		
        if (!EnumKeyModifier.isKeyCodeModifier(keyCode))
            return null;		
        return key;
    }

    public static void drawCuiControlsKeyEntry(GuiButton changeKeyButton, GuiButton resetButton, KeyBinding key, boolean flag, int par2, int par3, int par7, int par8) {		
        KeyBindingWrapper 
        property = KeyBindingWrapper.get(key),
        otherProperty;
        resetButton.xPosition = par2 + 210;
        resetButton.yPosition = par3;
        resetButton.enabled = !property.isSetToDefaultValue();
        resetButton.drawButton(ClientReference.getMinecraft(), par7, par8);                   
        changeKeyButton.width = 95;
        changeKeyButton.xPosition = par2 + 105;
        changeKeyButton.yPosition = par3;
        changeKeyButton.displayString = property.getDisplayName();       
        boolean 
        flag1 = false,
        keyCodeModifierConflict = true;		
        if (key.getKeyCode() != 0) {       	
            for (KeyBinding keyBinding : ClientReference.getKeyBindings()) {
                otherProperty = KeyBindingWrapper.get(keyBinding);
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
        changeKeyButton.drawButton(ClientReference.getMinecraft(), par7, par8);
    }

    public static void setResetButtonState(GuiButton resetAllButton) {
        boolean state = false;
        for (KeyBinding key : ClientReference.getKeyBindings()) {
            if (!KeyBindingWrapper.get(key).isSetToDefaultValue()) {
                state = true;
                break;
            }
        }
        resetAllButton.enabled = state;
    }

    public static void setToDefault(KeyBinding key) {
        KeyBindingWrapper.get(key).setToDefault();
    }

    public static void resetAllKeys() {
        KeyBindingWrapper property;
        for (KeyBinding key : ClientReference.getKeyBindings())       	        	
            KeyBindingWrapper.get(key).setToDefault();
        KeyBinding.resetKeyBindingArrayAndHash();
    }

    public static boolean loadControlsFromOptionsFile(String[] data) {
        if (ClientReference.getGameSettings() != null) {
            KeyBindingWrapper property;
            for (KeyBinding key : ClientReference.getKeyBindings()) {
                if (data[0].equals("key_" + key.getKeyDescription())) {
                    property = KeyBindingWrapper.get(key);
                    if (data[1].indexOf('&') != - 1) {
                        String[] keySettings = data[1].split("&");
                        property.setKeyModifierAndCode(EnumKeyModifier.valueFromString(keySettings[1]), Integer.parseInt(keySettings[0]));
                    } else {
                        property.setKeyModifierAndCode(EnumKeyModifier.NONE, Integer.parseInt(data[1]));
                    }
                }
            }
        }
        return false;
    }

    public static boolean saveControlsToOptionsFile(PrintWriter writer) {
        if (ClientReference.getGameSettings() != null) {
            KeyBindingWrapper property;
            for (KeyBinding key : ClientReference.getKeyBindings()) {
                property = KeyBindingWrapper.get(key);
                String keyString = "key_" + key.getKeyDescription() + ":" + key.getKeyCode();
                writer.println(property.getKeyModifier() != EnumKeyModifier.NONE ? keyString + "&" + property.getKeyModifier().toString() : keyString);
            }
        }
        return false;
    }

    public static KeyBinding lookupActive(int keyCode) {
        return KeyBindingWrapper.lookupActive(keyCode);
    }

    public static void setKeybindingsState(int keyCode, boolean state) {
        if (keyCode != 0) {
            for (KeyBinding key : KeyBindingWrapper.lookupAll(keyCode)) {
                if (key != null)	        	
                    key.pressed = state;  
            }
        }
    }

    public static boolean isKeyPressed(KeyBinding key) {
        return KeyBindingWrapper.get(key).isKeyDown();
    }
}
