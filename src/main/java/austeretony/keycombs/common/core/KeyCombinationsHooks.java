package austeretony.keycombs.common.core;

import java.io.PrintWriter;

import org.lwjgl.opengl.GL11;

import austeretony.keycombs.client.keybindings.EnumKeyModifier;
import austeretony.keycombs.client.keybindings.KeyBindingWrapper;
import austeretony.keycombs.client.reference.ClientReference;
import austeretony.keycombs.common.main.KeyCombinationsKeyHandler;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class KeyCombinationsHooks {

    protected static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");

    public static int getQuitKeyCode() {
        return KeyCombinationsKeyHandler.KEY_QUIT.keyCode;
    }

    public static boolean isQuitKeyPressed(int key) {
        return KeyBindingWrapper.get(KeyCombinationsKeyHandler.KEY_QUIT).isActiveAndMatches(key);
    }

    public static boolean isHideHUDKeyPressed(int key) {
        return KeyBindingWrapper.get(KeyCombinationsKeyHandler.KEY_HIDE_HUD).isActiveAndMatches(key);
    }

    public static int getScreenshotKeyCode() {
        return KeyCombinationsKeyHandler.KEY_SCREENSHOT.isPressed() ? KeyCombinationsKeyHandler.KEY_SCREENSHOT.keyCode : 0;
    }

    public static int getDebugScreenKeyCode() {
        return KeyBindingWrapper.get(KeyCombinationsKeyHandler.KEY_DEBUG_SCREEN).isActiveAndMatches(KeyCombinationsKeyHandler.KEY_DEBUG_SCREEN.keyCode) ? KeyCombinationsKeyHandler.KEY_DEBUG_SCREEN.keyCode : 0;
    }

    public static boolean isTogglePerspectiveKeyPressed(int key) {
        return KeyBindingWrapper.get(KeyCombinationsKeyHandler.KEY_TOGGLE_PERSPECTIVE).isActiveAndMatches(key);
    }

    public static boolean isSmoothCameraKeyPressed(int key) {
        return KeyBindingWrapper.get(KeyCombinationsKeyHandler.KEY_SMOOTH_CAMERA).isActiveAndMatches(key);
    }

    public static boolean isFullscreenKeyPressed(int key) {
        return KeyBindingWrapper.get(KeyCombinationsKeyHandler.KEY_FULLSCREEN).isActiveAndMatches(key);
    }

    public static void onTick(int keyCode) {
        if (keyCode != 0) {
            KeyBinding keybinding = KeyBindingWrapper.lookup(keyCode);
            if (keybinding != null)
                ++keybinding.pressTime;
        }
    }

    public static void setKeyBindState(int keyCode, boolean state) {
        if (keyCode != 0)
            for (KeyBinding key : KeyBindingWrapper.lookupAll(keyCode))
                if (key != null)	        	
                    key.pressed = state == true ? KeyBindingWrapper.get(key).isKeyDown() : state;  
    }

    public static boolean isKeyPressed(KeyBinding keyBinding) {
        return KeyBindingWrapper.get(keyBinding).isKeyDown();
    }

    public static void wrapKeyBinding(KeyBinding keyBinding) {
        KeyBindingWrapper.create(keyBinding);
    }

    public static void setKeyModifierAndCode(int selected, int keyCode) {
        KeyBindingWrapper.get(ClientReference.getKeyBindings()[selected]).setKeyModifierAndCode(EnumKeyModifier.getActiveModifier(), keyCode);
    }

    public static void drawSlot(GuiControls controls, int selected, int mouseX, int mouseY, int index, int xPosition, int yPosition, int l, Tessellator tessellator) {				
        GameSettings gameSettings = ClientReference.getGameSettings();
        KeyBindingWrapper 
        property = KeyBindingWrapper.get(gameSettings.keyBindings[index]),
        otherProperty;
        int 
        width = 95,
        height = 20;
        boolean isMouseInBoundaries = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
        int k = (isMouseInBoundaries ? 2 : 1);
        xPosition -= 20;
        ClientReference.getMinecraft().renderEngine.bindTexture(WIDGETS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        controls.drawTexturedModalRect(xPosition, yPosition, 0, 46 + k * 20, width / 2, height);
        controls.drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + k * 20, width / 2, height);
        controls.drawString(ClientReference.getMinecraft().fontRenderer, gameSettings.getKeyBindingDescription(index), xPosition + width + 4, yPosition + 6, 0xFFFFFFFF);
        boolean 
        flag = selected == index,
        conflict = false,
        keyCodeModifierConflict = true;
        for (KeyBinding keyBinding : gameSettings.keyBindings) {
            otherProperty = KeyBindingWrapper.get(keyBinding);
            if (keyBinding != gameSettings.keyBindings[index] && property.conflicts(otherProperty)) {
                conflict = true;
                keyCodeModifierConflict &= property.hasKeyCodeModifierConflict(otherProperty);
                break;
            }
        }
        String displayString = property.getDisplayName();
        if (flag)     	
            displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + displayString + EnumChatFormatting.WHITE + " <";
        else if (conflict)        	
            displayString = (keyCodeModifierConflict ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + displayString;
        controls.drawCenteredString(ClientReference.getMinecraft().fontRenderer, displayString, xPosition + (width / 2), yPosition + (height - 8) / 2, 0xFFFFFFFF);
    }

    public static boolean loadControlsFromOptionsFile(String[] data) {
        if (ClientReference.getGameSettings() != null) {
            KeyBindingWrapper property;
            for (KeyBinding key : ClientReference.getKeyBindings()) {
                if (data[0].equals("key_" + key.keyDescription)) {
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
                String keyString = "key_" + key.keyDescription + ":" + key.keyCode;
                writer.println(property.getKeyModifier() != EnumKeyModifier.NONE ? keyString + "&" + property.getKeyModifier().toString() : keyString);
            }
        }
        return false;
    }
}
