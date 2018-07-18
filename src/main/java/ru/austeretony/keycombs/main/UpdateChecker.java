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

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class UpdateChecker {

	@ForgeSubscribe
	public void onPlayerJoinedWorld(EntityJoinWorldEvent event) {
		
		if (event.world.isRemote && event.entity instanceof EntityPlayer)					
			this.checkForUpdate();
	}
	
	private void checkForUpdate() {
							
		try {
			
			URL versionsURL = new URL(KeyCombinationsMain.VERSIONS_URL);
			
			InputStream inputStream;
			
			try {
				
				inputStream = versionsURL.openStream();
			}
			
			catch (UnknownHostException exception) {
														
				KeyCombinationsMain.LOGGER.println("[Key Combinations][ERROR] Update check failed, no internet connection.");
				
				return;
			}
			
            JsonObject remoteData = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8"));  			
			
            inputStream.close();
            
            JsonObject data;  
            
            try {
            	
            	data = remoteData.get(KeyCombinationsMain.GAME_VERSION).getAsJsonObject();      
            }
            
            catch (NullPointerException exception) {
            	
            	KeyCombinationsMain.LOGGER.println("[Key Combinations][ERROR] Update check failed, remote data is undefined for " + KeyCombinationsMain.GAME_VERSION + " version.");
            	
            	return;
            }
               
            String availableVersion = data.get("available").getAsString();
            
            if (this.compareVersions(KeyCombinationsMain.VERSION, availableVersion))	 	            
            	Minecraft.getMinecraft().thePlayer.addChatMessage("[Key Combinations] " + I18n.getString("keycombs.update.newVersion") + " [" + KeyCombinationsMain.VERSION + "/" + availableVersion + "]");
		}
		
		catch (MalformedURLException exception) {
			
			exception.printStackTrace();
		}
		
		catch (FileNotFoundException exception) {
			
			KeyCombinationsMain.LOGGER.println("[Key Combinations][ERROR] Update check failed, remote file is absent.");			
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
