package austeretony.keycombs.common.main;

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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class UpdateChecker implements Runnable {

    private static String availableVersion = KeyCombinationsMain.VERSION;

    private static boolean notified;

    @SubscribeEvent
    public void onPlayerJoinedWorld(EntityJoinWorldEvent event) {
        if (event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
            if (!notified) {
                notified = true;
                if (this.compareVersions(KeyCombinationsMain.VERSION, availableVersion))
                    EnumChatMessages.UPDATE_MESSAGE.showMessage(availableVersion);
            } else {
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }

    @Override
    public void run() {
        URL versionsURL;                
        try {                   
            versionsURL = new URL(KeyCombinationsMain.VERSIONS_URL);
        } catch (MalformedURLException exception) {                     
            exception.printStackTrace();                        
            return;
        }
        JsonObject remoteData;                                  
        try (InputStream inputStream = versionsURL.openStream()) {                      
            remoteData = (JsonObject) new JsonParser().parse(new InputStreamReader(inputStream, "UTF-8")); 
        } catch (UnknownHostException exception) {              
            KeyCombinationsMain.LOGGER.error("Update check failed, no internet connection.");               
            return;
        } catch (FileNotFoundException exception) {                     
            KeyCombinationsMain.LOGGER.error("Update check failed, remote file is absent.");                        
            return;
        } catch (IOException exception) {                                               
            exception.printStackTrace();                        
            return;
        }                                       
        JsonObject data;          
        try {           
            data = remoteData.get(KeyCombinationsMain.GAME_VERSION).getAsJsonObject();      
        } catch (NullPointerException exception) {              
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
            } catch (NumberFormatException exception) {                         
                exception.printStackTrace();
            }
        }               
        return false;
    }
}
