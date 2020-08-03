package forestry.api.genetics.alyzer;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.apiculture.genetics.AlyzerInfo;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;

public interface IAlyzerDisplayProvider<I extends IIndividual> {
    default AlyzerInfo getInfo() {
        return AlyzerInfo.EMPTY;
    }

    /**
     * Called at {@link ContainerScreen#init(Minecraft, int, int)} in the alyzer gui. Can be used to add custom widgets
     * to the gui.
     */
    default void initAlyzer(IAlyzerHelper helper) {
    }

    /**
     * Used to display the handled allele on the alyzer.
     */
    default void drawAlyzer(IAlyzerHelper helper, IGenome genome, double mouseX, double mouseY, MatrixStack transform) {
    }
}
