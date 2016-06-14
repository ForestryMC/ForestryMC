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
package forestry.factory.blocks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.Tabs;
import forestry.apiculture.MaterialBeehive;
import forestry.core.blocks.BlockStructure;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.IActivatable;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.factory.multiblock.IDistillVatControllerInternal;
import forestry.factory.multiblock.TileDistillVat;
import forestry.factory.multiblock.TileDistillVatPlain;
import forestry.factory.network.packets.PacketDistillVatChange;

public abstract class BlockDistillVat extends BlockStructure implements IStateMapperRegister {
	private static final PropertyEnum<State> STATE = PropertyEnum.create("state", State.class);
	private static final PropertyEnum<DistillVatPlainType> PLAIN_TYPE = PropertyEnum.create("type", DistillVatPlainType.class);
	
	private enum State implements IStringSerializable {
		ON, OFF;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}
	
	private enum DistillVatPlainType implements IStringSerializable {
		//NORMAL, ENTRANCE, ENTRANCE_LEFT, ENTRANCE_RIGHT;
		NORMAL;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

	public static Map<BlockDistillVatType, BlockDistillVat> create() {
		Map<BlockDistillVatType, BlockDistillVat> blockMap = new EnumMap<>(BlockDistillVatType.class);
		for (final BlockDistillVatType type : BlockDistillVatType.VALUES) {
			BlockDistillVat block = new BlockDistillVat() {
				@Nonnull
				@Override
				public BlockDistillVatType getDistillVatType() {
					return type;
				}
			};
			blockMap.put(type, block);
		}
		return blockMap;
	}

	public BlockDistillVat() {
		super(new MaterialBeehive(false));

		BlockDistillVatType distillvatType = getDistillVatType();
		IBlockState defaultState = this.blockState.getBaseState();
		if (distillvatType == BlockDistillVatType.PLAIN) {
			defaultState = defaultState.withProperty(PLAIN_TYPE, DistillVatPlainType.NORMAL);
		} else if (distillvatType.activatable) {
			defaultState = defaultState.withProperty(STATE, State.OFF);
		}
		setDefaultState(defaultState);

		setHardness(1.0f);
		setCreativeTab(Tabs.tabAgriculture);
		setHarvestLevel("pickaxe", 0);
		setSoundType(SoundType.METAL);
	}

	@Nonnull
	public abstract BlockDistillVatType getDistillVatType();

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		BlockDistillVatType type = getDistillVatType();
		switch (type) {
			case PLAIN:
			default:
				return new TileDistillVatPlain();
		}
	}

	/* ITEM MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "factory/distillvat." + getDistillVatType());
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		BlockDistillVatType distillVatType = getDistillVatType();

		if (distillVatType == BlockDistillVatType.PLAIN) {
			return new BlockStateContainer(this, PLAIN_TYPE);
		} else if (distillVatType.activatable) {
			return new BlockStateContainer(this, STATE);
		} else {
			return new BlockStateContainer(this);
		}
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileDistillVat tile = TileUtil.getTile(world, pos, TileDistillVat.class);
		if (tile == null) {
			return super.getActualState(state, world, pos);
		}

		if (tile instanceof IActivatable) {
			if (((IActivatable) tile).isActive()) {
				state = state.withProperty(STATE, State.ON);
			} else {
				state = state.withProperty(STATE, State.OFF);
			}
		} else if (getDistillVatType() == BlockDistillVatType.PLAIN) {
			if (!tile.getMultiblockLogic().getController().isAssembled()) {
				state = state.withProperty(PLAIN_TYPE, DistillVatPlainType.NORMAL);
			} else {
//				IBlockState blockStateAbove = world.getBlockState(pos.up());
//				Block blockAbove = blockStateAbove.getBlock();
//				if (BlockUtil.isWoodSlabBlock(blockStateAbove, blockAbove, world, pos)) {
//					List<EnumFacing> blocksTouching = getBlocksTouching(world, pos);
//					switch (blocksTouching.size()) {
//						case 3:
//							state = state.withProperty(PLAIN_TYPE, DistillVatPlainType.ENTRANCE);
//							break;
//						case 2:
//							if (blocksTouching.contains(EnumFacing.SOUTH) && blocksTouching.contains(EnumFacing.EAST) ||
//									blocksTouching.contains(EnumFacing.NORTH) && blocksTouching.contains(EnumFacing.WEST)) {
//								state = state.withProperty(PLAIN_TYPE, DistillVatPlainType.ENTRANCE_LEFT);
//							} else {
//								state = state.withProperty(PLAIN_TYPE, DistillVatPlainType.ENTRANCE_RIGHT);
//							}
//							break;
//						default:
//							state = state.withProperty(PLAIN_TYPE, DistillVatPlainType.NORMAL);
//							break;
//					}
//				} else {
//					state = state.withProperty(PLAIN_TYPE, DistillVatPlainType.NORMAL);
//				}
			}
		}

		return super.getActualState(state, world, pos);
	}

//	@Nonnull
//	private static List<EnumFacing> getBlocksTouching(IBlockAccess world, BlockPos blockPos) {
//		List<EnumFacing> touching = new ArrayList<>();
//		for (EnumFacing direction : EnumFacing.HORIZONTALS) {
//			IBlockState blockState = world.getBlockState(blockPos.offset(direction));
//			if (blockState.getBlock() instanceof BlockDistillVat) {
//				touching.add(direction);
//			}
//		}
//		return touching;
//	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new DistillVatStateMapper(getDistillVatType()));
	}
	
	@SideOnly(Side.CLIENT)
	private static class DistillVatStateMapper extends StateMapperBase {
		@Nonnull
		private final BlockDistillVatType type;

		public DistillVatStateMapper(@Nonnull BlockDistillVatType type) {
			this.type = type;
		}

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			String resourceDomain = Block.REGISTRY.getNameForObject(state.getBlock()).getResourceDomain();
			String resourceLocation = "factory/distillvat_" + type;
			String propertyString = getPropertyString(state.getProperties());
			return new ModelResourceLocation(resourceDomain + ':' + resourceLocation, propertyString);
		}

	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof TileDistillVat) {
			TileDistillVat tileAlveary = (TileDistillVat) tileEntity;

			// We must check that the slabs on top were not removed
			IDistillVatControllerInternal distillvat = tileAlveary.getMultiblockLogic().getController();
			distillvat.reassemble();
			if(distillvat.getReferenceCoord() != null){
				Proxies.net.sendNetworkPacket(new PacketDistillVatChange(distillvat), worldIn);
			}
		}
	}
}
