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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class UpdateChecker implements Runnable {

	private static boolean notified;
	
	private static String availableVersion = KeyCombinationsMain.VERSION;
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlayerJoinedWorld(EntityJoinWorldEvent event) {
		
		if (event.entity instanceof EntityPlayer) {
			
			if (!notified) {
				
				notified = true;
				
		        if (this.compareVersions(KeyCombinationsMain.VERSION, availableVersion)) {	
	            	
		        	IChatComponent 
		        	updateMessage1 = new ChatComponentText("[Key Combinations] "),
		            updateMessage2 = new ChatComponentTranslation("keycombs.update.newVersion"),
		            updateMessage3 = new ChatComponentText(" [" + KeyCombinationsMain.VERSION + "/" + availableVersion + "]"),
		        	pageMessage1 = new ChatComponentTranslation("keycombs.update.projectPage"),
		            pageMessage2 = new ChatComponentText(": "),
		        	urlMessage = new ChatComponentText("minecraft.curseforge.com");		        
		        	updateMessage1.getChatStyle().setColor(EnumChatFormatting.AQUA);
		        	pageMessage1.getChatStyle().setColor(EnumChatFormatting.AQUA);
		        	urlMessage.getChatStyle().setColor(EnumChatFormatting.WHITE);		        	
		        	urlMessage.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, KeyCombinationsMain.PROJECT_URL));		        	
		        	((EntityPlayer) event.entity).addChatMessage(updateMessage1.appendSibling(updateMessage2).appendSibling(updateMessage3));
		        	((EntityPlayer) event.entity).addChatMessage(pageMessage1.appendSibling(pageMessage2).appendSibling(urlMessage));
		        }
			}
			
			else {
				
				MinecraftForge.EVENT_BUS.unregister(this);
			}
		}
	}

	@Override
	public void run() {

		URL versionsURL;
		
		try {
			
			versionsURL = new URL(KeyCombinationsMain.VERSIONS_URL);
		}
		
		catch (MalformedURLException exception) {
			
			exception.printStackTrace();
			
			return;
		}
		
		JsonObject remoteData;
					
		try (InputStream inputStream = versionsURL.openStream()) {
			
			remoteData = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8")); 
		}
		
		catch (UnknownHostException exception) {
			
			KeyCombinationsMain.LOGGER.error("Update check failed, no internet connection.");
			
			return;
		}
		
		catch (FileNotFoundException exception) {
			
			KeyCombinationsMain.LOGGER.error("Update check failed, remote file is absent.");
			
			return;
		}
		
		catch (IOException exception) {
						
			exception.printStackTrace();
			
			return;
		}
				        
        JsonObject data;  
        
        try {
        	
        	data = remoteData.get(KeyCombinationsMain.GAME_VERSION).getAsJsonObject();      
        }
        
        catch (NullPointerException exception) {
        	
        	KeyCombinationsMain.LOGGER.error("Update check failed, data is undefined for " + KeyCombinationsMain.GAME_VERSION + " version.");
        	
        	return;
        }
        
        availableVersion = data.get("available").getAsString();
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