package forestry.core.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;

import forestry.core.ItemGroupForestry;

import vazkii.patchouli.api.PatchouliAPI;

public class ForestersManualItem extends Item {

	public ForestersManualItem() {
		super(new ItemProperties().tab(ItemGroupForestry.tabForestry));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (player instanceof ServerPlayer) {
			PatchouliAPI.get().openBookGUI((ServerPlayer) player, Registry.ITEM.getKey(this));
			player.playSound(SoundEvents.BOOK_PAGE_TURN, 1F, (float) (0.7 + Math.random() * 0.4));
		}

		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}
}
