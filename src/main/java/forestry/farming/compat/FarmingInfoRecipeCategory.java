package forestry.farming.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import forestry.api.circuits.ICircuit;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmableInfo;
import forestry.api.farming.Soil;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.config.Constants;
import forestry.core.features.CoreItems;
import forestry.core.recipes.jei.ForestryRecipeCategory;
import forestry.core.utils.JeiUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class FarmingInfoRecipeCategory extends ForestryRecipeCategory<FarmingInfoRecipe> {
	public static final RecipeType<FarmingInfoRecipe> TYPE = RecipeType.create(Constants.MOD_ID, "farming", FarmingInfoRecipe.class);
	private final IDrawable slotDrawable;
	private final IDrawable addition;
	private final IDrawable arrow;
	private final IDrawable icon;

	public FarmingInfoRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper.createBlankDrawable(144, 90), "for.jei.farming");
		this.slotDrawable = guiHelper.getSlotDrawable();
		ResourceLocation resourceLocation = new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_GUI + "/jei/recipes.png");
		addition = guiHelper.createDrawable(resourceLocation, 44, 0, 15, 15);
		arrow = guiHelper.createDrawable(resourceLocation, 59, 0, 15, 15);
		ItemStack intricateCircuitboard = new ItemStack(CoreItems.CIRCUITBOARDS.get(EnumCircuitBoardType.INTRICATE));
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, intricateCircuitboard);
	}

	@Override
	public RecipeType<FarmingInfoRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, FarmingInfoRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 64, 19)
				.setBackground(slotDrawable, -1, -1)
				.addItemStack(recipe.tube());

		IFarmProperties properties = recipe.properties();
		Collection<IFarmableInfo> farmableInfo = properties.getFarmableInfo();

		{
			List<ItemStack> soils = properties.getSoils().stream()
					.map(Soil::getResource)
					.toList();

			List<IRecipeSlotBuilder> soilSlots = JeiUtil.layoutSlotGrid(builder, RecipeIngredientRole.INPUT, 2, 2, 1, 55, 18);
			soilSlots.forEach(slot -> slot.setBackground(slotDrawable, -1, -1));
			distributeItems(soilSlots, soils);
		}


		{
			List<ItemStack> germlings = farmableInfo.stream()
					.map(IFarmableInfo::getSeedlings)
					.flatMap(Collection::stream)
					.toList();

			List<IRecipeSlotBuilder> germlingSlots = JeiUtil.layoutSlotGrid(builder, RecipeIngredientRole.INPUT, 2, 2, 55, 55, 18);
			germlingSlots.forEach(slot -> slot.setBackground(slotDrawable, -1, -1));
			distributeItems(germlingSlots, germlings);
		}

		{
			List<ItemStack> products = farmableInfo.stream()
					.map(IFarmableInfo::getProducts)
					.flatMap(Collection::stream)
					.toList();

			List<IRecipeSlotBuilder> productSlots = JeiUtil.layoutSlotGrid(builder, RecipeIngredientRole.OUTPUT, 2, 2, 109, 55, 18);
			productSlots.forEach(slot -> slot.setBackground(slotDrawable, -1, -1));
			distributeItems(productSlots, products);
		}
	}

	@Override
	public void draw(FarmingInfoRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
		addition.draw(stack, 37, 64);
		arrow.draw(stack, 91, 64);
		int recipeWidth = this.getBackground().getWidth();
		Font fontRenderer = Minecraft.getInstance().font;
		ICircuit circuit = recipe.circuit();
		float textX = (float) (recipeWidth - fontRenderer.width(circuit.getDisplayName().getString())) / 2;
		fontRenderer.draw(stack, circuit.getDisplayName(), textX, 3, Color.darkGray.getRGB());

		Component soilName = Component.translatable("for.jei.farming.soil");
		fontRenderer.draw(stack, soilName, 18 - (float) (fontRenderer.width(soilName.getString())) / 2, 45, Color.darkGray.getRGB());

		Component germlingsName = Component.translatable("for.jei.farming.germlings");
		fontRenderer.draw(stack, germlingsName, (float) (recipeWidth - fontRenderer.width(germlingsName.getString())) / 2, 45, Color.darkGray.getRGB());

		//TODO: draw
		Component productsName = Component.translatable("for.jei.farming.products");
		fontRenderer.draw(stack, productsName, 126 - (float) (fontRenderer.width(productsName.getString())) / 2, 45, Color.darkGray.getRGB());
	}

	private static void distributeItems(List<IRecipeSlotBuilder> recipeSlots, List<ItemStack> items) {
		for (int i = 0; i < items.size(); i++) {
			ItemStack itemStack = items.get(i);
			IRecipeSlotBuilder recipeSlot = recipeSlots.get(i % recipeSlots.size());
			recipeSlot.addItemStack(itemStack);
		}
	}
}
