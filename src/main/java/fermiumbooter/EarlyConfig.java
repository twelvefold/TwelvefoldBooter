package fermiumbooter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EarlyConfig {
    private static EarlyConfig instance=null;
    private final List<String> config;
    private EarlyConfig(File minecraftHome)
    {
        config=readConfig(minecraftHome);
    }
    @SuppressWarnings({"unchecked"})
    private List<String> readConfig(File minecraftHome) {
        File configFile=new File(minecraftHome,"fermiumbooter_earlyconfig.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<String> localConfig=new ArrayList<>();
        localConfig.add("com.sci.torcherino");
        localConfig.add("moze_intel.projecte");
        if(configFile.isFile())
        {
            try {
                FileInputStream fileInputStream=new FileInputStream(configFile);
                localConfig=gson.fromJson(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8),List.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Failed to load config file",e);
            }
        }else {
            configFile.getParentFile().mkdirs();
            try(FileOutputStream fileOutputStream=new FileOutputStream(configFile)){
                fileOutputStream.write(gson.toJson(localConfig).getBytes(StandardCharsets.UTF_8));
            }
            catch (IOException e)
            {
                throw new RuntimeException("Failed to create config file",e);
            }
        }
        return localConfig;
    }

    public static void init(File minecraftHome)
    {
        if(instance != null)
            return;
        instance=new EarlyConfig(minecraftHome);
    }
    public static List<String> getConfig()
    {
        return instance.config;
    }
}
