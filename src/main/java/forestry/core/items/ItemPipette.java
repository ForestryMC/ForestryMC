/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.items;

import forestry.api.core.IToolPipette;
import forestry.core.ItemGroupForestry;
import forestry.core.fluids.PipetteContents;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nullable;
import java.util.List;

//import net.minecraft.client.renderer.ItemMeshDefinition;

public class ItemPipette extends ItemForestry implements IToolPipette {
    @OnlyIn(Dist.CLIENT)
    public ModelResourceLocation[] models;

    public ItemPipette() {
        super((new Item.Properties())
                .maxStackSize(1)
                .group(ItemGroupForestry.tabForestry));
        //addPropertyOverride(new ResourceLocation("state"), (itemStack, world, livingEntity) -> FluidUtil.getFluidContained(itemStack).isPresent() ? 1 : 0);
        //		setFull3D();
    }

    @Override
    public boolean canPipette(ItemStack itemstack) {
        PipetteContents contained = PipetteContents.create(itemstack);
        return contained == null || !contained.isFull();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(
            ItemStack itemstack,
            @Nullable World world,
            List<ITextComponent> list,
            ITooltipFlag flag
    ) {
        super.addInformation(itemstack, world, list, flag);

        if (CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY == null) {
            return;
        }

        PipetteContents contained = PipetteContents.create(itemstack);
        if (contained != null) {
            contained.addTooltip(list);
        }
    }

    /* Models */

    //	@OnlyIn(Dist.CLIENT)
    //	@Override
    //	public void registerModel(Item item, IModelManager manager) {
    //		models = new ModelResourceLocation[2];
    //		models[0] = manager.getModelLocation("pipette.0");
    //		models[1] = manager.getModelLocation("pipette.1");
    //		ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:pipette.0"));
    //		ModelBakery.registerItemVariants(item, new ResourceLocation("forestry:pipette.1"));
    //		manager.registerItemModel(item, new PipetteMeshDefinition());
    //	}

    //	@OnlyIn(Dist.CLIENT)
    //	public class PipetteMeshDefinition implements ItemMeshDefinition {
    //		@Override
    //		public ModelResourceLocation getModelLocation(ItemStack stack) {
    //			if (FluidUtil.getFluidContained(stack) == null) {
    //				return models[0];
    //			} else {
    //				return models[1];
    //			}
    //		}
    //	}

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new FluidHandlerItemStack(stack, FluidAttributes.BUCKET_VOLUME);
    }
}
