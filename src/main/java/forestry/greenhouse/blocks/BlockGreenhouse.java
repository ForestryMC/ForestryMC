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

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import forestry.api.core.ForestryAPI;
import forestry.api.core.ICamouflagedTile;
import forestry.api.core.IModelManager;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.ITextureManager;
import forestry.api.core.Tabs;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.CreativeTabForestry;
import forestry.core.blocks.BlockStructure;
import forestry.core.blocks.IBlockRotatable;
import forestry.core.blocks.IColoredBlock;
import forestry.core.blocks.properties.UnlistedBlockAccess;
import forestry.core.blocks.properties.UnlistedBlockPos;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.tiles.IActivatable;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.CamouflageUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.Log;
import forestry.core.utils.Translator;
import forestry.greenhouse.tiles.TileGreenhouseButterflyHatch;
import forestry.greenhouse.tiles.TileGreenhouseClimateControl;
import forestry.greenhouse.tiles.TileGreenhouseControl;
import forestry.greenhouse.tiles.TileGreenhouseDoor;
import forestry.greenhouse.tiles.TileGreenhouseDryer;
import forestry.greenhouse.tiles.TileGreenhouseFan;
import forestry.greenhouse.tiles.TileGreenhouseGearbox;
import forestry.greenhouse.tiles.TileGreenhouseHatch;
import forestry.greenhouse.tiles.TileGreenhouseHeater;
import forestry.greenhouse.tiles.TileGreenhousePlain;
import forestry.greenhouse.tiles.TileGreenhouseHumidifier;
import forestry.greenhouse.tiles.TileGreenhouseValve;
import forestry.greenhouse.tiles.TileGreenhouseWindow;
import forestry.greenhouse.tiles.TileGreenhouseWindow.WindowMode;
import forestry.plugins.ForestryPluginUids;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockGreenhouse extends BlockStructure implements ISpriteRegister, IColoredBlock, IBlockRotatable {
	private static final AxisAlignedBB SPRINKLER_BOUNDS = new AxisAlignedBB(0.3125F, 0.25F, 0.3125F, 0.6875F, 1F, 0.6875F);
	public static final PropertyEnum<State> STATE = PropertyEnum.create("state", State.class);
	public static final PropertyBool BLOCKED = PropertyBool.create("blocked");
	public static final PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);

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
				block = new BlockGreenhouse(type) {

					@Override
					public BlockGreenhouseType getGreenhouseType() {
						return type;
					}


					@Override
					public SoundType getSoundType() {
						if (hasGlass()) {
							return SoundType.GLASS;
						}
						return super.getSoundType();
					}
				};
			}
			blockMap.put(type, block);
		}
		return blockMap;
	}

	public BlockGreenhouse(BlockGreenhouseType type) {
		super(Material.ROCK);
		IBlockState defaultState = this.blockState.getBaseState();
		if (type == BlockGreenhouseType.WINDOW || type == BlockGreenhouseType.WINDOW_UP) {
			defaultState = defaultState.withProperty(FACING, EnumFacing.NORTH);
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn.isSneaking()) {
			return false;
		}

		TileEntity tile = worldIn.getTileEntity(pos);
		if (!(tile instanceof MultiblockTileEntityForestry)) {
			return false;
		}

		MultiblockTileEntityForestry part = (MultiblockTileEntityForestry) tile;
		IMultiblockController controller = part.getMultiblockLogic().getController();
		if (playerIn.getHeldItemMainhand().isEmpty()) {
			if (tile instanceof TileGreenhouseWindow) {
				TileGreenhouseWindow window = (TileGreenhouseWindow) tile;
				if (window.getMode() != WindowMode.CONTROL) {
					if (!worldIn.isRemote) {
						if (window.isBlocked() == WindowMode.OPEN) {
							if (window.getMode() == WindowMode.OPEN) {
								window.setMode(WindowMode.PLAYER);
							} else {
								window.setMode(WindowMode.OPEN);
							}
						}
					}
					worldIn.playEvent(playerIn, getPlaySound(!window.isActive()), pos, 0);
					return true;
				}
			}

			if (playerIn.getHeldItemOffhand().isEmpty()) {
				// If the player's hands are empty and they right-click on a multiblock, they get a
				// multiblock-debugging message if the machine is not assembled.
				if (!controller.isAssembled()) {
					String validationError = controller.getLastValidationError();
					if (validationError != null) {
						long tick = worldIn.getTotalWorldTime();
						if (tick > previousMessageTick + 20) {
							playerIn.sendMessage(new TextComponentString(validationError));
							previousMessageTick = tick;
						}
						return true;
					}
				}
			}
		}

		// Don't open the GUI if the multiblock isn't assembled
		if (!controller.isAssembled()) {
			return false;
		}
		
		if (!worldIn.isRemote) {
			part.openGui(playerIn);
		}
		return true;
	}

	protected int getPlaySound(boolean open) {
		if (open) {
			return 1007;
		} else {
			return 1013;
		}
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (getGreenhouseType() != BlockGreenhouseType.WINDOW && getGreenhouseType() != BlockGreenhouseType.WINDOW_UP) {
			return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(UnlistedBlockPos.POS, pos)
					.withProperty(UnlistedBlockAccess.BLOCKACCESS, world);
		}
		return super.getExtendedState(state, world, pos);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		if (getGreenhouseType() == BlockGreenhouseType.WINDOW || getGreenhouseType() == BlockGreenhouseType.WINDOW_UP) {
			return new BlockStateContainer(this, STATE, FACING);
		} else if (getGreenhouseType().activatable) {
			return new ExtendedBlockState(this, new IProperty[]{STATE}, new IUnlistedProperty[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS});
		} else {
			return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS});
		}
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntity tile = TileUtil.getTile(worldIn, pos, TileEntity.class);
		if (tile instanceof IActivatable) {
			state = state.withProperty(STATE, ((IActivatable) tile).isActive() ? State.ON : State.OFF);
		}
		if (tile instanceof TileGreenhouseWindow) {
			TileGreenhouseWindow window = (TileGreenhouseWindow) tile;
		}
		return super.getActualState(state, worldIn, pos);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		EnumFacing facing = state.getValue(FACING);
		return state.withProperty(FACING, rot.rotate(facing));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (getGreenhouseType() == BlockGreenhouseType.WINDOW || getGreenhouseType() == BlockGreenhouseType.WINDOW_UP) {
			return getDefaultState().withProperty(FACING, EnumFacing.VALUES[meta + 2]);
		}
		return super.getStateFromMeta(meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		if (getGreenhouseType() == BlockGreenhouseType.WINDOW || getGreenhouseType() == BlockGreenhouseType.WINDOW_UP) {
			return state.getValue(FACING).ordinal() - 2;
		}
		return 0;
	}

	@Override
	public void rotateAfterPlacement(EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		IBlockState state = world.getBlockState(pos);

		if (state.getProperties().containsKey(FACING)) {
			world.setBlockState(pos, state.withProperty(FACING, player.getHorizontalFacing().getOpposite()));
		}
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		try {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof TileGreenhouseWindow) {
				((TileGreenhouseWindow) tile).onNeighborBlockChange();
			}
		} catch (StackOverflowError error) {
			Log.error("Stack Overflow Error in BlockMachine.onNeighborBlockChange()", error);
			throw error;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		BlockGreenhouseType type = getGreenhouseType();
		switch (type) {
			case GEARBOX:
				return new TileGreenhouseGearbox();
			case DEHUMIDIFIER:
				return new TileGreenhouseDryer();
			case HUMIDIFIER:
				return new TileGreenhouseHumidifier();
			case VALVE:
				return new TileGreenhouseValve();
			case FAN:
				return new TileGreenhouseFan();
			case HEATER:
				return new TileGreenhouseHeater();
			case DOOR:
				return new TileGreenhouseDoor();
			case CONTROL:
				return new TileGreenhouseControl();
			case CLIMATE_CONTROL:
				return new TileGreenhouseClimateControl();
			case WINDOW_UP:
			case WINDOW:
				return new TileGreenhouseWindow();
			case HATCH_INPUT:
			case HATCH_OUTPUT:
				return new TileGreenhouseHatch();
			case BUTTERFLY_HATCH:
				return new TileGreenhouseButterflyHatch();
			default:
				return new TileGreenhousePlain();
		}
	}

	@Override
	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
		if (pos == null || worldIn == null) {
			return 0xffffff;
		}
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof ICamouflagedTile) {
			ItemStack camouflageStack = CamouflageUtil.getCamouflageBlock(worldIn, pos);

			if (tintIndex < 100 && !camouflageStack.isEmpty()) {
				Block block = Block.getBlockFromItem(camouflageStack.getItem());
				if (block != Blocks.AIR) {
					IBlockState camouflageState = block.getStateFromMeta(camouflageStack.getItemDamage());

					int color = Minecraft.getMinecraft().getBlockColors().colorMultiplier(camouflageState, worldIn, pos, tintIndex);
					if (color != -1) {
						return color;
					}
				}
			}
		}

		return 0xffffff;
	}

	/* MODELS */
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		if (hasGlass()) {
			return BlockRenderLayer.TRANSLUCENT;
		}
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return !hasGlass();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return !hasGlass();
	}
	
	protected boolean hasGlass(){
		BlockGreenhouseType type = getGreenhouseType();
		return type == BlockGreenhouseType.GLASS || type == BlockGreenhouseType.WINDOW || type == BlockGreenhouseType.WINDOW_UP;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileGreenhouseWindow) {
			TileGreenhouseWindow window = (TileGreenhouseWindow) tile;
			if (!window.getWorld().isRemote) {
				window.setMode(window.isBlocked());
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = blockAccess.getBlockState(pos);
		Block block = iblockstate.getBlock();

		if (getGreenhouseType() == BlockGreenhouseType.GLASS) {
			BlockPos posSide = pos.offset(side);
			if (blockAccess.getBlockState(posSide) != iblockstate) {
				return true;
			}
			TileEntity tile = blockAccess.getTileEntity(pos);
			TileEntity tileSide = blockAccess.getTileEntity(posSide);
			if (tile instanceof TileGreenhousePlain && tileSide instanceof TileGreenhousePlain) {
				if (((TileGreenhousePlain) tile).getCamouflageType().equals(((TileGreenhousePlain) tileSide).getCamouflageType())) {
					ItemStack camouflage = CamouflageUtil.getCamouflageBlock(blockAccess, pos);
					ItemStack camouflageSide = CamouflageUtil.getCamouflageBlock(blockAccess, posSide);
					if (!camouflage.isEmpty() && !camouflageSide.isEmpty()) {
						return !ItemStackUtil.isIdenticalItem(camouflage, camouflageSide);
					}
				}
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
		BlockGreenhouseType type = getGreenhouseType();
		if (type == BlockGreenhouseType.WINDOW) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("forestry:greenhouse.window", "inventory"));
		} else if (type == BlockGreenhouseType.WINDOW_UP) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("forestry:greenhouse.window_up", "inventory"));
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
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
		return getGreenhouseType() == BlockGreenhouseType.CONTROL;
	}


	public abstract BlockGreenhouseType getGreenhouseType();
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		tooltip.add(Translator.translateToLocal("tile.for.greenhouse.tooltip"));
		tooltip.add(TextFormatting.GREEN.toString() + TextFormatting.ITALIC.toString() + Translator.translateToLocal("tile.for.greenhouse.camouflage.tooltip"));
	}
}
