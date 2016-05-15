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
package forestry.greenhouse.blocks;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ForestryAPI;
import forestry.api.core.ICamouflagedTile;
import forestry.api.core.IModelManager;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.ITextureManager;
import forestry.api.core.Tabs;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.core.CreativeTabForestry;
import forestry.core.blocks.BlockStructure;
import forestry.core.blocks.IColoredBlock;
import forestry.core.blocks.propertys.UnlistedBlockAccess;
import forestry.core.blocks.propertys.UnlistedBlockPos;
import forestry.core.utils.CamouflageUtil;
import forestry.greenhouse.tiles.TileGreenhouseControl;
import forestry.greenhouse.tiles.TileGreenhouseDoor;
import forestry.greenhouse.tiles.TileGreenhouseDryer;
import forestry.greenhouse.tiles.TileGreenhouseFan;
import forestry.greenhouse.tiles.TileGreenhouseGearbox;
import forestry.greenhouse.tiles.TileGreenhouseHatch;
import forestry.greenhouse.tiles.TileGreenhouseHeater;
import forestry.greenhouse.tiles.TileGreenhousePlain;
import forestry.greenhouse.tiles.TileGreenhouseSprinkler;
import forestry.greenhouse.tiles.TileGreenhouseValve;
import forestry.plugins.ForestryPluginUids;

public abstract class BlockGreenhouse extends BlockStructure implements ISpriteRegister, IColoredBlock {
	private static final AxisAlignedBB SPRINKLER_BOUNDS = new AxisAlignedBB(0.3125F, 0.25F, 0.3125F, 0.6875F, 1F, 0.6875F);
	public static final PropertyEnum<State> STATE = PropertyEnum.create("state", State.class);
	
	public enum State implements IStringSerializable {
		ON, OFF;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}
	
	public static Map<BlockGreenhouseType, BlockGreenhouse> create() {
		Map<BlockGreenhouseType, BlockGreenhouse> blockMap = new EnumMap<>(BlockGreenhouseType.class);
		for (final BlockGreenhouseType type : BlockGreenhouseType.VALUES) {
			if (type == BlockGreenhouseType.BUTTERFLY_HATCH) {
				if (!ForestryAPI.enabledPlugins.contains(ForestryPluginUids.LEPIDOPTEROLOGY)) {
					continue;
				}
			}
			
			BlockGreenhouse block;
			if (type == BlockGreenhouseType.DOOR) {
				block = new BlockGreenhouseDoor();
			} else {
				block = new BlockGreenhouse() {
					@Nonnull
					@Override
					public BlockGreenhouseType getGreenhouseType() {
						return type;
					}
				};
			}
			blockMap.put(type, block);
		}
		return blockMap;
	}
	
	public BlockGreenhouse() {
		super(Material.ROCK);
		BlockGreenhouseType greenhouseType = getGreenhouseType();
		IBlockState defaultState = this.blockState.getBaseState();
		if (greenhouseType.activatable && greenhouseType != BlockGreenhouseType.SPRINKLER) {
			defaultState = defaultState.withProperty(STATE, State.OFF);
		}
		setDefaultState(defaultState);
		
		setHardness(1.0f);
		setHarvestLevel("pickaxe", 0);
		if (ForestryAPI.enabledPlugins.contains(ForestryPluginUids.FARMING)) {
			setCreativeTab(Tabs.tabAgriculture);
		} else {
			setCreativeTab(CreativeTabForestry.tabForestry);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		if (getGreenhouseType() == BlockGreenhouseType.SPRINKLER) {
			return SPRINKLER_BOUNDS;
		}
		return super.getSelectedBoundingBox(state, worldIn, pos);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		if (getGreenhouseType() == BlockGreenhouseType.SPRINKLER) {
			return SPRINKLER_BOUNDS;
		}
		return super.getCollisionBoundingBox(blockState, worldIn, pos);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (getGreenhouseType() != BlockGreenhouseType.SPRINKLER && getGreenhouseType() != BlockGreenhouseType.DOOR) {
			return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(UnlistedBlockPos.POS, pos)
					.withProperty(UnlistedBlockAccess.BLOCKACCESS, world);
		}
		return super.getExtendedState(state, world, pos);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		if (getGreenhouseType() == BlockGreenhouseType.SPRINKLER) {
			return new ExtendedBlockState(this, new IProperty[]{Properties.StaticProperty}, new IUnlistedProperty[]{Properties.AnimationProperty});
		} else if (getGreenhouseType().activatable) {
			return new ExtendedBlockState(this, new IProperty[]{STATE}, new IUnlistedProperty[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS});
		} else {
			return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS});
		}
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		if (getGreenhouseType() == BlockGreenhouseType.SPRINKLER) {
			return state.withProperty(Properties.StaticProperty, true);
		} else {
			return super.getActualState(state, worldIn, pos);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(item));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		BlockGreenhouseType type = getGreenhouseType();
		switch (type) {
			case GEARBOX:
				return new TileGreenhouseGearbox();
			case SPRINKLER:
				return new TileGreenhouseSprinkler();
			case DRYER:
				return new TileGreenhouseDryer();
			case VALVE:
				return new TileGreenhouseValve();
			case FAN:
				return new TileGreenhouseFan();
			case HEATER:
				return new TileGreenhouseHeater();
			case CONTROL:
				return new TileGreenhouseControl();
			case DOOR:
				return new TileGreenhouseDoor();
			case HATCH_INPUT:
			case HATCH_OUTPUT:
				return new TileGreenhouseHatch();
			case BUTTERFLY_HATCH:
				return new TileGreenhouseHatch();
			default:
				return new TileGreenhousePlain();
		}
	}

	@Override
	public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		if(pos == null){
			return 0xffffff;
		}
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof ICamouflagedTile) {
			ItemStack camouflageStack = CamouflageUtil.getCamouflageBlock(worldIn, pos);

			if (tintIndex < 100 && camouflageStack != null) {
				Block block = Block.getBlockFromItem(camouflageStack.getItem());
				IBlockState camouflageState = block.getStateFromMeta(camouflageStack.getItemDamage());
				
				int color = Minecraft.getMinecraft().getBlockColors().colorMultiplier(camouflageState, worldIn, pos, tintIndex);
				if(color != -1){
					return color;
				}
			}
		}

		return 0xffffff;
	}

	/* MODELS */
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		if (getGreenhouseType() == BlockGreenhouseType.GLASS || getGreenhouseType() == BlockGreenhouseType.SPRINKLER) {
			return BlockRenderLayer.TRANSLUCENT;
		}
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return getGreenhouseType() != BlockGreenhouseType.GLASS && getGreenhouseType() != BlockGreenhouseType.SPRINKLER;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return getGreenhouseType() != BlockGreenhouseType.GLASS && getGreenhouseType() != BlockGreenhouseType.SPRINKLER;
	}
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		if (getGreenhouseType() == BlockGreenhouseType.SPRINKLER) {
			if (worldIn.getBlockState(pos.up()).getBlock() == this) {
				return false;
			}
			if (!(worldIn.getTileEntity(pos.up()) instanceof IGreenhouseComponent)) {
				return false;
			}
		}
		return super.canPlaceBlockAt(worldIn, pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = blockAccess.getBlockState(pos);
		Block block = iblockstate.getBlock();

		if (getGreenhouseType() == BlockGreenhouseType.GLASS) {
			if (blockAccess.getBlockState(pos.offset(side)) != iblockstate) {
				return true;
			}

			if (block == this) {
				return false;
			}
			return block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
		} else if (getGreenhouseType() == BlockGreenhouseType.DOOR) {
			return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
		}

		return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		if (getGreenhouseType() == BlockGreenhouseType.SPRINKLER) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("forestry:greenhouse.sprinkler", "inventory"));
		} else {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("forestry:greenhouse", "inventory"));
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerSprites(ITextureManager manager) {
		BlockGreenhouseType.registerSprites();
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getGreenhouseType() == BlockGreenhouseType.CONTROL;
	}
	
	@Nonnull
	public abstract BlockGreenhouseType getGreenhouseType();
}
