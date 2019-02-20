package austeretony.keycombs.client.keybindings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import austeretony.keycombs.client.reference.ClientReference;
import austeretony.keycombs.common.main.KeyCombinationsMain;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindingWrapper {

    private static final Map<KeyBinding, KeyBindingWrapper> PROPERTIES_BY_KEYBINDINGS = new HashMap<KeyBinding, KeyBindingWrapper>();

    private static final Multimap<EnumKeyModifier, KeyBindingWrapper> KEY_MODIFIERS = HashMultimap.<EnumKeyModifier, KeyBindingWrapper>create();

    private final KeyBinding keyBinding;

    private final EnumKeyModifier defaultKeyModifier;

    private EnumKeyModifier keyModifier;

    private EnumKeyConflictContext keyConflictContext;

    private KeyBindingWrapper(KeyBinding keyBinding) {
        this.keyBinding = keyBinding;
        this.defaultKeyModifier = this.keyModifier = EnumKeyModifier.NONE;       		
        this.keyConflictContext = EnumKeyConflictContext.UNIVERSAL;
        PROPERTIES_BY_KEYBINDINGS.put(this.keyBinding, this);
        KEY_MODIFIERS.put(this.keyModifier, this);
    }

    public static void create(KeyBinding keyBinding) {
        new KeyBindingWrapper(keyBinding);
    }

    public static KeyBindingWrapper get(KeyBinding keyBinding) {
        return PROPERTIES_BY_KEYBINDINGS.get(keyBinding);
    }

    public EnumKeyModifier getDefaultKeyModifier() {
        return this.defaultKeyModifier;
    }

    public EnumKeyModifier getKeyModifier() {
        return this.keyModifier;
    }

    public void setKeyModifier(EnumKeyModifier keyModifier) {
        this.keyModifier = keyModifier;
    }

    public EnumKeyConflictContext getKeyConflictContext() {
        return this.keyConflictContext;
    }

    public void setKeyConflictContext(EnumKeyConflictContext keyConflictContext) {
        this.keyConflictContext = keyConflictContext;
    }

    public KeyBinding getKeyBinding() {
        return this.keyBinding;
    }

    public void setKeyModifierAndCode(EnumKeyModifier keyModifier, int keyCode) {
        this.getKeyBinding().setKeyCode(keyCode);
        if (keyModifier.match(keyCode))       	
            keyModifier = EnumKeyModifier.NONE;
        KEY_MODIFIERS.remove(this.getKeyModifier(), this);
        KeyBinding.hash.removeObject(keyCode);
        this.setKeyModifier(keyModifier);
        KEY_MODIFIERS.put(keyModifier, this);
        KeyBinding.hash.addKey(keyCode, this.getKeyBinding());
    }

    public void setToDefault() {
        setKeyModifierAndCode(this.getDefaultKeyModifier(), this.getKeyBinding().getKeyCodeDefault());
    }

    public boolean isSetToDefaultValue() {
        return this.getKeyBinding().getKeyCode() == this.getKeyBinding().getKeyCodeDefault() && this.getKeyModifier() == this.getDefaultKeyModifier();
    }

    public boolean isKeyDown() {
        EnumKeyConflictContext conflictContext = this.getKeyConflictContext();
        return this.getKeyBinding().pressed && conflictContext.isActive() && this.getKeyModifier().isActive(conflictContext);
    }

    public static KeyBinding lookupActive(int keyCode) {
        EnumKeyModifier activeModifier = EnumKeyModifier.getActiveModifier();
        if (!activeModifier.match(keyCode)) {
            KeyBinding key = getBinding(keyCode, activeModifier);
            if (key != null)          	
                return key;
        }
        return getBinding(keyCode, EnumKeyModifier.NONE);
    }

    private static KeyBinding getBinding(int keyCode, EnumKeyModifier keyModifier) {
        Collection<KeyBindingWrapper> propertties = KEY_MODIFIERS.get(keyModifier);
        if (propertties != null) {
            for (KeyBindingWrapper property : propertties) {
                if (property.isActiveAndMatch(keyCode))               	
                    return property.getKeyBinding();
            }
        }
        return null;
    }

    public static List<KeyBinding> lookupAll(int keyCode) {
        List<KeyBinding> matchingBindings = new ArrayList<KeyBinding>();
        for (KeyBindingWrapper property : KEY_MODIFIERS.values()) {
            if (property.getKeyBinding().getKeyCode() == keyCode)           	
                matchingBindings.add(property.getKeyBinding());
        }
        return matchingBindings;
    }

    public boolean isActiveAndMatch(int keyCode) {
        EnumKeyConflictContext conflictContext = this.getKeyConflictContext();
        return keyCode != 0 && keyCode == this.getKeyBinding().getKeyCode() && conflictContext.isActive() && this.getKeyModifier().isActive(conflictContext);
    }

    public boolean conflicts(KeyBindingWrapper other) {
        if (this.getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(this.getKeyConflictContext())) {
            EnumKeyModifier 
            keyModifier = this.getKeyModifier(),
            otherKeyModifier = other.getKeyModifier();
            if (keyModifier.match(other.getKeyBinding().getKeyCode()) || otherKeyModifier.match(this.getKeyBinding().getKeyCode()))        	
                return true;
            else if (this.getKeyBinding().getKeyCode() == other.getKeyBinding().getKeyCode())            	
                return keyModifier == otherKeyModifier || (this.getKeyConflictContext().conflicts(EnumKeyConflictContext.IN_GAME) && (keyModifier == EnumKeyModifier.NONE || otherKeyModifier == EnumKeyModifier.NONE));
        }
        return false;
    }

    public boolean hasKeyCodeModifierConflict(KeyBindingWrapper other) {
        if (this.getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(this.getKeyConflictContext())) {
            if (this.getKeyModifier().match(other.getKeyBinding().getKeyCode()) || other.getKeyModifier().match(this.getKeyBinding().getKeyCode()))         	
                return true;
        }
        return false;
    }

    public String getDisplayName() {
        return this.getKeyModifier().getLocalizedName(this.getKeyBinding().getKeyCode());
    }

    public static void setKeysConflictContext() {
        GameSettings gameSetings = ClientReference.getGameSettings();
        EnumKeyConflictContext inGame = EnumKeyConflictContext.IN_GAME;
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindForward).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindLeft).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindBack).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindRight).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindJump).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindSneak).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindSprint).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindAttack).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindChat).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindPlayerList).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindCommand).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindTogglePerspective).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(gameSetings.keyBindSmoothCamera).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(KeyCombinationsMain.keyBindingHideHUD).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(KeyCombinationsMain.keyBindingDebugScreen).setKeyConflictContext(inGame);
        PROPERTIES_BY_KEYBINDINGS.get(KeyCombinationsMain.keyBindingDisableShader).setKeyConflictContext(inGame);
    }
}