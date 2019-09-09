//package forestry.farming.compat;
//
//import javax.annotation.Nullable;
//import java.awt.Color;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.FontRenderer;
//import net.minecraft.item.ItemStack;
//
//import forestry.api.circuits.ICircuit;
//import forestry.api.farming.IFarmProperties;
//import forestry.api.farming.IFarmableInfo;
//import forestry.api.farming.ISoil;
//import forestry.core.utils.Translator;
//
//import mezz.jei.api.ingredients.IIngredients;
//import mezz.jei.api.ingredients.VanillaTypes;
//import mezz.jei.api.recipe.IRecipeWrapper;
//
//public class FarmingInfoRecipeWrapper implements IRecipeWrapper {
//	private final ItemStack tube;
//	private final IFarmProperties properties;
//	private final ICircuit circuit;
//
//	public FarmingInfoRecipeWrapper(ItemStack tube, IFarmProperties properties, ICircuit circuit) {
//		this.tube = tube;
//		this.properties = properties;
//		this.circuit = circuit;
//	}
//
//	@Override
//	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
//		FontRenderer fontRenderer = minecraft.fontRenderer;
//		String circuitName = Translator.translateToLocal(circuit.getUnlocalizedName());
//		fontRenderer.drawString(circuitName, (recipeWidth - fontRenderer.getStringWidth(circuitName)) / 2, 3, Color.darkGray.getRGB());
//		String soilName = Translator.translateToLocal("for.jei.farming.soil");
//		fontRenderer.drawString(soilName, 18 - (fontRenderer.getStringWidth(soilName)) / 2, 45, Color.darkGray.getRGB());
//		String germlingsName = Translator.translateToLocal("for.jei.farming.germlings");
//		fontRenderer.drawString(germlingsName, (recipeWidth - fontRenderer.getStringWidth(germlingsName)) / 2, 45, Color.darkGray.getRGB());
//		String productsName = Translator.translateToLocal("for.jei.farming.products");
//		fontRenderer.drawString(productsName, 126 - (fontRenderer.getStringWidth(productsName)) / 2, 45, Color.darkGray.getRGB());
//	}
//
//	@Override
//	public void getIngredients(IIngredients ingredients) {
//		List<List<ItemStack>> inputStacks = new ArrayList<>(9);
//		List<List<ItemStack>> outputStacks = new ArrayList<>(4);
//		inputStacks.add(Collections.singletonList(tube));
//		List<ISoil> soils = new ArrayList<>(properties.getSoils());
//		splitItems(inputStacks, 1, soils, ISoil::getResource);
//		Collection<IFarmableInfo> farmableInfo = properties.getFarmableInfo();
//		List<ItemStack> germlings = farmableInfo.stream()
//			.map(IFarmableInfo::getGermlings)
//			.flatMap(Collection::stream)
//			.collect(Collectors.toList());
//		splitItems(inputStacks, 5, germlings, item -> item);
//		List<ItemStack> productions = farmableInfo.stream()
//			.map(IFarmableInfo::getProducts)
//			.flatMap(Collection::stream)
//			.collect(Collectors.toList());
//		splitItems(outputStacks, 0, productions, item -> item);
//		ingredients.setInputLists(VanillaTypes.ITEM, inputStacks);
//		ingredients.setOutputLists(VanillaTypes.ITEM, outputStacks);
//	}
//
//	private static <T> void splitItems(List<List<ItemStack>> items, int startIndex, List<T> values, Function<T, ItemStack> itemFunction) {
//		int count = values.size();
//		if (count == 0 || count % 4 != 0) {
//			count += (4 - count % 4);
//		}
//		for (int i = 0; i < count; i++) {
//			int index = startIndex + i % 4;
//			ItemStack stack;
//			if (values.size() > i) {
//				stack = itemFunction.apply(values.getComb(i));
//			} else {
//				stack = null;
//			}
//			addItemToList(items, index, stack);
//		}
//	}
//
//	private static void addItemToList(List<List<ItemStack>> items, int index, @Nullable ItemStack stack) {
//		List<ItemStack> itemList;
//		if (items.size() > index) {
//			itemList = items.getComb(index);
//			if (itemList == null) {
//				itemList = new ArrayList<>();
//				items.set(index, itemList);
//			}
//		} else {
//			itemList = new ArrayList<>();
//			items.add(itemList);
//		}
//		itemList.add(stack);
//	}
//}
