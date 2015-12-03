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
package forestry.apiculture.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockTorch;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.Tabs;
import forestry.apiculture.tiles.TileCandle;
import forestry.core.config.Constants;
import forestry.core.render.TextureManager;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginApiculture;

public class BlockStump extends BlockTorch {

	public BlockStump() {
		super();
		this.setHardness(0.0F);
		this.setStepSound(soundTypeWood);
		setCreativeTab(Tabs.tabApiculture);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = TextureManager.registerTex(register, StringUtil.cleanBlockName(this));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float facingX, float facingY, float facingZ) {

		ItemStack held = player.getCurrentEquippedItem();
		if (held != null &&
				(held.getItem() == Items.flint_and_steel ||
						held.getItem() == Items.flint ||
						ItemStackUtil.equals(Blocks.torch, held))) {
			world.setBlock(x, y, z, PluginApiculture.blocks.candle, world.getBlockMetadata(x, y, z) | 0x08, Constants.FLAG_BLOCK_SYNCH);
			TileCandle tc = new TileCandle();
			tc.setColour(0); // default to white
			world.setTileEntity(x, y, z, tc);
			return true;
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int par1) {
		return 0xee0000;
	}

	@Override
	public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {
	}
}
