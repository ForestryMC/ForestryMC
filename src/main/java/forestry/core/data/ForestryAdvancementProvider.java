package forestry.core.data;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.TickTrigger;
import net.minecraft.command.FunctionObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.core.config.Constants;

import genetics.api.GeneticHelper;

public class ForestryAdvancementProvider implements IDataProvider {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

	private final DataGenerator generator;

	public ForestryAdvancementProvider(DataGenerator generator) {
		this.generator = generator;
	}

	@Override
	public void run(DirectoryCache cache) throws IOException {
		Path outputFolder = this.generator.getOutputFolder();
		Set<ResourceLocation> set = Sets.newHashSet();

		build(advancement -> {
			if (!set.add(advancement.getId())) {
				throw new IllegalStateException("Duplicate advancement " + advancement.getId());
			} else {
				Path path = createPath(outputFolder, advancement);

				try {
					IDataProvider.save(GSON, cache, advancement.deconstruct().serializeToJson(), path);
				} catch (IOException ioexception) {
					LOGGER.error("Couldn't save advancement {}", path, ioexception);
				}
			}
		});
	}

	private void build(Consumer<Advancement> consumer) {
		ItemStack icon = new ItemStack(ApicultureItems.BEE_QUEEN.getItem());
		GeneticHelper.setIndividual(icon, BeeDefinition.INDUSTRIOUS.createIndividual());

		Advancement.Builder.advancement()
				.display(icon, new TranslationTextComponent("advancements.forestry.root.title"), new TranslationTextComponent("advancements.forestry.root.description"), new ResourceLocation("textures/block/honeycomb_block.png"), FrameType.TASK, false, false, false)
				.addCriterion("tick", new TickTrigger.Instance(EntityPredicate.AndPredicate.ANY))
				.rewards(new AdvancementRewards(0, new ResourceLocation[]{
						new ResourceLocation(Constants.MOD_ID, "grant_guide")
				}, new ResourceLocation[0], FunctionObject.CacheableFunction.NONE))
				.save(consumer, "root");
	}

	private static Path createPath(Path outputFolder, Advancement advancement) {
		return outputFolder.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
	}

	@Override
	public String getName() {
		return "Advancements";
	}
}
