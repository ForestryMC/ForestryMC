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
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
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

import forestry.api.climate.IClimateContainer;
import forestry.api.climate.IClimateState;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflagedTile;
import forestry.api.core.IModelManager;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.ITextureManager;
import forestry.api.greenhouse.IGreenhouseProvider;
import forestry.api.multiblock.IGreenhouseComponent;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.blocks.BlockStructure;
import forestry.core.blocks.IBlockWithMeta;
import forestry.core.blocks.IColoredBlock;
import forestry.core.blocks.properties.UnlistedBlockAccess;
import forestry.core.blocks.properties.UnlistedBlockPos;
import forestry.core.multiblock.MultiblockTileEntityForestry;
import forestry.core.multiblock.MultiblockUtil;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ClimateUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.ItemTooltipUtil;
import forestry.core.utils.Translator;
import forestry.greenhouse.PluginGreenhouse;
import forestry.greenhouse.models.ModelCamouflaged;
import forestry.greenhouse.multiblock.IGreenhouseControllerInternal;
import forestry.greenhouse.tiles.TileGreenhouseControl;
import forestry.greenhouse.tiles.TileGreenhouseGearbox;
import forestry.greenhouse.tiles.TileGreenhousePlain;
import forestry.greenhouse.tiles.TileGreenhouseScreen;

public class BlockGreenhouse extends BlockStructure implements ISpriteRegister, IBlockWithMeta, IColoredBlock, IBlockCamouflaged<BlockGreenhouse> {
	public static final PropertyEnum<BlockGreenhouseType> TYPE = PropertyEnum.create("type", BlockGreenhouseType.class);

	public BlockGreenhouse() {
		super(Material.ROCK);

		setHardness(1.0f);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(PluginGreenhouse.getGreenhouseTab());
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockGreenhouseType.PLAIN));
	}

	@Override
	public String getNameFromMeta(int meta) {
		BlockGreenhouseType type = BlockGreenhouseType.VALUES[meta];
		return type.getName();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < BlockGreenhouseType.VALUES.length; i++) {
			if (i == 1 || i == 2) {
				continue;
			}
			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation("forestry:greenhouse", "inventory"));
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn.isSneaking()) {
			return false;
		}

		MultiblockTileEntityForestry part = TileUtil.getTile(worldIn, pos, MultiblockTileEntityForestry.class);
		if (part == null) {
			return false;
		}

		IMultiblockController controller = part.getMultiblockLogic().getController();
		ItemStack mainHand = playerIn.getHeldItemMainhand();
		if (mainHand.isEmpty()) {
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

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, BlockGreenhouseType.VALUES[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).ordinal();
	}

	@Override
	public int damageDropped(IBlockState state) {
		int meta = getMetaFromState(state);
		if (meta == 1 || meta == 2) {
			meta = 0;
		}
		return meta;
	}

	/* MODELS */
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
		for (int i = 0; i < BlockGreenhouseType.VALUES.length; i++) {
			if (i == 1 || i == 2) {
				continue;
			}
			items.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{TYPE}, new IUnlistedProperty[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS});
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		if (GuiScreen.isShiftKeyDown()) {
			tooltip.add(Translator.translateToLocal("tile.for.greenhouse.tooltip"));
			tooltip.add(TextFormatting.GREEN.toString() + TextFormatting.ITALIC.toString() + Translator.translateToLocal("tile.for.greenhouse.camouflage.tooltip"));
		} else {
			ItemTooltipUtil.addShiftInformation(stack, world, tooltip, flag);
		}
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
		return getGreenhouseType(state) == BlockGreenhouseType.CONTROL;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(UnlistedBlockPos.POS, pos)
			.withProperty(UnlistedBlockAccess.BLOCKACCESS, world);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		BlockGreenhouseType type = getGreenhouseType(getStateFromMeta(meta));
		switch (type) {
			case GEARBOX:
				return new TileGreenhouseGearbox();
			case CONTROL:
				return new TileGreenhouseControl();
			case SCREEN:
				return new TileGreenhouseScreen();
			default:
				return new TileGreenhousePlain();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("deprecation")
	public int colorMultiplier(IBlockState blockState, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
		if (pos == null || world == null) {
			return 16777215;
		}
		ItemStack camouflageStack = getCamouflageBlock(world, pos);
		if (tintIndex == ModelCamouflaged.OVERLAY_COLOR_INDEX + 1) {
			BlockGreenhouseType type = getGreenhouseType(blockState);
			if (type == BlockGreenhouseType.SCREEN) {
				IGreenhouseControllerInternal controller = MultiblockUtil.getController(world, pos, IGreenhouseComponent.class);
				if (controller == null || !controller.isAssembled()) {
					return 16777215;
				}
				IClimateContainer container = controller.getClimateContainer();
				IClimateState state = container.getState();
				return ClimateUtil.getColor(EnumTemperature.getFromValue(state.getTemperature()));
			} else if (type == BlockGreenhouseType.BORDER_CENTER) {
				boolean isClosed = true;
				IGreenhouseControllerInternal controller = MultiblockUtil.getController(world, pos, IGreenhouseComponent.class);
				if (controller == null || !controller.isAssembled()) {
					isClosed = false;
				} else {
					IGreenhouseProvider manager = controller.getProvider();
					isClosed = manager.isClosed();
				}
				return isClosed ? 1356406 : 12197655;
			}
		} else if (tintIndex < ModelCamouflaged.OVERLAY_COLOR_INDEX && !camouflageStack.isEmpty()) {
			Block block = Block.getBlockFromItem(camouflageStack.getItem());
			if (block != Blocks.AIR) {
				IBlockState camouflageState = block.getStateFromMeta(camouflageStack.getItemDamage());
				BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();

				int color = blockColors.colorMultiplier(camouflageState, world, pos, tintIndex);
				if (color != -1) {
					return color;
				}
			}
		}

		return 16777215;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerSprites(ITextureManager manager) {
		BlockGreenhouseSprite.registerSprites();
	}

	public BlockGreenhouseType getGreenhouseType(IBlockState state) {
		return state.getValue(TYPE);
	}

	@Override
	public ItemStack getCamouflageBlock(IBlockAccess world, BlockPos pos) {
		if (world == null || pos == null) {
			return ItemStack.EMPTY;
		}
		TileEntity tile = TileUtil.getTile(world, pos, TileEntity.class);
		if (tile instanceof ICamouflagedTile) {
			ICamouflagedTile block = (ICamouflagedTile) tile;
			ItemStack camouflageStack = ItemStack.EMPTY;

			if (tile instanceof ICamouflageHandler) {
				ICamouflageHandler tileHandler = (ICamouflageHandler) tile;
				ItemStack tileCamouflageStack = tileHandler.getCamouflageBlock();
				ItemStack defaultCamouflageStack = tileHandler.getDefaultCamouflageBlock();
				if (!ItemStackUtil.isIdenticalItem(tileCamouflageStack, defaultCamouflageStack)) {
					camouflageStack = tileCamouflageStack;
				}
			}
			if (camouflageStack.isEmpty() && tile instanceof IMultiblockComponent) {
				IMultiblockComponent component = (IMultiblockComponent) tile;
				IMultiblockController controller = component.getMultiblockLogic().getController();
				if (controller instanceof ICamouflageHandler) {
					ICamouflageHandler multiblockHandler = (ICamouflageHandler) controller;
					camouflageStack = multiblockHandler.getCamouflageBlock();
				}
				if (!controller.isAssembled()) {
					camouflageStack = ItemStack.EMPTY;
				}
			}

			return camouflageStack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ICamouflageHandler getCamouflageHandler(IBlockAccess world, BlockPos pos) {
		TileEntity tile = TileUtil.getTile(world, pos, TileEntity.class);
		if (tile instanceof ICamouflagedTile) {
			ICamouflagedTile block = (ICamouflagedTile) tile;
			ICamouflageHandler handler = null;
			if (tile instanceof ICamouflageHandler) {
				handler = (ICamouflageHandler) tile;
			}
			if ((handler == null || handler.getCamouflageBlock().isEmpty()) && tile instanceof IMultiblockComponent) {
				IMultiblockComponent component = (IMultiblockComponent) tile;
				IMultiblockController controller = component.getMultiblockLogic().getController();
				if (controller instanceof ICamouflageHandler) {
					handler = (ICamouflageHandler) controller;
				}
			}
			return handler;
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasOverlaySprite(int meta, int layer) {
		BlockGreenhouseType type = BlockGreenhouseType.VALUES[meta];
		return layer == 0 || type.twoLayers;
	}

	@Override
	public int getLayers() {
		return 2;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public TextureAtlasSprite getDefaultSprite() {
		return BlockGreenhouseSprite.getSprite(BlockGreenhouseType.PLAIN, null, null, -1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public TextureAtlasSprite getOverlaySprite(EnumFacing facing, IBlockState state, int meta, int layer) {
		return BlockGreenhouseSprite.getSprite(BlockGreenhouseType.VALUES[meta], facing, state, layer);
	}
}
