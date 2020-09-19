package forestry.core.utils;

import forestry.api.core.tooltips.ToolTip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemTooltipUtil {
    @OnlyIn(Dist.CLIENT)
    public static void addInformation(
            ItemStack stack,
            @Nullable World world,
            List<ITextComponent> tooltip,
            ITooltipFlag flag
    ) {
        String unlocalizedName = stack.getTranslationKey();
        String tooltipKey = unlocalizedName + ".tooltip";
        if (I18n.hasKey(tooltipKey)) {
            TranslationTextComponent tooltipInfo = new TranslationTextComponent(tooltipKey);
            tooltip.add(tooltipInfo.mergeStyle(TextFormatting.GRAY));
			/*Minecraft minecraft = Minecraft.getInstance();
			List<ITextProperties> tooltipInfoWrapped = minecraft.fontRenderer.func_238425_b_(tooltipInfo, 150);
			tooltipInfoWrapped.forEach(s -> {
				if(s instanceof IFormattableTextComponent) {
					s = ((IFormattableTextComponent) s).mergeStyle(TextFormatting.GRAY);
				}
				tooltip.add((ITextComponent) s);
				CharacterManager
			});*/
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void addShiftInformation(
            ItemStack stack,
            @Nullable IBlockReader world,
            List<ITextComponent> tooltip,
            ITooltipFlag flag
    ) {
        tooltip.add(new TranslationTextComponent("for.gui.tooltip.tmi", "< %s >").mergeStyle(
                TextFormatting.ITALIC,
                TextFormatting.GRAY
        ));
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static ToolTip getInformation(ItemStack stack) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean advancedTooltips = minecraft.gameSettings.advancedItemTooltips;
        return getInformation(
                stack,
                minecraft.player,
                advancedTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL
        );
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static ToolTip getInformation(ItemStack stack, PlayerEntity player, ITooltipFlag flag) {
        if (stack.isEmpty()) {
            return null;
        }
        List<ITextComponent> tooltip = stack.getTooltip(player, flag);
        for (int i = 0; i < tooltip.size(); ++i) {
            //TODO - can tis be simplified (and is it correct?)
            ITextComponent component = tooltip.get(i);
            if (i == 0) {
                tooltip.set(i, ((IFormattableTextComponent) component).mergeStyle(stack.getRarity().color));
            } else {
                tooltip.set(i, ((IFormattableTextComponent) component).mergeStyle(TextFormatting.GRAY));
            }
        }
        ToolTip toolTip = new ToolTip();
        toolTip.addAll(tooltip);
        return toolTip;
    }
}
