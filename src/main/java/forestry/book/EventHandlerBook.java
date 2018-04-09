package forestry.book;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import forestry.core.config.Config;

public class EventHandlerBook {

	private static final String HAS_BOOK = "forestry.spawned_book";

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (Config.spawnWithBook) {
			NBTTagCompound playerData = event.player.getEntityData();
			NBTTagCompound data = playerData.hasKey(EntityPlayer.PERSISTED_NBT_TAG) ? playerData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG) : new NBTTagCompound();

			if (!data.getBoolean(HAS_BOOK)) {
				ItemHandlerHelper.giveItemToPlayer(event.player, new ItemStack(ModuleBook.getItems().book));
				data.setBoolean(HAS_BOOK, true);
				playerData.setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
			}
		}
	}
}
