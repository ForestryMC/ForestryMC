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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
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
import forestry.apiculture.multiblock.IAlvearyControllerInternal;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.apiculture.multiblock.TileAlvearyFan;
import forestry.apiculture.multiblock.TileAlvearyHeater;
import forestry.apiculture.multiblock.TileAlvearyHygroregulator;
import forestry.apiculture.multiblock.TileAlvearyPlain;
import forestry.apiculture.multiblock.TileAlvearySieve;
import forestry.apiculture.multiblock.TileAlvearyStabiliser;
import forestry.apiculture.multiblock.TileAlvearySwarmer;
import forestry.apiculture.network.packets.PacketAlveryChange;
import forestry.core.blocks.BlockStructure;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.IActivatable;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;

public abstract class BlockAlveary extends BlockStructure implements IStateMapperRegister {
	private static final PropertyEnum<State> STATE = PropertyEnum.create("state", State.class);
	private static final PropertyEnum<AlvearyPlainType> PLAIN_TYPE = PropertyEnum.create("type", AlvearyPlainType.class);
	
	private enum State implements IStringSerializable {
		ON, OFF;

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}
	
	private enum AlvearyPlainType implements IStringSerializable {
		NORMAL, ENTRANCE, ENTRANCE_LEFT, ENTRANCE_RIGHT;

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}

	public static Map<BlockAlvearyType, BlockAlveary> create() {
		Map<BlockAlvearyType, BlockAlveary> blockMap = new EnumMap<>(BlockAlvearyType.class);
		for (final BlockAlvearyType type : BlockAlvearyType.VALUES) {
			BlockAlveary block = new BlockAlveary() {
				@Nonnull
				@Override
				public BlockAlvearyType getAlvearyType() {
					return type;
				}
			};
			blockMap.put(type, block);
		}
		return blockMap;
	}

	public BlockAlveary() {
		super(new MaterialBeehive(false));

		BlockAlvearyType alvearyType = getAlvearyType();
		IBlockState defaultState = this.blockState.getBaseState();
		if (alvearyType == BlockAlvearyType.PLAIN) {
			defaultState = defaultState.withProperty(PLAIN_TYPE, AlvearyPlainType.NORMAL);
		} else if (alvearyType.activatable) {
			defaultState = defaultState.withProperty(STATE, State.OFF);
		}
		setDefaultState(defaultState);

		setHardness(1.0f);
		setCreativeTab(Tabs.tabApiculture);
		setHarvestLevel("axe", 0);
		setSoundType(SoundType.WOOD);
	}

	@Nonnull
	public abstract BlockAlvearyType getAlvearyType();

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
		BlockAlvearyType type = getAlvearyType();
		switch (type) {
			case SWARMER:
				return new TileAlvearySwarmer();
			case FAN:
				return new TileAlvearyFan();
			case HEATER:
				return new TileAlvearyHeater();
			case HYGRO:
				return new TileAlvearyHygroregulator();
			case STABILISER:
				return new TileAlvearyStabiliser();
			case SIEVE:
				return new TileAlvearySieve();
			case PLAIN:
			default:
				return new TileAlvearyPlain();
		}
	}

	/* ITEM MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "apiculture/alveary." + getAlvearyType());
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		BlockAlvearyType alvearyType = getAlvearyType();

		if (alvearyType == BlockAlvearyType.PLAIN) {
			return new BlockStateContainer(this, PLAIN_TYPE);
		} else if (alvearyType.activatable) {
			return new BlockStateContainer(this, STATE);
		} else {
			return new BlockStateContainer(this);
		}
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileAlveary tile = TileUtil.getTile(world, pos, TileAlveary.class);
		if (tile == null) {
			return super.getActualState(state, world, pos);
		}

		if (tile instanceof IActivatable) {
			if (((IActivatable) tile).isActive()) {
				state = state.withProperty(STATE, State.ON);
			} else {
				state = state.withProperty(STATE, State.OFF);
			}
		} else if (getAlvearyType() == BlockAlvearyType.PLAIN) {
			if (!tile.getMultiblockLogic().getController().isAssembled()) {
				state = state.withProperty(PLAIN_TYPE, AlvearyPlainType.NORMAL);
			} else {
				IBlockState blockStateAbove = world.getBlockState(pos.up());
				Block blockAbove = blockStateAbove.getBlock();
				if (BlockUtil.isWoodSlabBlock(blockStateAbove, blockAbove, world, pos)) {
					List<EnumFacing> blocksTouching = getBlocksTouching(world, pos);
					switch (blocksTouching.size()) {
						case 3:
							state = state.withProperty(PLAIN_TYPE, AlvearyPlainType.ENTRANCE);
							break;
						case 2:
							if (blocksTouching.contains(EnumFacing.SOUTH) && blocksTouching.contains(EnumFacing.EAST) ||
									blocksTouching.contains(EnumFacing.NORTH) && blocksTouching.contains(EnumFacing.WEST)) {
								state = state.withProperty(PLAIN_TYPE, AlvearyPlainType.ENTRANCE_LEFT);
							} else {
								state = state.withProperty(PLAIN_TYPE, AlvearyPlainType.ENTRANCE_RIGHT);
							}
							break;
						default:
							state = state.withProperty(PLAIN_TYPE, AlvearyPlainType.NORMAL);
							break;
					}
				} else {
					state = state.withProperty(PLAIN_TYPE, AlvearyPlainType.NORMAL);
				}
			}
		}

		return super.getActualState(state, world, pos);
	}

	@Nonnull
	private static List<EnumFacing> getBlocksTouching(IBlockAccess world, BlockPos blockPos) {
		List<EnumFacing> touching = new ArrayList<>();
		for (EnumFacing direction : EnumFacing.HORIZONTALS) {
			IBlockState blockState = world.getBlockState(blockPos.offset(direction));
			if (blockState.getBlock() instanceof BlockAlveary) {
				touching.add(direction);
			}
		}
		return touching;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new AlvearyStateMapper(getAlvearyType()));
	}
	
	@SideOnly(Side.CLIENT)
	private static class AlvearyStateMapper extends StateMapperBase {
		@Nonnull
		private final BlockAlvearyType type;

		public AlvearyStateMapper(@Nonnull BlockAlvearyType type) {
			this.type = type;
		}

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			String resourceDomain = Block.REGISTRY.getNameForObject(state.getBlock()).getResourceDomain();
			String resourceLocation = "apiculture/alveary_" + type;
			String propertyString = getPropertyString(state.getProperties());
			return new ModelResourceLocation(resourceDomain + ':' + resourceLocation, propertyString);
		}

	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
		super.onNeighborBlockChange(world, pos, state, neighborBlock);
		
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileAlveary) {
			TileAlveary tileAlveary = (TileAlveary) tileEntity;

			// We must check that the slabs on top were not removed
			IAlvearyControllerInternal alveary = tileAlveary.getMultiblockLogic().getController();
			alveary.reassemble();
			if(alveary.getReferenceCoord() != null){
				Proxies.net.sendNetworkPacket(new PacketAlveryChange(alveary), world);
			}
		}
	}
}
