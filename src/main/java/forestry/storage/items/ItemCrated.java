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

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.ItemGroups;
import forestry.core.items.ItemForestry;
import forestry.core.items.definitions.IColoredItem;
import forestry.core.utils.ItemStackUtil;

public class ItemCrated extends ItemForestry implements IColoredItem {
	private final ItemStack contained;
	@Nullable
	private final String oreDictName;

	public ItemCrated(ItemStack contained, @Nullable String oreDictName) {
		super(ItemGroups.tabStorage);
		this.contained = contained;
		this.oreDictName = oreDictName;
	}

	public ItemStack getContained() {
		return contained;
	}

	@Nullable
	public String getOreDictName() {
		return oreDictName;
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack heldItem = playerIn.getItemInHand(handIn);
		if (!worldIn.isClientSide) {
			if (contained.isEmpty() || heldItem.isEmpty()) {
				return ActionResult.pass(heldItem);
			}

			heldItem.shrink(1);

			ItemStack dropStack = contained.copy();
			dropStack.setCount(9);
			ItemStackUtil.dropItemStackAsEntity(dropStack, worldIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), 40);
		}
		return ActionResult.success(heldItem);
	}

	@Override
	public ITextComponent getName(ItemStack itemstack) {
		if (contained.isEmpty()) {
			return new TranslationTextComponent("item.forestry.crate");
		} else {
			ITextComponent containedName = contained.getHoverName();
			return new TranslationTextComponent("for.item.crated.grammar", containedName);
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
		int color = colors.getColor(contained, renderPass);
		return color;
	}

}
