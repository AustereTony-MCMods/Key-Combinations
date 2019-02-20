package austeretony.keycombs.client.reference;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;

public class ClientReference {

    @SideOnly(Side.CLIENT)
    public static void registerKeyBinding(KeyHandler keyHandler) {
        KeyBindingRegistry.registerKeyBinding(keyHandler);          
    }

    @SideOnly(Side.CLIENT)
    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    @SideOnly(Side.CLIENT)
    public static GameSettings getGameSettings() {
        return Minecraft.getMinecraft().gameSettings;
    }

    @SideOnly(Side.CLIENT)
    public static KeyBinding[] getKeyBindings() {
        return Minecraft.getMinecraft().gameSettings.keyBindings;
    }

    @SideOnly(Side.CLIENT)
    public static EntityPlayer getClientPlayer() {
        return getMinecraft().thePlayer;
    }

    @SideOnly(Side.CLIENT)
    public static void showChatMessageClient(ChatMessageComponent chatComponent) {
        getClientPlayer().sendChatToPlayer(chatComponent);
    }
}
