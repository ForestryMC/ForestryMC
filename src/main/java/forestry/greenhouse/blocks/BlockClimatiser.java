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
import net.minecraft.block.ITileEntityProvider;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.FluidUtil;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ICamouflageHandler;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.ISpriteRegister;
import forestry.api.core.ITextureManager;
import forestry.core.blocks.IBlockWithMeta;
import forestry.core.blocks.IColoredBlock;
import forestry.core.blocks.properties.UnlistedBlockAccess;
import forestry.core.blocks.properties.UnlistedBlockPos;
import forestry.core.gui.GuiHandler;
import forestry.core.tiles.IActivatable;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.ItemTooltipUtil;
import forestry.api.core.Translator;
import forestry.greenhouse.ModuleGreenhouse;
import forestry.greenhouse.models.ModelCamouflaged;
import forestry.greenhouse.tiles.TileDehumidifier;
import forestry.greenhouse.tiles.TileFan;
import forestry.greenhouse.tiles.TileHeater;
import forestry.greenhouse.tiles.TileHumidifier;
import forestry.greenhouse.tiles.TileHygroregulator;

public class BlockClimatiser extends Block implements IBlockWithMeta, ISpriteRegister, IItemModelRegister, IColoredBlock, IBlockCamouflaged<BlockClimatiser>, ITileEntityProvider {
	public static final PropertyEnum<BlockClimatiserType> TYPE = PropertyEnum.create("type", BlockClimatiserType.class);

	public BlockClimatiser() {
		super(Material.GLASS);

		setHardness(1.0f);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(ModuleGreenhouse.getGreenhouseTab());
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockClimatiserType.HUMIDIFIER));
	}

	@Override
	public String getNameFromMeta(int meta) {
		BlockClimatiserType type = BlockClimatiserType.VALUES[meta];
		return type.getName();
	}

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, BlockClimatiserType.VALUES[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).ordinal();
	}

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState getActualState(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		IActivatable tile = TileUtil.getTile(worldIn, pos, IActivatable.class);
		State state = State.OFF;
		if (tile != null) {
			state = tile.isActive() ? State.ON : State.OFF;
		}
		return super.getActualState(blockState, worldIn, pos).withProperty(State.PROPERTY, state);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	/* MODELS */
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileForestry tile = TileUtil.getTile(world, pos, TileForestry.class);
		if (tile != null) {
			if (TileUtil.isUsableByPlayer(player, tile)) {

				if (!player.isSneaking()) {
					if (FluidUtil.interactWithFluidHandler(player, hand, world, pos, facing)) {
						return true;
					}
				}

				if (!world.isRemote) {
					GuiHandler.openGui(player, tile);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (int i = 0; i < BlockClimatiserType.VALUES.length; i++) {
			list.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{TYPE, State.PROPERTY}, new IUnlistedProperty[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS});
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		if (GuiScreen.isShiftKeyDown()) {
			tooltip.add(TextFormatting.GREEN.toString() + TextFormatting.ITALIC.toString() + Translator.translateToLocal("tile.for.greenhouse.camouflage.tooltip"));
		} else {
			ItemTooltipUtil.addShiftInformation(stack, world, tooltip, flag);
		}
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(UnlistedBlockPos.POS, pos).withProperty(UnlistedBlockAccess.BLOCKACCESS, world);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		BlockClimatiserType type = BlockClimatiserType.VALUES[meta];
		switch (type) {
			case DEHUMIDIFIER:
				return new TileDehumidifier();
			case HUMIDIFIER:
				return new TileHumidifier();
			case HYGRO:
				return new TileHygroregulator();
			case FAN:
				return new TileFan();
			case HEATER:
				return new TileHeater();
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("deprecation")
	public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
		if (pos == null || worldIn == null) {
			return 0xffffff;
		}
		ItemStack camouflageStack = getCamouflageBlock(worldIn, pos);

		if (tintIndex < ModelCamouflaged.OVERLAY_COLOR_INDEX && !camouflageStack.isEmpty()) {
			Block block = Block.getBlockFromItem(camouflageStack.getItem());
			if (block != Blocks.AIR) {
				IBlockState camouflageState = block.getStateFromMeta(camouflageStack.getItemDamage());
				BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();

				int color = blockColors.colorMultiplier(camouflageState, worldIn, pos, tintIndex);
				if (color != -1) {
					return color;
				}
			}
		}

		return 0xffffff;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < BlockClimatiserType.VALUES.length; i++) {
			ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation("forestry:climatiser", "inventory"));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerSprites(ITextureManager manager) {
		BlockClimatiserSprite.registerSprites();
	}

	@Override
	public ItemStack getCamouflageBlock(IBlockAccess world, BlockPos pos) {
		if (world == null || pos == null) {
			return ItemStack.EMPTY;
		}
		ICamouflageHandler handler = getCamouflageHandler(world, pos);
		if (handler != null) {
			ItemStack camouflageStack = handler.getCamouflageBlock();
			ItemStack defaultCamouflageStack = handler.getDefaultCamouflageBlock();
			if (!ItemStackUtil.isIdenticalItem(camouflageStack, defaultCamouflageStack)) {
				return camouflageStack;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ICamouflageHandler getCamouflageHandler(IBlockAccess world, BlockPos pos) {
		if (world == null || pos == null) {
			return null;
		}
		ICamouflageHandler handler = TileUtil.getTile(world, pos, ICamouflageHandler.class);
		if (handler != null) {
			return handler;
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasOverlaySprite(int meta, int layer) {
		return true;
	}

	@Override
	public int getLayers() {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public TextureAtlasSprite getDefaultSprite() {
		return BlockGreenhouseSprite.getSprite(BlockGreenhouseType.PLAIN, null, null, -1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public TextureAtlasSprite getOverlaySprite(EnumFacing facing, IBlockState state, int meta, int layer) {
		return BlockClimatiserSprite.getSprite(BlockClimatiserType.VALUES[meta], facing, state);
	}
}
