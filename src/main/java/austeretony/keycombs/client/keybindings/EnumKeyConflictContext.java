package austeretony.keycombs.client.keybindings;

import austeretony.keycombs.client.reference.ClientReference;

public enum EnumKeyConflictContext {

    UNIVERSAL,
    GUI,
    IN_GAME;

    public boolean isActive() {
        switch (this) {
        case UNIVERSAL:
            return true;
        case GUI:
            return ClientReference.getMinecraft().currentScreen != null;
        case IN_GAME:
            return !GUI.isActive();
        }
        return false;
    }

    public boolean conflicts(EnumKeyConflictContext other) {
        switch (this) {
        case UNIVERSAL:
            return true;
        case GUI:
            return this == other;
        case IN_GAME:
            return this == other;
        }
        return false;
    }
}