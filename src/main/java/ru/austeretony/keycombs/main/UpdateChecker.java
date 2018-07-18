package ru.austeretony.keycombs.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

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

public class UpdateChecker {

	@SubscribeEvent
	public void onPlayerJoinedWorld(EntityJoinWorldEvent event) {
		
		if (event.world.isRemote && event.entity instanceof EntityPlayer)					
			this.checkForUpdates();
	}
	
	private void checkForUpdates() {
							
		try {
			
			URL versionsURL = new URL(KeyCombinationsMain.VERSIONS_URL);
			
			InputStream inputStream;
			
			try {
				
				inputStream = versionsURL.openStream();
			}
			
			catch (UnknownHostException exception) {
														
				KeyCombinationsMain.LOGGER.error("Update check failed, no internet connection.");
				
				return;
			}
			
            JsonObject remoteData = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8"));  			
			
            inputStream.close();
            
            JsonObject data;  
            
            try {
            	
            	data = remoteData.get(KeyCombinationsMain.GAME_VERSION).getAsJsonObject();      
            }
            
            catch (NullPointerException exception) {
            	
            	KeyCombinationsMain.LOGGER.error("Update check failed, remote data is undefined for " + KeyCombinationsMain.GAME_VERSION + " version.");
            	
            	return;
            }
                           
            String availableVersion = data.get("available").getAsString();
            
            if (this.compareVersions(KeyCombinationsMain.VERSION, availableVersion)) {	
            	            	
            	EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            	
            	IChatComponent 
            	updateMessage = new ChatComponentText("[Key Combinations] " + I18n.format("keycombs.update.newVersion") + " [" + KeyCombinationsMain.VERSION + "/" + availableVersion + "]"),
            	pageMessage = new ChatComponentText(I18n.format("keycombs.update.projectPage") + ": "),
            	urlMessage = new ChatComponentText(KeyCombinationsMain.PROJECT_URL);
            
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
			
			KeyCombinationsMain.LOGGER.error("Update check failed, remote file is absent.");			
		}
		
		catch (IOException exception) {
			
			exception.printStackTrace();
		}
	}
	
	private boolean compareVersions(String currentVersion, String availableVersion) {
								
		String[] 
		cVer = currentVersion.split("[.]"),
		aVer = availableVersion.split("[.]");
				
		int diff;
		
		for (int i = 0; i < cVer.length; i++) {
					
			try {
				
				diff = Integer.parseInt(aVer[i]) - Integer.parseInt(cVer[i]);
												
				if (diff > 0)
					return true;
				
				if (diff < 0)
					return false;
			}
			
			catch (NumberFormatException exception) {
				
				exception.printStackTrace();
			}
		}
		
		return false;
	}
}
