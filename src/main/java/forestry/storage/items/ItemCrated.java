/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.storage.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.ItemGroups;
import forestry.core.items.ItemForestry;
import forestry.core.items.definitions.IColoredItem;
import forestry.core.utils.ItemStackUtil;

public class ItemCrated extends ItemForestry implements IColoredItem {
	private final ItemStack contained;

	/**
	 * @param contained The item which should be dropped on use, or be uncrated into
	 */
	public ItemCrated(ItemStack contained) {
		super(ItemGroups.tabStorage);
		this.contained = contained;
	}

	public ItemStack getContained() {
		return contained;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack heldItem = playerIn.getItemInHand(handIn);
		if (!worldIn.isClientSide) {
			if (contained.isEmpty() || heldItem.isEmpty()) {
				return InteractionResultHolder.pass(heldItem);
			}

			heldItem.shrink(1);

			ItemStack dropStack = contained.copy();
			dropStack.setCount(9);
			ItemStackUtil.dropItemStackAsEntity(dropStack, worldIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), 40);
		}
		return InteractionResultHolder.success(heldItem);
	}

	@Override
	public Component getName(ItemStack itemstack) {
		if (contained.isEmpty()) {
			return Component.translatable("item.forestry.crate");
		} else {
			Component containedName = contained.getHoverName();
			return Component.translatable("for.item.crated.grammar", containedName);
		}
	}

	//TODO I think this needs ItemOverrides or something?
	//	@OnlyIn(Dist.CLIENT)
	//	@Override
	//	public void registerModel(Item item, IModelManager manager) {
	//		if (contained.isEmpty()) {
	//			manager.registerItemModel(item, 0);
	//			manager.registerItemModel(item, 1, "crate-filled");
	//		} else {
	//			ResourceLocation location = Preconditions.checkNotNull(getRegistryName());
	//			ModelResourceLocation modelLocation = new ModelResourceLocation("forestry:crate-filled", location.getPath());
	//			//			ModelLoader.setCustomModelResourceLocation(item, 0, modelLocation);
	//			//			ModelBakery.registerItemVariants(item, modelLocation);
	//		}
	//	}


	@Override
	@OnlyIn(Dist.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		ItemColors colors = Minecraft.getInstance().getItemColors();

		if (contained.isEmpty() || renderPass == 100) {
			return -1;
		}

		return colors.getColor(contained, renderPass);
	}
}
