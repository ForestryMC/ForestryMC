package forestry.core.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import forestry.core.ItemGroupForestry;

import vazkii.patchouli.api.PatchouliAPI;

public class ForestersManualItem extends Item {

	public ForestersManualItem() {
		super(new ItemProperties().tab(ItemGroupForestry.tabForestry));
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (player instanceof ServerPlayerEntity) {
			PatchouliAPI.get().openBookGUI((ServerPlayerEntity) player, Registry.ITEM.getKey(this));
			player.playSound(SoundEvents.BOOK_PAGE_TURN, 1F, (float) (0.7 + Math.random() * 0.4));
		}

		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}
}
