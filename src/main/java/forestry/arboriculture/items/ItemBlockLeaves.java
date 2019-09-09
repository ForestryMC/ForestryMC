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
package forestry.arboriculture.items;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.blocks.BlockAbstractLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemBlockForestry;
import forestry.core.utils.Translator;

public class ItemBlockLeaves extends ItemBlockForestry<BlockAbstractLeaves> implements IColoredItem {

	public ItemBlockLeaves(BlockAbstractLeaves block) {
		super(block);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack itemstack) {
		if (itemstack.getTag() == null) {
			return new TranslationTextComponent("trees.grammar.leaves.type");
		}

		TileLeaves tileLeaves = new TileLeaves();
		tileLeaves.read(itemstack.getTag());

		String unlocalizedName = tileLeaves.getUnlocalizedName();
		return getDisplayName(unlocalizedName);
	}

	public static ITextComponent getDisplayName(String unlocalizedSpeciesName) {
		String customTreeKey = "for.trees.custom.leaves." + unlocalizedSpeciesName.replace("for.trees.species.", "");
		if (Translator.canTranslateToLocal(customTreeKey)) {
			return new TranslationTextComponent(customTreeKey);
		}

		ITextComponent localizedName = new TranslationTextComponent(unlocalizedSpeciesName);

		ITextComponent leaves = new TranslationTextComponent("for.trees.grammar.leaves.type");
		return new TranslationTextComponent("for.trees.grammar.leaves", localizedName, leaves);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
		if (itemStack.getTag() == null) {
			return ModuleArboriculture.proxy.getFoliageColorDefault();
		}

		TileLeaves tileLeaves = new TileLeaves();
		tileLeaves.read(itemStack.getTag());

		if (renderPass == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			return tileLeaves.getFruitColour();
		} else {
			PlayerEntity player = Minecraft.getInstance().player;
			return tileLeaves.getFoliageColour(player);
		}
	}

}
