package twelvefold.twelvefoldbooter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import twelvefold.twelvefoldbooter.TwelvefoldBooter;
import twelvefold.twelvefoldbooter.coremod.TwelvefoldPlugin;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TwelvefoldConfig {
    private static TwelvefoldConfig instance=null;
    private ModConfig modConfig = new ModConfig(true,"https://twelvefold.github.io/twelvefoldbooter.json",new String[0],new String[0]);
    private TwelvefoldConfig(File minecraftHome)
    {
        readConfig(minecraftHome);
    }
    @SuppressWarnings({"unchecked"})
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
        if(modConfig.fetchFromOnline)
        {
            readOnlineConfig(minecraftHome, modConfig.onlineConfigUrl);
        }
        //localConfig.merge(defaultConfig);
    }

    private static byte[] readInputStream(InputStream inputStream) throws IOException{
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        byte[] buffer=new byte[4096];
        int read;
        while ((read=inputStream.read(buffer))!=-1)
        {
            byteArrayOutputStream.write(buffer,0,read);
        }
        return byteArrayOutputStream.toByteArray();
    }
    private void readOnlineConfig(File minecraftHome,String url)
    {
        Gson gson=new Gson();
        File cacheFile=new File(minecraftHome,TwelvefoldBooter.MODID+"_onlinecache.json");
        try{
            URLConnection connection=new URL(url).openConnection();
            if(connection instanceof HttpsURLConnection)
            {
                SSLContext context=SSLContext.getInstance("TLSv1.2");
                context.init(null,null,new SecureRandom());
                ((HttpsURLConnection) connection).setSSLSocketFactory(context.getSocketFactory());
            }
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);

            if(!(connection instanceof HttpURLConnection))
            {
                throw new IOException("Unknown protocol:"+connection.getURL().toString().split(":")[0]);
            }
            if(((HttpURLConnection) connection).getResponseCode() != 200)
            {
                throw new IOException("Invalid response code: "+((HttpURLConnection) connection).getResponseCode());
            }
            byte[] configBytes=readInputStream(connection.getInputStream());
            modConfig=gson.fromJson(new String(configBytes,StandardCharsets.UTF_8), ModConfig.class);
            try (FileOutputStream ofstream=new FileOutputStream(cacheFile)){
                ofstream.write(configBytes);
            }
            return;
        }catch (Exception e)
        {
            TwelvefoldPlugin.LOGGER.error("Failed to load online config file",e);
        }
        if(cacheFile.isFile()){
            try (FileInputStream ifstream=new FileInputStream(cacheFile)){
                modConfig=gson.fromJson(new InputStreamReader(ifstream,StandardCharsets.UTF_8), ModConfig.class);
            }catch (Exception e)
            {
                TwelvefoldPlugin.LOGGER.error("Failed to load cached online config file", e);
            }
        }
    }
    public static void init(File minecraftHome)
    {
        if(instance != null)
            return;
        instance=new TwelvefoldConfig(minecraftHome);
    }
    public static List<String> getPackages()
    {
        return instance.modConfig.incompatiblePackages;
    }
    public static List<String> getMods()
    {
        return instance.modConfig.incompatibleMods;
    }
    private static class ModConfig{
        public final boolean fetchFromOnline;
        public final String onlineConfigUrl;
        public final List<String> incompatiblePackages;
        public final List<String> incompatibleMods;
        public ModConfig(boolean fetchFromOnline,String onlineConfigUrl,String[] defaultPackages,String[] defaultModids)
        {
            this.fetchFromOnline=fetchFromOnline;
            this.onlineConfigUrl=onlineConfigUrl;
            this.incompatiblePackages =new ArrayList<>(Arrays.asList(defaultPackages));
            this.incompatibleMods =new ArrayList<>(Arrays.asList(defaultModids));
        }
        @Override
        public Object clone()
        {
            return new ModConfig(fetchFromOnline,onlineConfigUrl, incompatiblePackages.toArray(new String[0]), incompatibleMods.toArray(new String[0]));
        }
        public void merge(ModConfig other)
        {
            this.incompatiblePackages.addAll(other.incompatiblePackages);
            this.incompatibleMods.addAll(other.incompatibleMods);
        }
    }
}
