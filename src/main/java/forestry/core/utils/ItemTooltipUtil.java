package forestry.core.utils;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.tooltips.ToolTip;

public class ItemTooltipUtil {
	@OnlyIn(Dist.CLIENT)
	public static void addInformation(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
		String unlocalizedName = stack.getDescriptionId();
		String tooltipKey = unlocalizedName + ".tooltip";
		if (Translator.canTranslateToLocal(tooltipKey)) {
            tooltip.add(Component.translatable(tooltipKey).withStyle(ChatFormatting.GRAY));
			/*Minecraft minecraft = Minecraft.getInstance();
			List<ITextProperties> tooltipInfoWrapped = minecraft.fontRenderer.split(tooltipInfo, 150);
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
	public static void addShiftInformation(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("for.gui.tooltip.tmi", "< %s >").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static ToolTip getInformation(ItemStack stack) {
		Minecraft minecraft = Minecraft.getInstance();
		boolean advancedTooltips = minecraft.options.advancedItemTooltips;
		return getInformation(stack, minecraft.player, advancedTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static ToolTip getInformation(ItemStack stack, Player player, TooltipFlag flag) {
		if (stack.isEmpty()) {
			return null;
		}
		List<Component> tooltip = stack.getTooltipLines(player, flag);
		for (int i = 0; i < tooltip.size(); ++i) {
			//TODO - can tis be simplified (and is it correct?)
			Component component = tooltip.get(i);
			if (i == 0) {
				tooltip.set(i, ((MutableComponent) component).withStyle(stack.getRarity().color));
			} else {
				tooltip.set(i, ((MutableComponent) component).withStyle(ChatFormatting.GRAY));
			}
		}
		ToolTip toolTip = new ToolTip();
		toolTip.addAll(tooltip);
		return toolTip;
	}
}
