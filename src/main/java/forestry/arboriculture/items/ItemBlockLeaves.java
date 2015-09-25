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

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.items.ItemBlockForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginArboriculture;

public class ItemBlockLeaves extends ItemBlockForestry {

	public ItemBlockLeaves(Block block) {
		super(block);
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		String type = StringUtil.localize("trees.grammar.leaves.type");
		if (!itemstack.hasTagCompound()) {
			return type;
		}

		TileLeaves tileLeaves = new TileLeaves();
		tileLeaves.readFromNBT(itemstack.getTagCompound());

		String unlocalizedName = tileLeaves.getUnlocalizedName();

		String customTreeKey = "trees.custom.leaves." + unlocalizedName.replace("trees.species.", "");
		if (StringUtil.canTranslate(customTreeKey)) {
			return StringUtil.localize(customTreeKey);
		}

		String grammar = StringUtil.localize("trees.grammar.leaves");
		String localizedName = StatCollector.translateToLocal(unlocalizedName);

		return grammar.replaceAll("%SPECIES", localizedName).replaceAll("%TYPE", type);
	}

	@Override
	public int getColorFromItemStack(ItemStack itemStack, int renderPass) {
		if (!itemStack.hasTagCompound()) {
			return PluginArboriculture.proxy.getFoliageColorBasic();
		}

		TileLeaves tileLeaves = new TileLeaves();
		tileLeaves.readFromNBT(itemStack.getTagCompound());

		return tileLeaves.getFoliageColour(Proxies.common.getPlayer());
	}

	@Override
	public boolean placeBlockAt(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		if (!itemStack.hasTagCompound()) {
			return false;
		}

		TileLeaves tileLeaves = new TileLeaves();
		tileLeaves.readFromNBT(itemStack.getTagCompound());
		tileLeaves.getTree().setLeavesDecorative(world, player.getGameProfile(), x, y, z);

		return true;
	}

}
