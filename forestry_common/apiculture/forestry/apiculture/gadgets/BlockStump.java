/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.gadgets;

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
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.render.TextureManager;
import forestry.core.utils.StackUtils;

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
		this.blockIcon = TextureManager.getInstance().registerTex(register, this.getUnlocalizedName().replace("tile.", ""));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
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
				StackUtils.equals(Blocks.torch, held))) {
			world.setBlock(x, y, z, ForestryBlock.candle, world.getBlockMetadata(x, y, z) | 0x08, Defaults.FLAG_BLOCK_SYNCH);
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
