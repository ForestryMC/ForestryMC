package forestry.core.recipes.json;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import forestry.arboriculture.IWoodTyped;
import forestry.core.utils.Log;
import forestry.modules.ModuleHelper;


public class RecipeConverter {

	// You can include this in your mod/a pack/whatever you want, as long as that work follows the Mojang EULA.
	// The original source is viewable at https://gist.github.com/williewillus/a1a899ce5b0f0ba099078d46ae3dae6e

	// This is a janky JSON generator, for porting from below 1.12 to 1.12.
	// Simply replace calls to GameRegistry.addShapeless/ShapedRecipe with these methods, which will dump it to a json in RECIPE_DIR
	// Also works with OD, replace GameRegistry.addRecipe(new ShapedOreRecipe/ShapelessOreRecipe with the same calls
	// After you are done, call generateConstants()
	// Note that in many cases, you can combine multiple old recipes into one, since you can now specify multiple possibilities for an ingredient without using the OD. See vanilla for examples.
	//example: crafting_table.json
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static File RECIPE_DIR = null;
	private static final Set<String> USED_OD_NAMES = new TreeSet<>();

	private static void setupDir() {
		if (RECIPE_DIR == null) {
			File rootDir = new File("");
			RECIPE_DIR = new File(rootDir.getAbsolutePath() + "/../", "src/main/resources/assets/forestry/recipes");
		}

		if (!RECIPE_DIR.exists()) {
			RECIPE_DIR.mkdir();
		}
	}

	private static File setupModuleDir(String moduleUID) {
		File moduleDir = new File(RECIPE_DIR.getAbsolutePath(), "/" + moduleUID);
		moduleDir.mkdirs();
		return moduleDir;
	}

	private static Object getConditions(String moduleUID) {
		List<Map<String, Object>> conditions = new ArrayList<>();
		Map<String, Object> module = new HashMap<>();
		module.put("type", "module");
		module.put("module", moduleUID);
		conditions.add(module);
		return conditions;
	}

	public static void addShapedRecipe(ItemStack result, String moduleUID, Object... components) {
		setupDir();
		File moduleDIR = setupModuleDir(moduleUID);
		if (!ModuleHelper.isEnabled(moduleUID)) {
			throw new IllegalArgumentException(String.format("Tried to register a recipe for a module that is not enabled! %s", ForgeRegistries.ITEMS.getKey(result.getItem()).toString()));
		}

		// GameRegistry.addShapedRecipe(result, components);

		Map<String, Object> json = new HashMap<>();


		json.put("conditions", getConditions(moduleUID));
		List<String> pattern = new ArrayList<>();
		int i = 0;
		while (i < components.length && components[i] instanceof String) {
			pattern.add((String) components[i]);
			i++;
		}
		json.put("pattern", pattern);
		json.put("group", moduleUID);

		boolean isOreDict = false;
		Map<String, Map<String, Object>> key = new HashMap<>();
		Character curKey = null;
		for (; i < components.length; i++) {
			Object o = components[i];
			if (o instanceof Character) {
				if (curKey != null) {
					throw new IllegalArgumentException("Provided two char keys in a row");
				}
				curKey = (Character) o;
			} else {
				if (curKey == null) {
					throw new IllegalArgumentException("Providing object without a char key");
				}
				if (o instanceof String) {
					isOreDict = true;
				}
				key.put(Character.toString(curKey), serializeItem(o));
				curKey = null;
			}
		}
		json.put("key", key);
		json.put("type", isOreDict ? "forge:ore_shaped" : "minecraft:crafting_shaped");
		json.put("result", serializeItem(result));

		// names the json the same name as the output's registry name
		// repeatedly adds _alt if a file already exists
		// janky I know but it works
		String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
		File f = new File(moduleDIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

		while (f.exists()) {
			suffix += "_alt";
			f = new File(moduleDIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");
		}

		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}

	public static void addCombBlockRecipe(ItemStack result, String moduleUID, Object... components) {
		setupDir();
		File moduleDIR = setupModuleDir(moduleUID);
		if (!ModuleHelper.isEnabled(moduleUID)) {
			throw new IllegalArgumentException(String.format("Tried to register a recipe for a module that is not enabled! %s", ForgeRegistries.ITEMS.getKey(result.getItem()).toString()));
		}

		// GameRegistry.addShapedRecipe(result, components);

		Map<String, Object> json = new HashMap<>();


		json.put("conditions", getConditions(moduleUID));
		List<String> pattern = new ArrayList<>();
		int i = 0;
		while (i < components.length && components[i] instanceof String) {
			pattern.add((String) components[i]);
			i++;
		}
		json.put("pattern", pattern);
		json.put("group", moduleUID);

		boolean isOreDict = false;
		Map<String, Map<String, Object>> key = new HashMap<>();
		Character curKey = null;
		for (; i < components.length; i++) {
			Object o = components[i];
			if (o instanceof Character) {
				if (curKey != null) {
					throw new IllegalArgumentException("Provided two char keys in a row");
				}
				curKey = (Character) o;
			} else {
				if (curKey == null) {
					throw new IllegalArgumentException("Providing object without a char key");
				}
				if (o instanceof String) {
					isOreDict = true;
				}
				key.put(Character.toString(curKey), serializeItem(o));
				curKey = null;
			}
		}
		json.put("key", key);
		json.put("type", "forestry:comb_block");
		json.put("result", serializeItem(result));

		// names the json the same name as the output's registry name
		// repeatedly adds _alt if a file already exists
		// janky I know but it works
		String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
		File f = new File(moduleDIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

		while (f.exists()) {
			suffix += "_alt";
			f = new File(moduleDIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");
		}

		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}

	public static void addFarmBlockRecipe(ItemStack result, String moduleUID, Object... components) {
		setupDir();
		File moduleDIR = setupModuleDir(moduleUID);
		if (!ModuleHelper.isEnabled(moduleUID)) {
			throw new IllegalArgumentException(String.format("Tried to register a recipe for a module that is not enabled! %s", ForgeRegistries.ITEMS.getKey(result.getItem()).toString()));
		}

		// GameRegistry.addShapedRecipe(result, components);

		Map<String, Object> json = new HashMap<>();


		json.put("conditions", getConditions(moduleUID));
		List<String> pattern = new ArrayList<>();
		int i = 0;
		while (i < components.length && components[i] instanceof String) {
			pattern.add((String) components[i]);
			i++;
		}
		json.put("pattern", pattern);
		json.put("group", moduleUID);

		boolean isOreDict = false;
		Map<String, Map<String, Object>> key = new HashMap<>();
		Character curKey = null;
		for (; i < components.length; i++) {
			Object o = components[i];
			if (o instanceof Character) {
				if (curKey != null) {
					throw new IllegalArgumentException("Provided two char keys in a row");
				}
				curKey = (Character) o;
			} else {
				if (curKey == null) {
					throw new IllegalArgumentException("Providing object without a char key");
				}
				if (o instanceof String) {
					isOreDict = true;
				}
				key.put(Character.toString(curKey), serializeItem(o));
				curKey = null;
			}
		}
		json.put("key", key);
		json.put("type", "forestry:farm_block");
		json.put("result", serializeItem(result));

		// names the json the same name as the output's registry name
		// repeatedly adds _alt if a file already exists
		// janky I know but it works
		String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
		File f = new File(moduleDIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

		while (f.exists()) {
			suffix += "_alt";
			f = new File(moduleDIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");
		}

		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}

	public static void addShapelessRecipe(ItemStack result, String moduleUID, Object... components) {
		setupDir();
		File moduleDIR = setupModuleDir(moduleUID);

		if (!ModuleHelper.isEnabled(moduleUID)) {
			throw new IllegalArgumentException(String.format("Tried to register a recipe for a module that is not enabled! %s", ForgeRegistries.ITEMS.getKey(result.getItem()).toString()));
		}

		// addShapelessRecipe(result, components);

		Map<String, Object> json = new HashMap<>();

		json.put("conditions", getConditions(moduleUID));
		boolean isOreDict = false;
		List<Map<String, Object>> ingredients = new ArrayList<>();
		for (Object o : components) {
			if (o instanceof String) {
				isOreDict = true;
			}
			ingredients.add(serializeItem(o));
		}
		json.put("ingredients", ingredients);
		json.put("type", isOreDict ? "forge:ore_shapeless" : "minecraft:crafting_shapeless");
		json.put("result", serializeItem(result));
		json.put("group", moduleUID);


		// names the json the same name as the output's registry name
		// repeatedly adds _alt if a file already exists
		// janky I know but it works
		String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
		File f = new File(moduleDIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

		while (f.exists()) {
			suffix += "_alt";
			f = new File(moduleDIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");
		}


		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}

	public static void addRecipeMultipleIngredients(ItemStack result, String moduleUID, Object... components) {
		setupDir();
		File moduleDIR = setupModuleDir(moduleUID);

		if (!ModuleHelper.isEnabled(moduleUID)) {
			throw new IllegalArgumentException(String.format("Tried to register a recipe for a module that is not enabled! %s", ForgeRegistries.ITEMS.getKey(result.getItem()).toString()));
		}

		// GameRegistry.addShapedRecipe(result, components);

		Map<String, Object> json = new HashMap<>();


		json.put("conditions", getConditions(moduleUID));
		List<String> pattern = new ArrayList<>();
		int i = 0;
		while (i < components.length && components[i] instanceof String) {
			pattern.add((String) components[i]);
			i++;
		}
		json.put("pattern", pattern);
		json.put("group", moduleUID);


		boolean isOreDict = false;
		Map<String, Object> key = new HashMap<>();
		Character curKey = null;
		for (; i < components.length; i++) {
			Object o = components[i];
			if (o instanceof Character) {
				if (curKey != null) {
					throw new IllegalArgumentException("Provided two char keys in a row");
				}
				curKey = (Character) o;
			} else {
				if (curKey == null) {
					throw new IllegalArgumentException("Providing object without a char key");
				}
				if (!(o instanceof List)) {
					if (o instanceof String) {
						isOreDict = true;
					}
					key.put(Character.toString(curKey), serializeItem(o));
					curKey = null;
				} else {
					List<Map<String, Object>> inredientOptionsMap = new ArrayList<>();
					for (Object obj : (List) o) {
						if (obj instanceof String) {
							isOreDict = true;
						}
						inredientOptionsMap.add(serializeItem(obj));
					}
					key.put(Character.toString(curKey), inredientOptionsMap);
					curKey = null;

				}
			}
		}
		json.put("key", key);
		json.put("type", isOreDict ? "forge:ore_shaped" : "minecraft:crafting_shaped");
		json.put("result", serializeItem(result));

		// names the json the same name as the output's registry name
		// repeatedly adds _alt if a file already exists
		// janky I know but it works
		String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
		File f = new File(moduleDIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

		while (f.exists()) {
			suffix += "_alt";
			f = new File(moduleDIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");
		}

		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}

	public static void addRecipeMultipleIngredientsShapeless(ItemStack result, String moduleUID, Object... components) {
		setupDir();
		File moduleDIR = setupModuleDir(moduleUID);

		if (!ModuleHelper.isEnabled(moduleUID)) {
			throw new IllegalArgumentException(String.format("Tried to register a recipe for a module that is not enabled! %s", ForgeRegistries.ITEMS.getKey(result.getItem()).toString()));
		}

		// addShapelessRecipe(result, components);

		Map<String, Object> json = new HashMap<>();

		json.put("conditions", getConditions(moduleUID));
		boolean isOreDict = false;
		List<Object> ingredients = new ArrayList<>();
		for (Object o : components) {
			if (!(o instanceof List)) {
				if (o instanceof String) {
					isOreDict = true;
				}
				ingredients.add(serializeItem(o));
			} else {
				List<Object> possibleIngredients = Lists.newArrayList();
				for (Object obj : (List) o) {
					possibleIngredients.add(serializeItem(obj));
				}
				ingredients.add(possibleIngredients);
			}
		}
		json.put("ingredients", ingredients);
		json.put("type", isOreDict ? "forge:ore_shapeless" : "minecraft:crafting_shapeless");
		json.put("result", serializeItem(result));
		json.put("group", moduleUID);


		// names the json the same name as the output's registry name
		// repeatedly adds _alt if a file already exists
		// janky I know but it works
		String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
		File f = new File(moduleDIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

		while (f.exists()) {
			suffix += "_alt";
			f = new File(moduleDIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");
		}


		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}

	public static void addWoodTypeRecipe(String moduleUID, IWoodTyped inputType, IWoodTyped outputType, String[] recipe, int outputCount) {
		setupDir();
		File moduleDir = setupModuleDir(moduleUID);

		if (!ModuleHelper.isEnabled(moduleUID)) {
			throw new IllegalArgumentException("Tried to register a wood type recipe for a module that is not enabled!");
		}

		Map<String, Object> json = new HashMap<>();
		json.put("conditions", getConditions(moduleUID));
		json.put("type", "forestry:wood_type");
		json.put("pattern", Arrays.asList(recipe));

		Map<String, Object> input = new HashMap<>();
		input.put("blockKind", inputType.getBlockKind());
		input.put("fireproof", inputType.isFireproof());
		json.put("input", input);

		Map<String, Object> output = new HashMap<>();
		output.put("blockKind", outputType.getBlockKind());
		output.put("fireproof", outputType.isFireproof());
		if(outputCount != 1) {
			output.put("count", outputCount);
		}
		json.put("output", output);
		json.put("group", moduleUID);


		String fpsuffix = inputType.isFireproof() ? "_fireproof" : "";
		String fileName = inputType.getBlockKind().getName() + "_" + outputType.getBlockKind().getName() + fpsuffix;
		File f = new File(moduleDir, fileName + ".json");
		String suffix = "";
		while (f.exists()) {
			suffix += "_alt";
			f = new File(moduleDir, fileName + suffix + ".json");
		}


		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}

	public static void addWoodTypeRecipeShapeless(String moduleUID, IWoodTyped inputType, IWoodTyped outputType, int outputCount) {
		setupDir();
		File moduleDir = setupModuleDir(moduleUID);

		if (!ModuleHelper.isEnabled(moduleUID)) {
			throw new IllegalArgumentException("Tried to register a wood type recipe for a module that is not enabled!");
		}

		Map<String, Object> json = new HashMap<>();
		json.put("conditions", getConditions(moduleUID));
		json.put("type", "forestry:wood_type");

		Map<String, Object> input = new HashMap<>();
		input.put("blockKind", inputType.getBlockKind());
		input.put("fireproof", inputType.isFireproof());
		json.put("input", input);

		Map<String, Object> output = new HashMap<>();
		output.put("blockKind", outputType.getBlockKind());
		output.put("fireproof", outputType.isFireproof());
		if(outputCount != 1) {
			output.put("count", outputCount);
		}
		json.put("output", output);

		String fpsuffix = inputType.isFireproof() ? "_fireproof" : "";
		String fileName = inputType.getBlockKind().getName() + "_" + outputType.getBlockKind().getName() + fpsuffix;
		File f = new File(moduleDir, fileName + ".json");
		String suffix = "";
		while (f.exists()) {
			suffix += "_alt";
			f = new File(moduleDir, fileName + suffix + ".json");
		}


		try (FileWriter w = new FileWriter(f)) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}


	private static Map<String, Object> serializeItem(Object thing) {
		if (thing instanceof Item) {
			return serializeItem(new ItemStack((Item) thing));
		}
		if (thing instanceof Block) {
			return serializeItem(new ItemStack((Block) thing));
		}
		if (thing instanceof ItemStack) {
			ItemStack stack = (ItemStack) thing;
			Map<String, Object> ret = new HashMap<>();
			ret.put("item", stack.getItem().getRegistryName().toString());
			if (stack.getItem().getHasSubtypes() || stack.getItemDamage() != 0) {
				ret.put("data", stack.getItemDamage());
			}
			if (stack.getCount() > 1) {
				ret.put("count", stack.getCount());
			}

			if (stack.hasTagCompound()) {
				ret.put("type", "minecraft:item_nbt");
				ret.put("nbt", stack.getTagCompound().toString());
			}

			return ret;
		}
		if (thing instanceof String) {
			Map<String, Object> ret = new HashMap<>();
			USED_OD_NAMES.add((String) thing);
			ret.put("item", "#" + ((String) thing).toUpperCase(Locale.ROOT));
			return ret;
		}

		throw new IllegalArgumentException("Not a block, item, stack, or od name");
	}

	// Call this after you are done generating
	public static void generateConstants() {
		List<Map<String, Object>> json = new ArrayList<>();
		for (String s : USED_OD_NAMES) {
			Map<String, Object> entry = new HashMap<>();
			entry.put("name", s.toUpperCase(Locale.ROOT));
			entry.put("ingredient", ImmutableMap.of("type", "forge:ore_dict", "ore", s));
			json.add(entry);
		}

		try (FileWriter w = new FileWriter(new File(RECIPE_DIR, "_constants.json"))) {
			GSON.toJson(json, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteRecipes() {
		setupDir();
		if (!RECIPE_DIR.exists()) {
			return;
		}
		for (File file : RECIPE_DIR.listFiles()) {
			String fileName = file.getName();
			if (!fileName.startsWith("_")) {
				if (file.isDirectory()) {
					try {
						FileUtils.deleteDirectory(file);
					} catch (IOException e) {
						Log.error("Error resetting file %s!", file.getAbsolutePath());
					}
				} else {
					file.delete();
				}
			}
		}
	}
}
