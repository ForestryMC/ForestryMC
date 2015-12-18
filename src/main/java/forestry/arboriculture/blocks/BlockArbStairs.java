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
package forestry.arboriculture.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.api.core.IModelRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.items.ItemBlockWood;
import forestry.arboriculture.items.ItemBlockWood.WoodMeshDefinition;
import forestry.arboriculture.tiles.TileWood;
import forestry.core.render.ParticleHelper;

public class BlockArbStairs extends BlockStairs implements IWoodTyped, IModelRegister, ITileEntityProvider {

	private final ParticleHelper.Callback particleCallback;
	private final boolean fireproof;

	public BlockArbStairs(Block par2Block, boolean fireproof) {
		super(par2Block.getStateFromMeta(0));

		this.fireproof = fireproof;

		setCreativeTab(Tabs.tabArboriculture);
		setHardness(2.0F);
		setResistance(5.0F);
		setHarvestLevel("axe", 0);

		this.particleCallback = new ParticleHelper.DefaultCallback(this);
	}
	

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] {	EnumWoodType.WOODTYPE, FACING, HALF, SHAPE });
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (EnumWoodType woodType : EnumWoodType.VALUES) {
			list.add(TreeManager.woodItemAccess.getStairs(woodType, fireproof));
		}
	}

	/* DROP HANDLING */
	// Hack: 	When harvesting we need to get the drops in onBlockHarvested,
	// 			because Mojang destroys the block and tile before calling getDrops.
	private final ThreadLocal<ArrayList<ItemStack>> drops = new ThreadLocal<>();
	
	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		drops.set(TileWood.getDrops(this, world, pos));
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> ret = drops.get();
		drops.remove();

		// not harvested, get drops normally
		if (ret == null) {
			ret = TileWood.getDrops(this, world, pos);
		}

		return ret;
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		return TileWood.getPickBlock(this, world, pos);
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		if (!fireproof) {
			manager.registerVariant(item, ItemBlockWood.getVariants(this));
		}
		manager.registerItemModel(item, new WoodMeshDefinition(this));
	}

	@Override
	public boolean getUseNeighborBrightness() {
		return true;
	}

	@Override
	public String getBlockKind() {
		return "stairs";
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileWood();
	}

	/* Particles */
	@SideOnly(Side.CLIENT)
	@Override
	public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
		return ParticleHelper.addHitEffects(worldObj, this, target, effectRenderer, particleCallback);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer) {
		return ParticleHelper.addDestroyEffects(world, this, world.getBlockState(pos), pos, effectRenderer, particleCallback);
	}
}
