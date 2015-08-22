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
package forestry.core.gadgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.interfaces.IBlockRenderer;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;

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
		for (int i = 0; i < 8; i++) {
			faceMap[i] = 0;
		}

	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public void register() {
		registerTileEntity();
		registerCrafting();
		if (renderer != null) {
			Proxies.render.registerTESR(this);
		}
	}

	@SuppressWarnings("unchecked")
	private void registerCrafting() {
		for (IRecipe recipe : recipes) {
			if (recipe != null) {
				CraftingManager.getInstance().getRecipeList().add(recipe);
			}
		}
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

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, meta));
	}

	/* BLOCK DROPS */
	public boolean handlesDrops() {
		return false;
	}

	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return new ArrayList<ItemStack>();
	}

	/* INTERACTION */
	public boolean isSolidOnSide(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float fXplayerClick, float fY, float fZ) {
		return false;
	}

	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
	}

	public boolean removedByPlayer(World world, EntityPlayer player, BlockPos pos) {
		return true;
	}

	public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		return false;
	}

	/* TEXTURES */
	private final int[] faceMap;

	public MachineDefinition setFaces(int... faces) {

		if (faces.length > 6) {
			System.arraycopy(faces, 0, faceMap, 0, faces.length);
		} else {
			System.arraycopy(faces, 0, faceMap, 0, 6);
			faceMap[6] = faces[0];
			faceMap[7] = faces[1];
		}

		return this;
	}
}
