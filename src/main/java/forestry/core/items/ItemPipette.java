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
package forestry.core.items;

import java.util.List;

import forestry.api.core.IModelManager;
import forestry.api.core.IToolPipette;
import forestry.core.fluids.PipetteContents;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPipette extends ItemForestry implements IToolPipette {

	public ItemPipette() {
		setMaxStackSize(1);
		setFull3D();
	}

	@Override
	public boolean canPipette(ItemStack itemstack) {
		PipetteContents contained = PipetteContents.create(itemstack);
		return contained == null || !contained.isFull();
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {
		super.addInformation(itemstack, player, list, flag);

		PipetteContents contained = PipetteContents.create(itemstack);
		if (contained != null) {
			contained.addTooltip(list);
		}
	}

	/* Models */
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation[] models;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		models = new ModelResourceLocation[2];
		models[0] = manager.getModelLocation("pipette.0");
		models[1] = manager.getModelLocation("pipette.1");
		ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:pipette.0"));
		ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:pipette.1"));
		manager.registerItemModel(item, new PipetteMeshDefinition());
	}

	public class PipetteMeshDefinition implements ItemMeshDefinition {

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			if (FluidUtil.getFluidContained(stack) == null) {
				return models[0];
			} else {
				return models[1];
			}
		}

	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new FluidHandlerItemStackSimple(stack, Fluid.BUCKET_VOLUME);
	}
}
