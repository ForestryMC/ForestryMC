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

import com.google.common.collect.ImmutableMap;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.Tabs;
import forestry.apiculture.tiles.TileCandle;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.StringUtil;

public class BlockCandle extends BlockTorch {

	private static final ImmutableMap<String, Integer> colours;
	private static final Set<Item> lightingItems;
	public static final String colourTagName = "colour";

	static {
		colours = ImmutableMap.<String, Integer>builder()
				.put("dyeWhite", new Color(255, 255, 255).getRGB())
				.put("dyeOrange", new Color(219, 125, 62).getRGB())
				.put("dyeMagenta", new Color(255, 20, 255).getRGB())
				.put("dyeLightBlue", new Color(107, 138, 201).getRGB())
				.put("dyeYellow", new Color(255, 255, 20).getRGB())
				.put("dyeLime", new Color(20, 255, 20).getRGB())
				.put("dyePink", new Color(208, 132, 153).getRGB())
				.put("dyeGray", new Color(74, 74, 74).getRGB())
				.put("dyeLightGray", new Color(154, 161, 161).getRGB())
				.put("dyeCyan", new Color(20, 255, 255).getRGB())
				.put("dyePurple", new Color(126, 61, 181).getRGB())
				.put("dyeBlue", new Color(20, 20, 255).getRGB())
				.put("dyeBrown", new Color(79, 50, 31).getRGB())
				.put("dyeGreen", new Color(53, 70, 27).getRGB())
				.put("dyeRed", new Color(150, 52, 48).getRGB())
				.put("dyeBlack", new Color(20, 20, 20).getRGB())
				.build();

		lightingItems = new HashSet<>(Arrays.asList(
				Items.flint_and_steel,
				Items.flint,
				Item.getItemFromBlock(Blocks.torch)
		));
	}

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
		return Proxies.render.getCandleRenderId();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		String fileBase = StringUtil.cleanBlockName(this);
		this.blockIcon = TextureManager.registerTex(register, "stump");
		this.litStump = TextureManager.registerTex(register, fileBase + "StumpLit");
		this.litTip = TextureManager.registerTex(register, fileBase + "TipLit");
		this.unlitStump = TextureManager.registerTex(register, fileBase + "StumpUnlit");
		this.unlitTip = TextureManager.registerTex(register, fileBase + "TipUnlit");
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileCandle && ((TileCandle) tileEntity).isLit()) {
			return 14;
		}
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getTextureFromPassAndLit(int pass, boolean isLit) {
		IIcon i;
		if (pass == 0) {
			if (isLit) {
				i = this.litTip;
			} else {
				i = this.unlitTip;
			}
		} else {
			if (isLit) {
				i = this.litStump;
			} else {
				i = this.unlitStump;
			}
		}
		return i;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float facingX, float facingY, float facingZ) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof TileCandle)) {
			return false;
		}

		TileCandle tileCandle = (TileCandle) tileEntity;
		final boolean isLit = tileCandle.isLit();

		boolean flag = false;
		boolean toggleLitState = true;
		ItemStack heldItem = player.getCurrentEquippedItem();

		if (!isLit) {
			if (heldItem == null || !lightingItems.contains(heldItem.getItem())) {
				toggleLitState = false;
			} else if (ItemStackUtil.equals(this, heldItem) && isLit(heldItem)) {
				toggleLitState = true;
			}
		}

		if (heldItem != null) {
			if (ItemStackUtil.equals(this, heldItem)) {
				if (!isLit(heldItem)) {
					// Copy the colour of an unlit, coloured candle.
					if (heldItem.hasTagCompound() && heldItem.getTagCompound().hasKey(colourTagName)) {
						tileCandle.setColour(heldItem.getTagCompound().getInteger(colourTagName));
					} else {
						// Reset to white if item has no
						tileCandle.setColour(0xffffff);
					}
				} else {
					toggleLitState = true;
				}
				flag = true;
			} else {
				boolean dyed = tryDye(heldItem, isLit, tileCandle);
				if (dyed) {
					world.markBlockForUpdate(x, y, z);
					toggleLitState = false;
					flag = true;
				}
			}
		}

		if (toggleLitState) {
			tileCandle.setLit(!isLit);
			world.markBlockForUpdate(x, y, z);
			flag = true;
		}
		return flag;
	}

	private static boolean tryDye(ItemStack held, boolean isLit, TileCandle tileCandle) {
		// Check for dye-able ness.
		for (Map.Entry<String, Integer> colour : colours.entrySet()) {
			String colourName = colour.getKey();
			for (ItemStack stack : OreDictionary.getOres(colourName)) {
				if (OreDictionary.itemMatches(stack, held, true)) {
					if (isLit) {
						tileCandle.setColour(colour.getValue());
					} else {
						tileCandle.addColour(colour.getValue());
					}
					return true;
				}
			}
		}
		return false;
	}

	/* DROP HANDLING */
	// Hack: 	When harvesting we need to get the drops in onBlockHarvested,
	// 			because Mojang destroys the block and tile before calling getDrops.
	private final ThreadLocal<ItemStack> drop = new ThreadLocal<>();

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int metadata, EntityPlayer player) {
		if (!world.isRemote) {
			ItemStack itemStack = getCandleDrop(world, x, y, z);
			drop.set(itemStack);
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ItemStack dropStack = drop.get();
		drop.remove();

		// not harvested, get drops normally
		if (dropStack == null) {
			dropStack = getCandleDrop(world, x, y, z);
		}

		ArrayList<ItemStack> drops = new ArrayList<>(1);
		drops.add(dropStack);

		return drops;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return getCandleDrop(world, x, y, z);
	}

	private ItemStack getCandleDrop(World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof TileCandle)) {
			return null;
		}
		TileCandle tileCandle = (TileCandle) tileEntity;
		int colour = tileCandle.getColour();

		int newMeta = tileCandle.isLit() ? 1 : 0;
		ItemStack itemStack = new ItemStack(this, 1, newMeta);
		if (colour != 0xffffff) {
			// When dropped, tag new item stack with colour. Unless it's white, then do no such thing for maximum stacking.
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger(colourTagName, colour);
			itemStack.setTagCompound(tag);
		}
		return itemStack;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
		TileCandle tileCandle = (TileCandle) (world.getTileEntity(x, y, z));
		int colour = getColourValueFromItemStack(itemStack);
		boolean isLit = isLit(itemStack);
		tileCandle.setColour(colour);
		tileCandle.setLit(isLit);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileCandle && ((TileCandle) tileEntity).isLit()) {
			super.randomDisplayTick(world, x, y, z, random);
		}
	}

	private static int getColourValueFromItemStack(ItemStack itemStack) {
		int value = 0xffffff; // default to white.
		if (itemStack.hasTagCompound()) {
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag.hasKey(colourTagName)) {
				value = tag.getInteger(colourTagName);
			}
		}
		return value;
	}

	public static boolean isLit(ItemStack itemStack) {
		return itemStack.getItemDamage() > 0;
	}

	public static void addItemToLightingList(Item item) {
		if (item == null) {
			throw new NullPointerException();
		}

		if (!lightingItems.contains(item)) {
			lightingItems.add(item);
		}
	}

	public ItemStack getUnlitCandle(int amount) {
		return new ItemStack(this, amount, 0);
	}

	public ItemStack getLitCandle(int amount) {
		return new ItemStack(this, amount, 1);
	}
}
