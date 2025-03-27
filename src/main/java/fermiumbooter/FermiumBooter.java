package fermiumbooter;

import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = FermiumBooter.MODID, useMetadata = true)
public class FermiumBooter
{
    public static final String MODID = "fermiumbooter";
    @Config(modid = MODID)
    public static final class ModConfig{
        public static String[] conflictingModIds ={"torcherino","projecte"};
    }
	@Instance(MODID)
    @SuppressWarnings("unused")
	public static FermiumBooter instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        for(String pedoModId:ModConfig.conflictingModIds)
        {
            if(Loader.isModLoaded(pedoModId))
                throw new ReportedException(CrashReport.makeCrashReport(new IllegalArgumentException(),
                        String.format("Conflicting mod detected:%s",pedoModId)));
        }
    }

}