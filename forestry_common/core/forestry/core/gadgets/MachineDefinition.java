/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gadgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.core.interfaces.IBlockRenderer;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;

/**
 * This will replace the whole {@link Gadget}-nonsense at some point, using the proper Forge hook. Will hopefully sort out any issues with interfaces.
 */
public class MachineDefinition {

	public final Class<? extends TileEntity> teClass;

	public final String teIdent;
	public Block block;
	public final int meta;

	public final IBlockRenderer renderer;

	/* CRAFTING */
	public IRecipe[] recipes;

	public MachineDefinition(int meta, String teIdent, Class<? extends TileEntity> teClass, IRecipe... recipes) {
		this(meta, teIdent, teClass, null, recipes);
	}

	public MachineDefinition(int meta, String teIdent, Class<? extends TileEntity> teClass, IBlockRenderer renderer, IRecipe... recipes) {
		this.meta = meta;
		this.teIdent = teIdent;
		this.teClass = teClass;
		this.renderer = renderer;

		this.recipes = recipes;

		this.faceMap = new int[8];
		for (int i = 0; i < 8; i++)
			faceMap[i] = 0;

	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public void register() {
		registerTileEntity();
		registerCrafting();
		if (renderer != null)
			Proxies.render.registerTESR(this);
	}

	@SuppressWarnings("unchecked")
	private void registerCrafting() {
		for (IRecipe recipe : recipes)
			if(recipe != null)
				CraftingManager.getInstance().getRecipeList().add(recipe);
	}

	/**
	 * Registers the tile entity with MC.
	 */
	private void registerTileEntity() {
		GameRegistry.registerTileEntity(teClass, teIdent);
	}

	public TileEntity createMachine() {
		try {
			return teClass.getConstructor().newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to instantiate tile entity of class " + teClass.getName());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, meta));
	}

	/* BLOCK DROPS */
	public boolean handlesDrops() {
		return false;
	}

	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList<ItemStack>();
	}

	/* INTERACTION */
	public boolean isSolidOnSide(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fXplayerClick, float fY, float fZ) {
		return false;
	}

	public void onBlockAdded(World world, int x, int y, int z) {
	}

	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		return true;
	}

	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return false;
	}

	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		return false;
	}

	/* TEXTURES */
	private final int[] faceMap;

	public MachineDefinition setFaces(int... faces) {

		if (faces.length > 6)
			for (int i = 0; i < faces.length; i++)
				faceMap[i] = faces[i];
		else {
			for (int i = 0; i < 6; i++)
				faceMap[i] = faces[i];
			faceMap[6] = faces[0];
			faceMap[7] = faces[1];
		}

		return this;
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		icons = new IIcon[8];

		for (int i = 0; i < 8; i++)
			icons[i] = TextureManager.getInstance().registerTex(register, teIdent.replace("forestry.", "").toLowerCase(Locale.ENGLISH) + "." + faceMap[i]);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getBlockTextureFromSideAndMetadata(int side, int metadata) {
		return icons[side];
	}

	/**
	 * 0 - Bottom 1 - Top 2 - Back 3 - Front 4,5 - Sides, 7 - Reversed ?, 8 - Reversed ?
	 */
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side, int metadata) {

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileForestry))
			return getBlockTextureFromSideAndMetadata(side, metadata);

		ForgeDirection dir = ((TileForestry) tile).getOrientation();
		switch (dir) {
		case WEST:
			side = side == 2 ? 4 : side == 3 ? 5 : side == 4 ? 3 : side == 5 ? 2 : side == 0 ? 6 : 7;
			break;
		case EAST:
			side = side == 2 ? 5 : side == 3 ? 4 : side == 4 ? 2 : side == 5 ? 3 : side == 0 ? 6 : 7;
			break;
		case SOUTH:
			break;
		case NORTH:
			side = side == 2 ? 3 : side == 3 ? 2 : side == 4 ? 5 : side == 5 ? 4 : side;
			break;
		default:
		}

		return getBlockTextureFromSideAndMetadata(side, metadata);
	}
}
