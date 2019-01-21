package austeretony.keycombs.common.main;

import austeretony.keycombs.client.reference.ClientReference;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

public enum EnumChatMessages {

    UPDATE_MESSAGE {

        @Override
        public void showMessage(String... args) {
            ChatMessageComponent 
            modPrefix = new ChatMessageComponent().addText("[Key Combinations] "),
            msg1, msg2, msg3;
            modPrefix.setColor(EnumChatFormatting.AQUA);      
            msg1 = new ChatMessageComponent().addKey("keycombs.update.newVersion");
            msg2 = new ChatMessageComponent().addText(" [" + KeyCombinationsMain.VERSION + "/" + args[0] + "]");        
            ClientReference.showChatMessageClient(modPrefix.appendComponent(msg1).appendComponent(msg2));
            msg1 = new ChatMessageComponent().addKey("keycombs.update.projectPage");
            msg2 = new ChatMessageComponent().addText(": ");
            msg3 = new ChatMessageComponent().addText(KeyCombinationsMain.PROJECT_URL);   
            msg1.setColor(EnumChatFormatting.AQUA);      
            msg3.setColor(EnumChatFormatting.WHITE);                             
            ClientReference.showChatMessageClient(msg1.appendComponent(msg2).appendComponent(msg3));   
        }
    };

    public abstract void showMessage(String... args);
}
