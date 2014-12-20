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
package forestry.apiculture.gadgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.core.Tabs;
import forestry.core.ForestryClient;
import forestry.core.config.Defaults;
import forestry.core.render.TextureManager;
import forestry.core.utils.StackUtils;
import forestry.core.utils.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

public class BlockCandle extends BlockTorch {

	private final ArrayList<Item> lightingItems = new ArrayList<Item>();

	@SideOnly(Side.CLIENT)
	private IIcon litStump;
	@SideOnly(Side.CLIENT)
	private IIcon litTip;
	@SideOnly(Side.CLIENT)
	private IIcon unlitStump;
	@SideOnly(Side.CLIENT)
	private IIcon unlitTip;

	public BlockCandle() {
		super();
		this.setHardness(0.0F);
		this.setStepSound(soundTypeWood);
		setCreativeTab(Tabs.tabApiculture);

		lightingItems.add(Items.flint_and_steel);
		lightingItems.add(Items.flint);
		lightingItems.add(Item.getItemFromBlock(Blocks.torch));
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileCandle();
	}

	@Override
	public int getRenderType() {
		return ForestryClient.candleRenderId;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		String fileBase = StringUtil.cleanBlockName(this);
		this.blockIcon = TextureManager.getInstance().registerTex(register, "stump");
		this.litStump = TextureManager.getInstance().registerTex(register, fileBase + "StumpLit");
		this.litTip = TextureManager.getInstance().registerTex(register, fileBase + "TipLit");
		this.unlitStump = TextureManager.getInstance().registerTex(register, fileBase + "StumpUnlit");
		this.unlitTip = TextureManager.getInstance().registerTex(register, fileBase + "TipUnlit");
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return (isLit(meta))? 14 : 0;
	}

	/*@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		IIcon i = this.unlitStump;
		if (this.isLit(world.getBlockMetadata(x, y, z))) {
			i = this.litStump;
		}
		return i;
	}*/

	@SideOnly(Side.CLIENT)
	public IIcon getTextureFromPassAndMeta(int meta, int pass) {
		IIcon i;
		if (pass == 0) {
			if (isLit(meta)) {
				i = this.litTip;
			}
			else {
				i = this.unlitTip;
			}
		}
		else {
			if (isLit(meta)) {
				i = this.litStump;
			}
			else {
				i = this.unlitStump;
			}
		}
		return i;
	}

	public int getColourFromItemStack(ItemStack stack) {
		int colour = 0xffffff;
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			colour = (tag.getByte("red") << 16) | (tag.getByte("green") << 8) | tag.getByte("blue");
		}
		return colour;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
	}

	// Dye names correspond to colour values as below.
	private static final String[] dyes = {	"dyeWhite",		"dyeOrange",	"dyeMagenta",	"dyeLightBlue",
		"dyeYellow",	"dyeLime",		"dyePink",		"dyeGray",
		"dyeLightGray",	"dyeCyan",		"dyePurple",	"dyeBlue",
		"dyeBrown",		"dyeGreen",		"dyeRed",		"dyeBlack" };

	private static final int[][] colours = {	{ 255, 255, 255 } , { 219, 125,  62 } , { 255,  20, 255 } , { 107, 138, 201 } ,
		{ 255, 255,  20 } , {  20, 255,  20 } , { 208, 132, 153 } , {  74,  74,  74 } ,
		{ 154, 161, 161 } , {  20, 255, 255 } , { 126,  61, 181 } , {  20,  20, 255 } ,
		{  79,  50,  31 } , {  53,  70,  27 } , { 150,  52,  48 } , {  20,  20,  20 } };

	public static final String colourTagName = "colour";

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float facingX, float facingY, float facingZ) {
		boolean flag = false;
		int meta = world.getBlockMetadata(x, y, z);
		boolean toggleLitState = true;
		ItemStack held = player.getCurrentEquippedItem();

		if (!isLit(meta)) {
			if (held == null || !lightingItems.contains(held.getItem())) {
				toggleLitState = false;
			}
			else if (StackUtils.equals(this, held) && isLit(held)) {
				toggleLitState = true;
			}
		}

		if (held != null) {
			// Ensure a TileEntity exists. May be able to remove this in future versions.
			TileCandle te = (TileCandle)world.getTileEntity(x, y, z);
			if (te == null) {
				world.setTileEntity(x, y, z, this.createTileEntity(world, meta));
			}

			if (StackUtils.equals(this, held)) {
				if (!isLit(held)) {
					// Copy the colour of an unlit, coloured candle.
					if (held.hasTagCompound() && held.getTagCompound().hasKey(colourTagName)) {
						te.setColour(held.getTagCompound().getInteger(colourTagName));
					}
					else {
						// Reset to white if item has no
						te.setColour(0xffffff);
					}
				}
				else {
					toggleLitState = true;
				}
				flag = true;
			}
			else {
				// Check for dye-able ness.
				boolean matched = false;
				for (int i = 0; i < dyes.length; ++i) {
					for (ItemStack stack : OreDictionary.getOres(dyes[i])) {
						if (OreDictionary.itemMatches(stack, held, true)) {
							if (isLit(meta)) {
								te.setColour(colours[i][0], colours[i][1], colours[i][2]);
							}
							else {
								te.addColour(colours[i][0], colours[i][1], colours[i][2]);
							}
							world.markBlockForUpdate(x, y, z);
							matched = true;
							toggleLitState = false;
							flag = true;
							break;
						}
					}
					if (matched) {
						break;
					}
				}
			}
		}

		if (toggleLitState) {
			meta = this.toggleLitStatus(meta);
			world.setBlockMetadataWithNotify(x, y, z, meta, Defaults.FLAG_BLOCK_SYNCH | Defaults.FLAG_BLOCK_UPDATE);
			flag = true;
		}
		return flag;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if (!world.isRemote) {
			TileCandle tc = (TileCandle)world.getTileEntity(x, y, z);
			int newMeta = isLit(meta) ? 1 : 0;
			ItemStack stack = new ItemStack(this, 1, newMeta);
			if (tc != null && tc.getColour() != 0xffffff) {
				// When dropped, tag new item stack with colour. Unless it's white, then do no such thing for maximum stacking.
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger(colourTagName, tc.getColour());
				stack.setTagCompound(tag);
			}
			this.dropBlockAsItem(world, x, y, z, stack);
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
		TileCandle tc = (TileCandle)(world.getTileEntity(x, y, z));
		tc.setColour(this.getColourValueFromItemStack(itemStack));
		if (isLit(itemStack)) {
			int meta = world.getBlockMetadata(x, y, z);
			world.setBlockMetadataWithNotify(x, y, z, this.toggleLitStatus(meta), Defaults.FLAG_BLOCK_SYNCH | Defaults.FLAG_BLOCK_UPDATE);
		}
	}

	@Override
	public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7) {
		// Does nothing to prevent extra candles from falling.
	}

	@Override
	public void onBlockHarvested(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer) {
		// Does nothing to prevent extra candles from falling.
	}

	@Override
	protected boolean func_150109_e(World world, int x, int y, int z) {
		// Slightly tweaked version of BlockTorch's version to account for TE nonsense.
		if (!this.canPlaceBlockAt(world, x, y, z)) {
			if (world.getBlock(x, y, z) == this) {
				world.setBlockToAir(x, y, z);
			}
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		if (isLit(world.getBlockMetadata(x, y, z))) {
			int l = world.getBlockMetadata(x, y, z) & 0x07;
			double d0 = x + 0.5F;
			double d1 = y + 0.7F;
			double d2 = z + 0.5F;
			double d3 = 0.2199999988079071D;
			double d4 = 0.27000001072883606D;

			if (l == 1)
			{
				world.spawnParticle("smoke", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			}
			else if (l == 2)
			{
				world.spawnParticle("smoke", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			}
			else if (l == 3)
			{
				world.spawnParticle("smoke", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
			}
			else if (l == 4)
			{
				world.spawnParticle("smoke", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
			}
			else
			{
				world.spawnParticle("smoke", d0, d1, d2, 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected boolean func_150108_b(World par1World, int par2, int par3, int par4, Block block)
	{
		if (this.func_150109_e(par1World, par2, par3, par4))
		{
			int i1 = par1World.getBlockMetadata(par2, par3, par4) & 0x07;
			boolean flag = false;

			if (!par1World.isSideSolid(par2 - 1, par3, par4, ForgeDirection.EAST, true) && i1 == 1)
			{
				flag = true;
			}

			if (!par1World.isSideSolid(par2 + 1, par3, par4, ForgeDirection.WEST, true) && i1 == 2)
			{
				flag = true;
			}

			if (!par1World.isSideSolid(par2, par3, par4 - 1, ForgeDirection.SOUTH, true) && i1 == 3)
			{
				flag = true;
			}

			if (!par1World.isSideSolid(par2, par3, par4 + 1, ForgeDirection.NORTH, true) && i1 == 4)
			{
				flag = true;
			}

			if (!this.canPlaceTorchOn(par1World, par2, par3 - 1, par4) && i1 == 5)
			{
				flag = true;
			}

			if (flag)
			{
				this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
				par1World.setBlockToAir(par2, par3, par4);
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
	}

	// Yes, function hiding. Go away.
	public boolean canPlaceTorchOn(World par1World, int par2, int par3, int par4)
	{
		if (World.doesBlockHaveSolidTopSurface(par1World, par2, par3, par4))
		{
			return true;
		}
		else
		{
			Block block = par1World.getBlock(par2, par3, par4);
			return block.canPlaceTorchOnTop(par1World, par2, par3, par4);
		}
	}

	protected int getColourValueFromItemStack(ItemStack itemStack) {
		int value = 0xffffff; // default to white.
		if (itemStack.hasTagCompound()) {
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag.hasKey(colourTagName)) {
				value = tag.getInteger(colourTagName);
			}
		}
		return value;
	}

	public static boolean isLit(int meta) {
		return (meta & 0x08) > 0;
	}

	public static boolean isLit(ItemStack itemStack) {
		return itemStack.getItemDamage() > 0;
	}

	protected int toggleLitStatus(int meta) {
		return meta ^ 0x08;
	}

	public void addItemToLightingList(Item item) {
		if (item == null) throw new NullPointerException();

		if (!this.lightingItems.contains(item)) {
			this.lightingItems.add(item);
		}
	}

}
