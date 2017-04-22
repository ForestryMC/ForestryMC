package forestry.factory.recipes.jei.rainmaker;

import java.awt.Color;
import java.util.Collections;

import forestry.api.fuels.RainSubstrate;
import forestry.core.utils.Translator;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RainmakerRecipeWrapper extends BlankRecipeWrapper {
	private final RainSubstrate substrate;

	public RainmakerRecipeWrapper(RainSubstrate substrate) {
		this.substrate = substrate;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputs(ItemStack.class, Collections.singletonList(substrate.getItem()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);

		minecraft.fontRendererObj.drawString(getEffectString(), 24, 0, Color.darkGray.getRGB());
		String speed = Translator.translateToLocalFormatted("for.jei.rainmaker.speed", substrate.getSpeed());
		minecraft.fontRendererObj.drawString(speed, 24, 10, Color.gray.getRGB());
		if (!substrate.isReverse()) {
			String duration = Translator.translateToLocalFormatted("for.jei.rainmaker.duration", substrate.getDuration());
			minecraft.fontRendererObj.drawString(duration, 24, 20, Color.gray.getRGB());
		}
	}

	private String getEffectString() {
		if (substrate.isReverse()) {
			return Translator.translateToLocal("for.jei.rainmaker.stops.rain");
		} else {
			return Translator.translateToLocal("for.jei.rainmaker.causes.rain");
		}
	}
}
