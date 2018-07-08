package ru.austeretony.keycombs.event;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import ru.austeretony.keycombs.coremod.KeyCombsClassTransformer;
import ru.austeretony.keycombs.main.KeyCombsMain;

public class KeyCombsEvents {

	@SubscribeEvent
	public void onPlayerJoinedWorld(EntityJoinWorldEvent event) {
		
		if (event.world.isRemote && event.entity instanceof EntityPlayer) {
									
			this.checkForUpdates();
		}
	}
	
	private void checkForUpdates() {
					
		try {
			
			URL versionsURL = new URL(KeyCombsMain.VERSIONS_URL);
			
			InputStream inputStream = null;
			
			try {
				
				inputStream = versionsURL.openStream();
			}
			
			catch (UnknownHostException exception) {
														
				KeyCombsClassTransformer.LOGGER.error("Update check failed, no internet connection.");
				
				return;
			}
			
            JsonObject remoteData = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8"));  			
			
            inputStream.close();
            
            JsonObject data = remoteData.get(KeyCombsMain.GAME_VERSION).getAsJsonObject();
            
            String newVersion = data.get("available").getAsString();
            	            
            int 
            availableVersion = Integer.valueOf(newVersion.replace(".", "")),
            currentVersion = Integer.valueOf(KeyCombsMain.VERSION.replace(".", ""));
            
            if (currentVersion < availableVersion) {
            	
            	List<String> changelog = new ArrayList<String>();
            	
            	for (JsonElement element : data.get("changelog").getAsJsonArray()) {
            		
            		changelog.add(element.getAsString());
            	}
            	
            	EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            	
            	IChatComponent 
            	updateMessage = new ChatComponentText("[Key Combinations] " + I18n.format("keycombs.update.newVersion") + " [" + KeyCombsMain.VERSION + "/" + newVersion + "]"),
            	pageMessage = new ChatComponentText(I18n.format("keycombs.update.projectPage") + ": "),
            	urlMessage = new ChatComponentText(KeyCombsMain.PROJECT_URL);
            
            	updateMessage.getChatStyle().setColor(EnumChatFormatting.AQUA);
            	pageMessage.getChatStyle().setColor(EnumChatFormatting.AQUA);
            	urlMessage.getChatStyle().setColor(EnumChatFormatting.WHITE);
            	
            	urlMessage.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, urlMessage.getUnformattedText()));
            	
            	player.addChatMessage(updateMessage);
            	player.addChatMessage(pageMessage.appendSibling(urlMessage));
            }
		}
		
		catch (MalformedURLException exception) {
			
			exception.printStackTrace();
		}
		
		catch (FileNotFoundException exception) {
			
			KeyCombsClassTransformer.LOGGER.error("Update check failed, remote file is absent.");			
		}
		
		catch (IOException exception) {
						
			exception.printStackTrace();
		}
	}
}
