package twelvefold.twelvefoldbooter;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
}