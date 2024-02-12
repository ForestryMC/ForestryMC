package forestry.core.data;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.IWoodType;
import forestry.core.config.Constants;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class WoodBlockModelProvider extends BlockModelProvider {

	public WoodBlockModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, Constants.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		for (EnumForestryWoodType type : EnumForestryWoodType.VALUES) {
			add(type);
		}

		cubeAll("leaves", new ResourceLocation(Constants.MOD_ID, "block/leaves/deciduous.plain"));
	}

	private void add(IWoodType type) {
		String name = type.getSerializedName();
		ResourceLocation texture = new ResourceLocation(Constants.MOD_ID, "block/wood/planks." + name);
		ResourceLocation bark = new ResourceLocation(Constants.MOD_ID, "block/wood/bark." + name);
		ResourceLocation heart = new ResourceLocation(Constants.MOD_ID, "block/wood/heart." + name);

		cubeColumn("arboriculture/logs/" + name, bark, heart);
		cubeAll("arboriculture/planks/" + name, texture);
		stairs("arboriculture/stairs/" + name, texture, texture, texture).texture("particle", texture);
		stairsInner("arboriculture/stairs/" + name + "_inner", texture, texture, texture).texture("particle", texture);
		stairsOuter("arboriculture/stairs/" + name + "_outer", texture, texture, texture).texture("particle", texture);
		slab("arboriculture/slabs/" + name, texture, texture, texture).texture("particle", texture);
		slabTop("arboriculture/slabs/" + name + "_top", texture, texture, texture).texture("particle", texture);
		fencePost("arboriculture/fences/" + name, texture);
		fenceInventory("arboriculture/fences/" + name + "_inventory", texture);
		fenceSide("arboriculture/fences/" + name + "_side", texture);
		fenceGate("arboriculture/fence_gates/" + name, texture);
		fenceGateOpen("arboriculture/fence_gates/" + name + "_open", texture);
		fenceGateWall("arboriculture/fence_gates/" + name + "_wall", texture);
		fenceGateWallOpen("arboriculture/fence_gates/" + name + "_wall_open", texture);

		ResourceLocation top = new ResourceLocation(Constants.MOD_ID, "block/doors/" + name + "_upper");
		ResourceLocation bottom = new ResourceLocation(Constants.MOD_ID, "block/doors/" + name + "_lower");

		doorBottomLeft("arboriculture/doors/" + name + "_bottom_left", bottom, top);
		doorBottomRight("arboriculture/doors/" + name + "_bottom_right", bottom, top);
		doorBottomLeftOpen("arboriculture/doors/" + name + "_bottom_left_open", bottom, top);
		doorBottomRightOpen("arboriculture/doors/" + name + "_bottom_right_open", bottom, top);

		doorTopLeft("arboriculture/doors/" + name + "_top_left", bottom, top);
		doorTopRight("arboriculture/doors/" + name + "_top_right", bottom, top);
		doorTopLeftOpen("arboriculture/doors/" + name + "_top_left_open", bottom, top);
		doorTopRightOpen("arboriculture/doors/" + name + "_top_right_open", bottom, top);
	}
}
