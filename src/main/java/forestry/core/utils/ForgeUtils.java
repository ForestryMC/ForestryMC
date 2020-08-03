package forestry.core.utils;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ForgeUtils {

    private ForgeUtils() {
    }

    public static void registerSubscriber(Object target) {
        modBus().register(target);
    }

    public static void postEvent(Event event) {
        modBus().post(event);
    }

    public static IEventBus modBus() {
        return FMLJavaModLoadingContext.get().getModEventBus();
    }
}
