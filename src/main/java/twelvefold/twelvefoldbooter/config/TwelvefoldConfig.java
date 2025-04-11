package twelvefold.twelvefoldbooter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import twelvefold.twelvefoldbooter.TwelvefoldBooter;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TwelvefoldConfig {
    private static TwelvefoldConfig instance=null;
    private ModConfig modConfig = ModConfig.getDefaultConfig();
    private TwelvefoldConfig(File minecraftHome)
    {
        readConfig(minecraftHome);
    }
    private void readConfig(File minecraftHome) {
        File configFile=new File(minecraftHome, TwelvefoldBooter.MODID+".json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if(configFile.isFile())
        {
            try {
                FileInputStream fileInputStream=new FileInputStream(configFile);
                modConfig =gson.fromJson(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8),ModConfig.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Failed to load config file",e);
            }
        }else {
            configFile.getParentFile().mkdirs();
            try(FileOutputStream fileOutputStream=new FileOutputStream(configFile)){
                fileOutputStream.write(gson.toJson(modConfig).getBytes(StandardCharsets.UTF_8));
            }
            catch (IOException e)
            {
                throw new RuntimeException("Failed to create config file",e);
            }
        }
    }

    public static void init(File minecraftHome)
    {
        if(instance != null)
            return;
        instance=new TwelvefoldConfig(minecraftHome);
    }
    private static class ModConfig{
        //TODO mod config
        public static ModConfig getDefaultConfig()
        {
            return new ModConfig();
        }
        @Override
        public Object clone()
        {
            return new ModConfig();
        }
    }
}
