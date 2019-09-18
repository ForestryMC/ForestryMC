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
package forestry.farming.blocks;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import forestry.api.core.IModelManager;
import forestry.core.blocks.BlockStructure;
import forestry.farming.models.EnumFarmMaterial;
import forestry.farming.tiles.TileFarmControl;
import forestry.farming.tiles.TileFarmGearbox;
import forestry.farming.tiles.TileFarmHatch;
import forestry.farming.tiles.TileFarmPlain;
import forestry.farming.tiles.TileFarmValve;

//import net.minecraftforge.common.property.ExtendedBlockState;
//import net.minecraftforge.common.property.IExtendedBlockState;
//import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockFarm extends BlockStructure {

	public static final EnumProperty<EnumFarmBlockType> META = EnumProperty.create("meta", EnumFarmBlockType.class);
	private final EnumFarmBlockType type;
	private final EnumFarmMaterial farmMaterial;

	public BlockFarm(EnumFarmBlockType type, EnumFarmMaterial farmMaterial) {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(1.0f)
				.harvestTool(ToolType.PICKAXE)
				.harvestLevel(0));
		this.type = type;
		this.farmMaterial = farmMaterial;
	}

	//TODO - either flatten or work out how extended states work
	//	@Override
	//	public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
	//		return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).with(UnlistedBlockPos.POS, pos)
	//			.with(UnlistedBlockAccess.BLOCKACCESS, world);
	//	}
	//
	//	@Override
	//	protected BlockStateContainer createBlockState() {
	//		return new ExtendedBlockState(this, new IProperty[]{META},
	//			new ModelProperty<>()[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS});
	//	}

	@Override
	public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> list) {
		if (type == EnumFarmBlockType.BAND) {
			return;
		}
		super.fillItemGroup(tab, list);
	}

	public EnumFarmBlockType getType() {
		return type;
	}

	public EnumFarmMaterial getFarmMaterial() {
		return farmMaterial;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		List<ItemStack> drops = getDrops(world.getBlockState(pos), (ServerWorld) world, pos, world.getTileEntity(pos));    //TODO this call is not safe
		if (drops.isEmpty()) {
			return super.getPickBlock(state, target, world, pos, player);
		}
		return drops.get(0);
	}

	//TODO - idk
	//	@Override
	//	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest) {
	//		if (!world.isRemote && canHarvestBlock(world, pos, player)) {
	//			List<ItemStack> drops = getDrops(world, pos, world.getBlockState(pos), 0);
	//			for (ItemStack drop : drops) {
	//				ItemStackUtil.dropItemStackAsEntity(drop, world, pos);
	//			}
	//		}
	//		return world.setBlockToAir(pos);
	//	}

	//TODO not sjure about this
	//	@Override
	//	public static List<ItemStack> getDrops(BlockState state, ServerWorld worldIn, BlockPos pos, @Nullable TileEntity tileEntityIn) {
	////		int meta = getMetaFromState(state);
	//
	//		List<ItemStack> ret = new ArrayList<>();
	//		if(!(tileEntityIn instanceof TileFarm)) {
	//			return ret;
	//		}
	//
	//		ItemStack stack = new ItemStack(state.getBlock());
	//		CompoundNBT compound = new CompoundNBT();
	//		((TileFarm) te).getFarmBlockTexture().saveToCompound(compound);
	//		stack.setTag(compound);
	//		ret.add(stack);
	//		return ret;
	//	}

		@Override
		public TileEntity createTileEntity(BlockState state, IBlockReader world) {
			switch (state.get(META)) {
				case GEARBOX:
					return new TileFarmGearbox();
				case HATCH:
					return new TileFarmHatch();
				case VALVE:
					return new TileFarmValve();
				case CONTROL:
					return new TileFarmControl();
				default:
					return new TileFarmPlain();
			}
		}

		@Override
		public boolean hasTileEntity(BlockState state) {
			return true;
		}

	/* MODELS */
	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < 6; i++) {
			if (i == 1) {
				continue;
			}
			//TODO
			//			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation("forestry:ffarm", "inventory"));
		}
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
		return getType() == EnumFarmBlockType.CONTROL;
	}
}
