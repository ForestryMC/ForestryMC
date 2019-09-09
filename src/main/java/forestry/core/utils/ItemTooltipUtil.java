package forestry.core.utils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
public class ItemTooltipUtil {
	@OnlyIn(Dist.CLIENT)
	public static void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		String unlocalizedName = stack.getTranslationKey();
		String tooltipKey = unlocalizedName + ".tooltip";
		if (Translator.canTranslateToLocal(tooltipKey)) {
			TranslationTextComponent tooltipInfo = new TranslationTextComponent(tooltipKey);
			Minecraft minecraft = Minecraft.getInstance();
			List<String> tooltipInfoWrapped = minecraft.fontRenderer.listFormattedStringToWidth(tooltipInfo.getFormattedText(), 150);
			tooltipInfoWrapped.forEach(s -> tooltip.add(new StringTextComponent(s).applyTextStyle(TextFormatting.GRAY)));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void addShiftInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		tooltip.add(new TranslationTextComponent("for.gui.tooltip.tmi", "< %s >").setStyle(new Style().setItalic(true).setColor(TextFormatting.GRAY)));
	}

	@OnlyIn(Dist.CLIENT)
	public static List<ITextComponent> getInformation(ItemStack stack) {
		Minecraft minecraft = Minecraft.getInstance();
		boolean advancedTooltips = minecraft.gameSettings.advancedItemTooltips;
		return getInformation(stack, minecraft.player, advancedTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
	}

	@OnlyIn(Dist.CLIENT)
	public static List<ITextComponent> getInformation(ItemStack stack, PlayerEntity player, ITooltipFlag flag) {
		if (stack.isEmpty()) {
			return Collections.emptyList();
		}
		List<ITextComponent> tooltip = stack.getTooltip(player, flag);
		for (int i = 0; i < tooltip.size(); ++i) {
			//TODO - can tis be simplified (and is it correct?)
			ITextComponent component = tooltip.get(i);
			if (i == 0) {
				tooltip.set(i, component.applyTextStyle(stack.getRarity().color));
			} else {
				tooltip.set(i, component.applyTextStyle(TextFormatting.GRAY));
			}
		}
		return tooltip;
	}
}
