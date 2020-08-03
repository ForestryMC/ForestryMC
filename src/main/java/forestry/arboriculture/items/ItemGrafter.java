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
package forestry.arboriculture.items;

import forestry.api.arboriculture.IToolGrafter;
import forestry.api.core.ItemGroups;
import forestry.core.items.ItemForestryTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class ItemGrafter extends ItemForestryTool implements IToolGrafter {

    public static final ToolType GRAFTER = ToolType.get("grafter");

    public ItemGrafter(int maxDamage) {
        super(ItemStack.EMPTY, (new Item.Properties())
                .maxDamage(maxDamage)
                .group(ItemGroups.tabArboriculture)
                .addToolType(GRAFTER, 3));
        setEfficiencyOnProperMaterial(4.0f);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        if (!stack.isDamaged()) {
            tooltip.add(new TranslationTextComponent("item.forestry.uses", stack.getMaxDamage() + 1).mergeStyle(TextFormatting.GRAY));
        }
    }

    @Override
    public boolean canHarvestBlock(BlockState state) {
        Block block = state.getBlock();
        return block instanceof LeavesBlock ||
                state.getMaterial() == Material.LEAVES ||
                block.isIn(BlockTags.LEAVES) ||
                super.canHarvestBlock(state);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        return true;
    }

    @Override
    public float getSaplingModifier(ItemStack stack, World world, PlayerEntity player, BlockPos pos) {
        return 100f;
    }
}
