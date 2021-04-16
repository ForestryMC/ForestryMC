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

import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.blocks.BlockAbstractLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.items.ItemBlockForestry;
import forestry.core.items.definitions.IColoredItem;
import forestry.core.utils.ResourceUtil;

public class ItemBlockLeaves extends ItemBlockForestry<BlockAbstractLeaves> implements IColoredItem {

	public ItemBlockLeaves(BlockAbstractLeaves block) {
		super(block);
	}

	@Override
	public ITextComponent getName(ItemStack itemstack) {
		if (!itemstack.hasTag()) {
			return new TranslationTextComponent("trees.grammar.leaves.type");
		}

		TileLeaves tileLeaves = new TileLeaves();
		tileLeaves.load(tileLeaves.getBlockState(), itemstack.getTag());

		ITree tree = tileLeaves.getTree();
		if (tree == null) {
			return new TranslationTextComponent("for.leaves.corrupted");
		}
		return getDisplayName(tree);
	}

	public static ITextComponent getDisplayName(ITree tree) {
		IAlleleTreeSpecies primary = (IAlleleTreeSpecies) tree.getGenome().getPrimary();
		String customTreeKey = "for.trees.custom.leaves." + primary.getSpeciesIdentifier();
		return ResourceUtil.tryTranslate(customTreeKey, () -> {
			ITextComponent leaves = new TranslationTextComponent("for.trees.grammar.leaves.type");
			return new TranslationTextComponent("for.trees.grammar.leaves", primary.getDisplayName(), leaves);
		});
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
		if (itemStack.getTag() == null) {
			return ModuleArboriculture.proxy.getFoliageColorDefault();
		}

		TileLeaves tileLeaves = new TileLeaves();
		tileLeaves.load(tileLeaves.getBlockState(), itemStack.getTag());

		if (renderPass == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			return tileLeaves.getFruitColour();
		} else {
			PlayerEntity player = Minecraft.getInstance().player;
			return tileLeaves.getFoliageColour(player);
		}
	}

}
