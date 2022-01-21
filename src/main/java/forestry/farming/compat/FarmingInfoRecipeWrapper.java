package forestry.farming.compat;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import com.mojang.blaze3d.vertex.PoseStack;

import forestry.api.circuits.ICircuit;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmableInfo;
import forestry.api.farming.Soil;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;

public class FarmingInfoRecipeWrapper implements IRecipeCategoryExtension {
	private final ItemStack tube;
	private final IFarmProperties properties;
	private final ICircuit circuit;

	public FarmingInfoRecipeWrapper(ItemStack tube, IFarmProperties properties, ICircuit circuit) {
		this.tube = tube;
		this.properties = properties;
		this.circuit = circuit;
	}

	private static <T> void splitItems(List<List<ItemStack>> items, int startIndex, List<T> values, Function<T, ItemStack> itemFunction) {
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

		if (stack != null) {
			itemList.add(stack);
		}
	}

	@Override
	public void setIngredients(IIngredients ingredients) {
		List<List<ItemStack>> inputStacks = new ArrayList<>(9);
		List<List<ItemStack>> outputStacks = new ArrayList<>(4);
		inputStacks.add(Collections.singletonList(tube));

		List<Soil> soils = new ArrayList<>(properties.getSoils());
		splitItems(inputStacks, 1, soils, Soil::getResource);

		Collection<IFarmableInfo> farmableInfo = properties.getFarmableInfo();
		List<ItemStack> germlings = farmableInfo.stream().map(IFarmableInfo::getSeedlings).flatMap(Collection::stream).collect(Collectors.toList());
		splitItems(inputStacks, 5, germlings, item -> item);

		List<ItemStack> productions = farmableInfo.stream().map(IFarmableInfo::getProducts).flatMap(Collection::stream).collect(Collectors.toList());
		splitItems(outputStacks, 0, productions, item -> item);

		ingredients.setInputLists(VanillaTypes.ITEM, inputStacks);
		ingredients.setOutputLists(VanillaTypes.ITEM, outputStacks);
	}

	@Override
	public void drawInfo(int recipeWidth, int recipeHeight, PoseStack matrixStack, double mouseX, double mouseY) {
		Font fontRenderer = Minecraft.getInstance().font;
		fontRenderer.draw(matrixStack, circuit.getDisplayName(), (float) (recipeWidth - fontRenderer.width(circuit.getDisplayName().getString())) / 2, 3, Color.darkGray.getRGB());

		Component soilName = new TranslatableComponent("for.jei.farming.soil");
		fontRenderer.draw(matrixStack, soilName, 18 - (float) (fontRenderer.width(soilName.getString())) / 2, 45, Color.darkGray.getRGB());

		Component germlingsName = new TranslatableComponent("for.jei.farming.germlings");
		fontRenderer.draw(matrixStack, germlingsName, (float) (recipeWidth - fontRenderer.width(germlingsName.getString())) / 2, 45, Color.darkGray.getRGB());

		//TODO: draw
		Component productsName = new TranslatableComponent("for.jei.farming.products");
		fontRenderer.draw(matrixStack, productsName, 126 - (float) (fontRenderer.width(productsName.getString())) / 2, 45, Color.darkGray.getRGB());
	}
}
