package twelvefold.twelvefoldbooter;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import twelvefold.twelvefoldbooter.api.TwelvefoldRegistryAPI;
import twelvefold.twelvefoldbooter.config.TwelvefoldConfig;

//@Mod(modid = TwelvefoldBooter.MODID, useMetadata = true)
public class TwelvefoldBooter extends DummyModContainer
{
    public TwelvefoldBooter()
    {
        super(new ModMetadata());
        ModMetadata metadata=this.getMetadata();
        metadata.modId=MODID;
        metadata.name=NAME;
    }
    public static final String MODID = "twelvefoldbooter";
    public static final String NAME = "Twelvefold Booter";

	@Instance(MODID)
    @SuppressWarnings("unused")
	public static TwelvefoldBooter instance;
    @Subscribe
    public void preInit(FMLPreInitializationEvent event)
    {
        for(String unfavoredModId: TwelvefoldConfig.getMods())
        {
            if(Loader.isModLoaded(unfavoredModId))
                throw new ReportedException(CrashReport.makeCrashReport(new IllegalArgumentException(),
                        String.format("Conflicting mod detected:%s",unfavoredModId)));
        }
        for (String unfavoredPackage: TwelvefoldConfig.getPackages()) {
            if (TwelvefoldRegistryAPI.isPackageLoaded(unfavoredPackage)) {
                throw new ReportedException(CrashReport.makeCrashReport(new IllegalArgumentException(),
                        String.format("Conflicting package detected:%s",unfavoredPackage)));
            }
        }
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
}