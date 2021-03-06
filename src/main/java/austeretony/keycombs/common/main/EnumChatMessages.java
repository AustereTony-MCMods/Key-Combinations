package austeretony.keycombs.common.main;

import austeretony.keycombs.client.reference.ClientReference;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public enum EnumChatMessages {

    UPDATE_MESSAGE;

    public static final IChatComponent PREFIX;

    static {
        PREFIX = new ChatComponentText("[" + KeyCombinationsMain.NAME + "] ");
        PREFIX.getChatStyle().setColor(EnumChatFormatting.AQUA);                   
    }

    private static IChatComponent prefix() {
        return PREFIX.createCopy();
    }

    public void showMessage(String... args) {
        IChatComponent msg1, msg2, msg3;
        switch (this) {
        case UPDATE_MESSAGE:
            msg1 = new ChatComponentTranslation("keycombs.update.newVersion");
            msg2 = new ChatComponentText(" [" + KeyCombinationsMain.VERSION + " / " + args[0] + "]") ;        
            ClientReference.showChatMessageClient(prefix().appendSibling(msg1).appendSibling(msg2));
            msg1 = new ChatComponentTranslation("keycombs.update.projectPage");
            msg2 = new ChatComponentText(": ");
            msg3 = new ChatComponentText(KeyCombinationsMain.PROJECT_LOCATION);   
            msg1.getChatStyle().setColor(EnumChatFormatting.AQUA);      
            msg3.getChatStyle().setColor(EnumChatFormatting.WHITE);                             
            msg3.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, KeyCombinationsMain.PROJECT_URL));             
            ClientReference.showChatMessageClient(msg1.appendSibling(msg2).appendSibling(msg3));
            break;
        }
    }
}
