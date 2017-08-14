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
package forestry.greenhouse.items;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.ICamouflageHandler;
import forestry.api.core.IModelManager;
import forestry.core.items.ItemWithGui;
import forestry.core.tiles.TileUtil;
import forestry.greenhouse.PluginGreenhouse;
import forestry.greenhouse.gui.ContainerCamouflageSprayCan;
import forestry.greenhouse.gui.GuiCamouflageSprayCan;
import forestry.greenhouse.inventory.ItemInventoryCamouflageSprayCan;

public class ItemCamouflageSprayCan extends ItemWithGui {

	public ItemCamouflageSprayCan() {
		setCreativeTab(PluginGreenhouse.getGreenhouseTab());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiContainer getGui(EntityPlayer player, ItemStack heldItem, int data) {
		return new GuiCamouflageSprayCan(player, new ItemInventoryCamouflageSprayCan(player, heldItem));
	}

	@Override
	public Container getContainer(EntityPlayer player, ItemStack heldItem, int data) {
		return new ContainerCamouflageSprayCan(new ItemInventoryCamouflageSprayCan(player, heldItem), player.inventory);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		super.registerModel(item, manager);
		ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:camouflage_spray_can_filled"));
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (player.isSneaking()) {
			return EnumActionResult.PASS;
		}
		ItemStack heldItem = player.getHeldItem(hand);
		ItemInventoryCamouflageSprayCan inventory = new ItemInventoryCamouflageSprayCan(player, heldItem);
		ItemStack camouflage = inventory.getStackInSlot(0);
		ICamouflageHandler handler = TileUtil.getTile(world, pos, ICamouflageHandler.class);
		if (handler != null && !camouflage.isEmpty()) {
			if (world.isRemote) {
				handler.setCamouflageBlock(camouflage, true);
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking()) {
			if (!world.isRemote) {
				openGui(player);
			}
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}
		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

}
