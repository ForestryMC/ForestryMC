package forestry.farming.compat;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.circuits.ICircuit;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmableInfo;
import forestry.api.farming.Soil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FarmingInfoRecipeWrapper implements IRecipeCategoryExtension {
    private final ItemStack tube;
    private final IFarmProperties properties;
    private final ICircuit circuit;

    public FarmingInfoRecipeWrapper(ItemStack tube, IFarmProperties properties, ICircuit circuit) {
        this.tube = tube;
        this.properties = properties;
        this.circuit = circuit;
    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, MatrixStack matrixStack, double mouseX, double mouseY) {
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        fontRenderer.func_243248_b(
                matrixStack,
                circuit.getDisplayName(),
                (float) (recipeWidth - fontRenderer.getStringWidth(circuit.getDisplayName().getString())) / 2,
                3,
                Color.darkGray.getRGB()
        );

        ITextComponent soilName = new TranslationTextComponent("for.jei.farming.soil");
        fontRenderer.func_243248_b(
                matrixStack,
                soilName,
                18 - (float) (fontRenderer.getStringWidth(soilName.getString())) / 2,
                45,
                Color.darkGray.getRGB()
        );

        ITextComponent germlingsName = new TranslationTextComponent("for.jei.farming.germlings");
        fontRenderer.func_243248_b(
                matrixStack,
                germlingsName,
                (float) (recipeWidth - fontRenderer.getStringWidth(germlingsName.getString())) / 2,
                45,
                Color.darkGray.getRGB()
        );

        ITextComponent productsName = new TranslationTextComponent("for.jei.farming.products");
        fontRenderer.func_243248_b(
                matrixStack,
                productsName,
                126 - (float) (fontRenderer.getStringWidth(productsName.getString())) / 2,
                45,
                Color.darkGray.getRGB()
        );
    }

    @Override
    public void setIngredients(IIngredients ingredients) {
        List<List<ItemStack>> inputStacks = new ArrayList<>(9);
        List<List<ItemStack>> outputStacks = new ArrayList<>(4);
        inputStacks.add(Collections.singletonList(tube));
        List<Soil> soils = new ArrayList<>(properties.getSoils());
        splitItems(inputStacks, 1, soils, Soil::getResource);
        Collection<IFarmableInfo> farmableInfo = properties.getFarmableInfo();
        List<ItemStack> germlings = farmableInfo.stream()
                                                .map(IFarmableInfo::getSeedlings)
                                                .flatMap(Collection::stream)
                                                .collect(Collectors.toList());
        splitItems(inputStacks, 5, germlings, item -> item);
        List<ItemStack> productions = farmableInfo.stream()
                                                  .map(IFarmableInfo::getProducts)
                                                  .flatMap(Collection::stream)
                                                  .collect(Collectors.toList());
        splitItems(outputStacks, 0, productions, item -> item);
        ingredients.setInputLists(VanillaTypes.ITEM, inputStacks);
        ingredients.setOutputLists(VanillaTypes.ITEM, outputStacks);
    }

    private static <T> void splitItems(
            List<List<ItemStack>> items,
            int startIndex,
            List<T> values,
            Function<T, ItemStack> itemFunction
    ) {
        int count = values.size();
        if (count == 0 || count % 4 != 0) {
            count += (4 - count % 4);
        }

        for (int i = 0; i < count; i++) {
            int index = startIndex + i % 4;
            ItemStack stack;
            if (values.size() > i) {
                stack = itemFunction.apply(values.get(i));
            } else {
                stack = null;
            }

            addItemToList(items, index, stack);
        }
    }

    private static void addItemToList(List<List<ItemStack>> items, int index, @Nullable ItemStack stack) {
        List<ItemStack> itemList;
        if (items.size() > index) {
            itemList = items.get(index);
            if (itemList == null) {
                itemList = new ArrayList<>();
                items.set(index, itemList);
            }
        } else {
            itemList = new ArrayList<>();
            items.add(itemList);
        }

        itemList.add(stack);
    }
}
