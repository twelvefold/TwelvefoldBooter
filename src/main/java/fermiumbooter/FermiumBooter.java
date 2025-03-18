package fermiumbooter;

import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = FermiumBooter.MODID, version = FermiumBooter.VERSION, name = FermiumBooter.NAME)
public class FermiumBooter
{
    public static final String MODID = "fermiumbooter";
    public static final String VERSION = "1.1.1";
    public static final String NAME = "FermiumBooter";
    @Config(modid = MODID)
    public static final class ModConfig{
        public static String[] pedoModIds={"mixinbooter","loliasm","naughthirium","flare",
                "gtclassic","configanytime"};
    }
	@Instance(MODID)
    @SuppressWarnings("unused")
	public static FermiumBooter instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        for(String pedoModId:ModConfig.pedoModIds)
        {
            if(Loader.isModLoaded(pedoModId))
                throw new ReportedException(CrashReport.makeCrashReport(new IllegalArgumentException(),
                        String.format("Pedophilic mod detected:%s",pedoModId)));
        }
    }

}