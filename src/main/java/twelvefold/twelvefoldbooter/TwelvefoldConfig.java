package twelvefold.twelvefoldbooter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TwelvefoldConfig {
    private static TwelvefoldConfig instance=null;
    private static final ModConfig defaultConfig=new ModConfig(new String[]{"zone.rong", "com.cleanroommc", "gkappa", "keletu", "fermiumbooter"},
            new String[]{"fermiumbooter", "mixinbooter", "configanytime"});
    private ModConfig localConfig = (ModConfig) defaultConfig.clone();
    private TwelvefoldConfig(File minecraftHome)
    {
        readConfig(minecraftHome);
    }
    @SuppressWarnings({"unchecked"})
    private void readConfig(File minecraftHome) {
        File configFile=new File(minecraftHome,TwelvefoldBooter.MODID+".json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if(configFile.isFile())
        {
            try {
                FileInputStream fileInputStream=new FileInputStream(configFile);
                localConfig=gson.fromJson(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8),ModConfig.class);
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
        localConfig.merge(defaultConfig);
    }

    public static void init(File minecraftHome)
    {
        if(instance != null)
            return;
        instance=new TwelvefoldConfig(minecraftHome);
    }
    public static List<String> getPackages()
    {
        return instance.localConfig.packages;
    }
    public static List<String> getMods()
    {
        return instance.localConfig.modids;
    }
    private static class ModConfig{
        public final List<String> packages;
        public final List<String> modids;
        public ModConfig(String[] defaultPackages,String[] defaultModids)
        {
            this.packages=new ArrayList<>(Arrays.asList(defaultPackages));
            this.modids=new ArrayList<>(Arrays.asList(defaultModids));
        }
        @Override
        public Object clone()
        {
            return new ModConfig(packages.toArray(new String[0]), modids.toArray(new String[0]));
        }
        public void merge(ModConfig other)
        {
            this.packages.addAll(other.packages);
            this.modids.addAll(other.modids);
        }
    }
}
