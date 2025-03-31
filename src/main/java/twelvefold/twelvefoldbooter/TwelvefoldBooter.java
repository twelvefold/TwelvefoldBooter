package twelvefold.twelvefoldbooter;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
    private static final String[] incompatibleModIds={"fermiumbooter","mixinbooter","configanytime"};
    /*
    @Config(modid = MODID)
    public static final class ModConfig{
        public static String[] conflictingModIds=incompatibleModIds.clone();
    }

     */

	@Instance(MODID)
    @SuppressWarnings("unused")
	public static TwelvefoldBooter instance;
    @Subscribe
    public void preInit(FMLPreInitializationEvent event)
    {
        //List<String> unfavoredModIds=new ArrayList<>(Arrays.asList(incompatibleModIds));
        //unfavoredModIds.addAll(Arrays.asList(ModConfig.conflictingModIds));
        for(String unfavoredModId: TwelvefoldConfig.getMods())
        {
            if(Loader.isModLoaded(unfavoredModId))
                throw new ReportedException(CrashReport.makeCrashReport(new IllegalArgumentException(),
                        String.format("Conflicting mod detected:%s",unfavoredModId)));
        }
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
}