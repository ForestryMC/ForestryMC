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
package forestry.climatology.items;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.climate.IClimateHousing;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTransformer;
import forestry.core.climate.ClimateRoot;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.StringUtil;
import forestry.core.utils.Translator;

public class ItemHabitatScreen extends ItemForestry implements IColoredItem {

	public static final String POSITION_KEY = "greenhouse";
	public static final String DIMENSION_KEY = "dimension";
	public static final String PREVIEW_KEY = "preview";

	public static boolean isPreviewModeActive(ItemStack itemStack) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null || !nbtTagCompound.hasKey(PREVIEW_KEY)) {
			return false;
		}
		return nbtTagCompound.getBoolean(PREVIEW_KEY);
	}

	public static void setPreviewMode(ItemStack itemStack, boolean preview) {
		itemStack.setTagInfo(PREVIEW_KEY, new NBTTagByte((byte) (preview ? 1 : 0)));
	}

	@Nullable
	public static BlockPos getPosition(ItemStack itemStack) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null || !nbtTagCompound.hasKey(POSITION_KEY)) {
			return null;
		}
		NBTTagCompound compound = nbtTagCompound.getCompoundTag(POSITION_KEY);
		if (compound.isEmpty()) {
			return null;
		}
		return NBTUtil.getPosFromTag(compound);
	}

	public static int getDimension(ItemStack itemStack) {
		NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
		if (nbtTagCompound == null || !nbtTagCompound.hasKey(DIMENSION_KEY)) {
			return Integer.MAX_VALUE;
		}
		return nbtTagCompound.getInteger(DIMENSION_KEY);
	}

	public static boolean isValid(ItemStack stack, @Nullable World world) {
		BlockPos pos = getPosition(stack);
		int dimension = getDimension(stack);
		if (pos == null || world == null || dimension == Integer.MAX_VALUE || dimension != world.provider.getDimension() || !world.isBlockLoaded(pos)) {
			return false;
		} else {
			return TileUtil.getTile(world, pos, IClimateHousing.class) != null;
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
		if (!player.isSneaking()) {
			boolean previewModeActive = isPreviewModeActive(itemStack);
			setPreviewMode(itemStack, !previewModeActive);

			if (!world.isRemote) {
				String text = !previewModeActive ? "for.habitat_screen.mode.active" : "for.habitat_screen.mode.inactive";
				player.sendStatusMessage(new TextComponentTranslation(text), true);
			}
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (player.isSneaking()) {
			IClimateHousing housing = TileUtil.getTile(world, pos, IClimateHousing.class);
			if (housing != null) {
				ItemStack heldItem = player.getHeldItem(hand);
				heldItem.setTagInfo(POSITION_KEY, NBTUtil.createPosTag(pos));
				heldItem.setTagInfo(DIMENSION_KEY, new NBTTagInt(world.provider.getDimension()));
			}
		}
		if (!world.isRemote) {
			IClimateState state;
			IClimateState climateState = ClimateRoot.getInstance().getState(world, pos);
			if (climateState.isPresent()) {
				state = climateState;
				if (!state.isPresent()) {
					state = ClimateRoot.getInstance().getBiomeState(world, pos);
				}
			} else {
				state = ClimateRoot.getInstance().getBiomeState(world, pos);
			}
			if (state.isPresent()) {
				player.sendStatusMessage(new TextComponentTranslation("for.habitat_screen.status.state", TextFormatting.GOLD.toString() + StringUtil.floatAsPercent(state.getTemperature()), TextFormatting.BLUE.toString() + StringUtil.floatAsPercent(state.getHumidity())), true);
			} else {
				player.sendStatusMessage(new TextComponentTranslation("for.habitat_screen.status.nostate"), true);
			}
		}
		return EnumActionResult.SUCCESS;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		super.addInformation(stack, world, tooltip, flag);
		if (world == null) {
			return;
		}
		boolean previewModeActive = isPreviewModeActive(stack);
		tooltip.add(Translator.translateToLocal(previewModeActive ? "for.habitat_screen.mode.active" : "for.habitat_screen.mode.inactive"));
		boolean isValid = isValid(stack, world);
		BlockPos pos = getPosition(stack);
		if (pos != null) {
			String state = isValid ? Translator.translateToLocalFormatted("for.habitat_screen.state.linked", pos.getX(), pos.getY(), pos.getZ(), world.provider.getDimension()) : Translator.translateToLocal("for.habitat_screen.state.fail");
			tooltip.add(state);
		}
		if (!isValid || pos == null) {
			return;
		}
		IClimateHousing housing = TileUtil.getTile(world, pos, IClimateHousing.class);
		if (housing == null) {
			return;
		}
		IClimateState climateState = housing.getTransformer().getCurrent();
		tooltip.add(Translator.translateToLocalFormatted("for.habitat_screen.temperature", TextFormatting.GOLD + StringUtil.floatAsPercent(climateState.getTemperature())));
		tooltip.add(Translator.translateToLocalFormatted("for.habitat_screen.humidity", TextFormatting.BLUE + StringUtil.floatAsPercent(climateState.getHumidity())));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemstack(ItemStack stack, int tintIndex) {
		if (tintIndex == 2) {
			return isValid(stack, Minecraft.getMinecraft().world) ? 0x14B276 : 0xBA1F17;
		} else if (tintIndex == 1) {
			World world = Minecraft.getMinecraft().world;
			if (!isValid(stack, world)) {
				return 0xFFFFFF;
			}
			BlockPos pos = getPosition(stack);
			if (pos == null) {
				return 0xFFFFFF;
			}
			IClimateHousing housing = TileUtil.getTile(world, pos, IClimateHousing.class);
			if (housing == null) {
				return 0xFFFFFF;
			}
			IClimateTransformer transformer = housing.getTransformer();
			IClimateState state = transformer.getCurrent();
			return state.getTemperatureEnum().color;
		}
		return 0xFFFFFF;
	}
}
