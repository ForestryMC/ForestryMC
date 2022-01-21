package forestry.api.genetics.alyzer;

import net.minecraft.client.Minecraft;

import com.mojang.blaze3d.vertex.PoseStack;

import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;

import forestry.apiculture.genetics.AlyzerInfo;

public interface IAlyzerDisplayProvider<I extends IIndividual> {
	default AlyzerInfo getInfo() {
		return AlyzerInfo.EMPTY;
	}

	/**
	 * Called at {@link net.minecraft.client.gui.screens.inventory.ContainerScreen#init(Minecraft, int, int)} in the alyzer gui. Can be used to add custom widgets
	 * to the gui.
	 */
	default void initAlyzer(IAlyzerHelper helper) {
	}

	/**
	 * Used to display the handled allele on the alyzer.
	 */
	default void drawAlyzer(IAlyzerHelper helper, IGenome genome, double mouseX, double mouseY, PoseStack transform) {
	}
}
