package austeretony.keycombs.common.main;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import austeretony.keycombs.client.reference.ClientReference;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.settings.KeyBinding;

public class KeyCombinationsKeyHandler extends KeyHandler {

    public static final KeyBinding 
    KEY_QUIT = new KeyBinding("key.quit", Keyboard.KEY_ESCAPE),
    KEY_HIDE_HUD = new KeyBinding("key.hideHUD", Keyboard.KEY_F1),
    KEY_SCREENSHOT = new KeyBinding("key.screenshot", Keyboard.KEY_F2),
    KEY_DEBUG_SCREEN = new KeyBinding("key.debugScreen", Keyboard.KEY_F3),
    KEY_TOGGLE_PERSPECTIVE = new KeyBinding("key.togglePerspective", Keyboard.KEY_F5),
    KEY_SMOOTH_CAMERA = new KeyBinding("key.smoothCamera", Keyboard.KEY_F8),
    KEY_FULLSCREEN = new KeyBinding("key.fullscreen", Keyboard.KEY_F11);

    public static void init() {
        KeyBinding[] internalKeys = new KeyBinding[]{
                KEY_QUIT, 
                KEY_HIDE_HUD, 
                KEY_SCREENSHOT,
                KEY_DEBUG_SCREEN,
                KEY_TOGGLE_PERSPECTIVE,
                KEY_SMOOTH_CAMERA,
                KEY_FULLSCREEN};
        ClientReference.registerKeyBinding(new KeyCombinationsKeyHandler(internalKeys));
    }

    public KeyCombinationsKeyHandler(KeyBinding[] keyBindings) {	
        super(keyBindings);
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {}

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {}

    @Override
    public EnumSet<TickType> ticks() {
        return null;
    }			
}
