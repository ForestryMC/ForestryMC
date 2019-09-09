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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.climate.IClimateHousing;
import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTransformer;
import forestry.core.climate.ClimateRoot;
import forestry.core.items.IColoredItem;
import forestry.core.items.ItemForestry;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.StringUtil;

public class ItemHabitatScreen extends ItemForestry implements IColoredItem {

	public static final String POSITION_KEY = "greenhouse";
	public static final String DIMENSION_KEY = "dimension";
	public static final String PREVIEW_KEY = "preview";

	public static boolean isPreviewModeActive(ItemStack itemStack) {
		CompoundNBT compoundNBT = itemStack.getTag();
		if (compoundNBT == null || !compoundNBT.contains(PREVIEW_KEY)) {
			return false;
		}
		return compoundNBT.getBoolean(PREVIEW_KEY);
	}

	public static void setPreviewMode(ItemStack itemStack, boolean preview) {
		itemStack.setTagInfo(PREVIEW_KEY, new ByteNBT((byte) (preview ? 1 : 0)));
	}

	@Nullable
	public static BlockPos getPosition(ItemStack itemStack) {
		CompoundNBT CompoundNBT = itemStack.getTag();
		if (CompoundNBT == null || !CompoundNBT.contains(POSITION_KEY)) {
			return null;
		}
		CompoundNBT compound = CompoundNBT.getCompound(POSITION_KEY);
		if (compound.isEmpty()) {
			return null;
		}
		return NBTUtil.readBlockPos(compound);
	}

	public static int getDimension(ItemStack itemStack) {
		CompoundNBT compoundNBT = itemStack.getTag();
		if (compoundNBT == null || !compoundNBT.contains(DIMENSION_KEY)) {
			return Integer.MAX_VALUE;
		}
		return compoundNBT.getInt(DIMENSION_KEY);
	}

	public static boolean isValid(ItemStack stack, @Nullable World world) {
		BlockPos pos = getPosition(stack);
		int dimension = getDimension(stack);
		if (pos == null || world == null || dimension == Integer.MAX_VALUE || dimension != world.getDimension().getType().getId() || !world.isBlockLoaded(pos)) {
			return false;
		} else {
			return TileUtil.getTile(world, pos, IClimateHousing.class) != null;
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
		if (!player.isSneaking()) {
			boolean previewModeActive = isPreviewModeActive(itemStack);
			setPreviewMode(itemStack, !previewModeActive);

			if (!world.isRemote) {
				String text = !previewModeActive ? "for.habitat_screen.mode.active" : "for.habitat_screen.mode.inactive";
				player.sendStatusMessage(new TranslationTextComponent(text), true);
			}
		}

		return ActionResult.newResult(ActionResultType.SUCCESS, itemStack);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		World world = context.getWorld();
		BlockPos pos = context.getPos();

		if (player.isSneaking()) {
			IClimateHousing housing = TileUtil.getTile(world, pos, IClimateHousing.class);
			if (housing != null) {
				ItemStack heldItem = player.getHeldItem(context.getHand());
				heldItem.setTagInfo(POSITION_KEY, NBTUtil.writeBlockPos(pos));
				heldItem.setTagInfo(DIMENSION_KEY, new IntNBT(world.getDimension().getType().getId()));
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
				player.sendStatusMessage(new TranslationTextComponent("for.habitat_screen.status.state", TextFormatting.GOLD.toString() + StringUtil.floatAsPercent(state.getTemperature()), TextFormatting.BLUE.toString() + StringUtil.floatAsPercent(state.getHumidity())), true);
			} else {
				player.sendStatusMessage(new TranslationTextComponent("for.habitat_screen.status.nostate"), true);
			}
		}
		return ActionResultType.SUCCESS;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		super.addInformation(stack, world, tooltip, flag);
		if (world == null) {
			return;
		}
		boolean previewModeActive = isPreviewModeActive(stack);
		tooltip.add(new TranslationTextComponent(previewModeActive ? "for.habitat_screen.mode.active" : "for.habitat_screen.mode.inactive"));
		boolean isValid = isValid(stack, world);
		BlockPos pos = getPosition(stack);
		if (pos != null) {
			ITextComponent state = isValid ? new TranslationTextComponent("for.habitat_screen.state.linked", pos.getX(), pos.getY(), pos.getZ(), world.getDimension().getType().getId()) : new TranslationTextComponent("for.habitat_screen.state.fail");
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
		tooltip.add(new TranslationTextComponent("for.habitat_screen.temperature", StringUtil.floatAsPercent(climateState.getTemperature())).applyTextStyle(TextFormatting.GOLD));
		tooltip.add(new TranslationTextComponent("for.habitat_screen.humidity", StringUtil.floatAsPercent(climateState.getHumidity())).applyTextStyle(TextFormatting.BLUE));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int tintIndex) {
		if (tintIndex == 2) {
			return isValid(stack, Minecraft.getInstance().world) ? 0x14B276 : 0xBA1F17;
		} else if (tintIndex == 1) {
			World world = Minecraft.getInstance().world;
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
