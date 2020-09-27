package forestry.book.data.content;

import forestry.api.book.BookContent;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.book.data.CraftingData;
import forestry.book.gui.elements.FabricatorElement;
import forestry.core.gui.elements.lib.IElementGroup;
import forestry.core.gui.elements.lib.IGuiElement;
import forestry.core.gui.elements.lib.IGuiElementFactory;
import forestry.factory.recipes.FabricatorSmeltingRecipeManager;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class FabricatorContent extends BookContent<CraftingData> {

    @Override
    public Class<? extends CraftingData> getDataClass() {
        return CraftingData.class;
    }

    @Override
    public boolean addElements(
            IElementGroup page,
            IGuiElementFactory factory,
            @Nullable BookContent previous,
            @Nullable IGuiElement previousElement,
            int pageHeight
    ) {
        if (data == null || (data.stack.isEmpty() && data.stacks.length == 0)) {
            return false;
        }
        if (!data.stack.isEmpty()) {
            page.add(new FabricatorElement(0, 0, data.stack));
        } else {
            page.add(new FabricatorElement(0, 0, data.stacks));
        }
        return true;
    }

    private static Map<Fluid, List<IFabricatorSmeltingRecipe>> getSmeltingInputs() {
        Map<Fluid, List<IFabricatorSmeltingRecipe>> smeltingInputs = new HashMap<>();
        for (IFabricatorSmeltingRecipe smelting : FabricatorSmeltingRecipeManager.recipes) {
            Fluid fluid = smelting.getProduct().getFluid();
            if (!smeltingInputs.containsKey(fluid)) {
                smeltingInputs.put(fluid, new ArrayList<>());
            }
            smeltingInputs.get(fluid).add(smelting);
        }
        return smeltingInputs;
    }
}
