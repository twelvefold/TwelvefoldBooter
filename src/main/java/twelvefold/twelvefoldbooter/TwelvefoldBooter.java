package twelvefold.twelvefoldbooter;

import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

//@Mod(modid = TwelvefoldBooter.MODID, useMetadata = true)
public class TwelvefoldBooter extends DummyModContainer
{
    public TwelvefoldBooter()
    {
        super(new ModMetadata());
        ModMetadata metadata=this.getMetadata();
        metadata.modId="twelvefoldbooter";
    }
    public static final String MODID = "twelvefoldbooter";
    @Config(modid = MODID)
    public static final class ModConfig{
        public static String[] conflictingModIds =new String[0];
    }
    private static final String[] incompatibleModIds={"fermiumbooter","mixinbooter","configanytime"};
	@Instance(MODID)
    @SuppressWarnings("unused")
	public static TwelvefoldBooter instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        System.arraycopy(incompatibleModIds,0,ModConfig.conflictingModIds,ModConfig.conflictingModIds.length, incompatibleModIds.length);
        for(String pedoModId:ModConfig.conflictingModIds)
        {
            if(Loader.isModLoaded(pedoModId))
                throw new ReportedException(CrashReport.makeCrashReport(new IllegalArgumentException(),
                        String.format("Conflicting mod detected:%s",pedoModId)));
        }
    }

}