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

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
import forestry.api.core.IToolPipette;
import forestry.core.config.Constants;
import forestry.core.fluids.PipetteContents;

public class ItemPipette extends ItemForestry implements IToolPipette {

	public ItemPipette() {
		super();
		setMaxStackSize(1);
		setFull3D();
	}

	/**
	 * @return true if the item's stackTagCompound needs to be synchronized over SMP.
	 */
	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public boolean canPipette(ItemStack itemstack) {
		PipetteContents contained = new PipetteContents(itemstack.getTagCompound());
		return !contained.isFull();
	}

	@Override
	public int fill(ItemStack itemstack, FluidStack liquid, boolean doFill) {
		PipetteContents contained = new PipetteContents(itemstack.getTagCompound());

		int limit = getCapacity(itemstack);
		int filled;

		if (contained.getContents() == null) {
			if (liquid.amount > limit) {
				filled = limit;
			} else {
				filled = liquid.amount;
			}

			contained.setContents(new FluidStack(liquid, filled));
			filled = liquid.amount;
		} else {
			if (contained.getContents().amount >= limit) {
				return 0;
			}
			if (!contained.getContents().isFluidEqual(liquid)) {
				return 0;
			}

			int space = limit - contained.getContents().amount;

			if (liquid.amount > space) {
				filled = space;
			} else {
				filled = liquid.amount;
			}

			contained.getContents().amount += filled;
		}

		if (doFill) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			contained.writeToNBT(nbttagcompound);
			itemstack.setTagCompound(nbttagcompound);
			itemstack.setItemDamage(1);
		}

		return filled;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		PipetteContents contained = new PipetteContents(itemstack.getTagCompound());
		contained.addTooltip(list);
	}

	/* Models */
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation[] models = new ModelResourceLocation[2];

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		models[0] = manager.getModelLocation("pipette.0");
		models[1] = manager.getModelLocation("pipette.1");
		manager.registerVariant(item, new ResourceLocation("forestry:pipette.0"));
		manager.registerVariant(item, new ResourceLocation("forestry:pipette.1"));
		manager.registerItemModel(item, new PippetMeshDefinition());
	}

	public class PippetMeshDefinition implements ItemMeshDefinition {

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			if (stack.getItemDamage() <= 0) {
				return models[0];
			} else {
				return models[1];
			}
		}

	}

	@Override
	public FluidStack drain(ItemStack pipette, int maxDrain, boolean doDrain) {
		PipetteContents contained = new PipetteContents(pipette.getTagCompound());
		if (contained.getContents() == null || contained.getContents().getFluid().getID() <= 0) {
			return null;
		}

		int drained = maxDrain;
		if (contained.getContents().amount < drained) {
			drained = contained.getContents().amount;
		}

		if (doDrain) {
			contained.getContents().amount -= drained;

			if (contained.getContents().amount <= 0) {
				pipette.setTagCompound(null);
				pipette.setItemDamage(0);
			} else {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				contained.writeToNBT(nbttagcompound);
				pipette.setTagCompound(nbttagcompound);
			}
		}

		return new FluidStack(contained.getContents(), drained);
	}

	@Override
	public int getCapacity(ItemStack pipette) {
		return Constants.BUCKET_VOLUME;
	}
}
