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
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.arboriculture.ModuleArboriculture;
import forestry.arboriculture.blocks.BlockAbstractLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.items.ItemBlockForestry;
import forestry.core.items.definitions.IColoredItem;
import forestry.core.utils.Translator;

public class ItemBlockLeaves extends ItemBlockForestry<BlockAbstractLeaves> implements IColoredItem {

	public ItemBlockLeaves(BlockAbstractLeaves block) {
		super(block);
	}

	@Override
	public Component getName(ItemStack itemstack) {
		if (!itemstack.hasTag()) {
			return Component.translatable("trees.grammar.leaves.type");
		}

		TileLeaves tileLeaves = new TileLeaves(BlockPos.ZERO, getBlock().defaultBlockState());
		tileLeaves.load(itemstack.getTag());

		ITree tree = tileLeaves.getTree();
		if (tree == null) {
			return Component.translatable("for.leaves.corrupted");
		}
		return getDisplayName(tree);
	}

	public static Component getDisplayName(ITree tree) {
		IAlleleTreeSpecies primary = (IAlleleTreeSpecies) tree.getGenome().getPrimary();
		String customTreeKey = "for.trees.custom.leaves." + primary.getSpeciesIdentifier();
		return Translator.tryTranslate(customTreeKey, () -> {
			Component leaves = Component.translatable("for.trees.grammar.leaves.type");
			return Component.translatable("for.trees.grammar.leaves", primary.getDisplayName(), leaves);
		});
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
		if (itemStack.getTag() == null) {
			return ModuleArboriculture.proxy.getFoliageColorDefault();
		}

		TileLeaves tileLeaves = new TileLeaves(BlockPos.ZERO, getBlock().defaultBlockState());
		tileLeaves.load(itemStack.getTag());

		if (renderPass == BlockAbstractLeaves.FRUIT_COLOR_INDEX) {
			return tileLeaves.getFruitColour();
		} else {
			Player player = Minecraft.getInstance().player;
			return tileLeaves.getFoliageColour(player);
		}
	}

}
