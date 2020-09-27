package forestry.book;

import forestry.book.features.BookItems;
import forestry.core.config.Config;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

public class EventHandlerBook {

    private static final String HAS_BOOK = "forestry.spawned_book";

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (Config.spawnWithBook) {
            CompoundNBT playerData = event.getPlayer().getPersistentData();    //TODO think this is the right method
            CompoundNBT data = playerData.contains(PlayerEntity.PERSISTED_NBT_TAG)
                               ? playerData.getCompound(PlayerEntity.PERSISTED_NBT_TAG) : new CompoundNBT();

            if (!data.getBoolean(HAS_BOOK)) {
                ItemHandlerHelper.giveItemToPlayer(event.getPlayer(), BookItems.BOOK.stack());
                data.putBoolean(HAS_BOOK, true);
                playerData.put(PlayerEntity.PERSISTED_NBT_TAG, data);
            }
        }
    }
}
