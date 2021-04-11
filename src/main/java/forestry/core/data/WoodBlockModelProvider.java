package forestry.core.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.IWoodType;
import forestry.core.config.Constants;

public class WoodBlockModelProvider extends ModelProvider {

	public WoodBlockModelProvider(DataGenerator generator) {
		super(generator, "block");
	}

	@Override
	protected void registerModels() {
		for (EnumForestryWoodType type : EnumForestryWoodType.VALUES) {
			addPlank(type);
			addLog(type);
			addStair(type);
			addSlab(type);
			addFence(type);
			addFenceGate(type);
			addDoor(type);
		}
		registerModel("leaves", new ModelBuilder()
			.parent("block/cube_all")
			.texture("all", new ResourceLocation(Constants.MOD_ID, "block/leaves/deciduous.plain")));
	}

	private void addPlank(IWoodType type) {
		registerModel("arboriculture/planks/" + type.getSerializedName(), new ModelBuilder()
				.parent("block/cube_all")
				.texture("all", new ResourceLocation(Constants.MOD_ID, "block/wood/planks." + type.getSerializedName())));
	}

	private void addLog(IWoodType type) {
		registerModel("arboriculture/logs/" + type.getSerializedName(), new ModelBuilder()
				.parent("block/cube_column")
				.texture("side", new ResourceLocation(Constants.MOD_ID, "block/wood/bark." + type.getSerializedName()))
				.texture("end", new ResourceLocation(Constants.MOD_ID, "block/wood/heart." + type.getSerializedName())));
	}

	private void addStair(IWoodType type) {
		ResourceLocation texture = new ResourceLocation(Constants.MOD_ID, "block/wood/planks." + type.getSerializedName());
		registerModel("arboriculture/stairs/" + type.getSerializedName(), new ModelBuilder()
				.parent("block/stairs")
				.texture("side", texture)
				.texture("top", texture)
				.texture("bottom", texture)
				.particle(texture));
		registerModel("arboriculture/stairs/" + type.getSerializedName() + "_inner", new ModelBuilder()
				.parent("block/inner_stairs")
				.texture("side", texture)
				.texture("top", texture)
				.texture("bottom", texture)
				.particle(texture));
		registerModel("arboriculture/stairs/" + type.getSerializedName() + "_outer", new ModelBuilder()
				.parent("block/outer_stairs")
				.texture("side", texture)
				.texture("top", texture)
				.texture("bottom", texture)
				.particle(texture));
	}

	private void addSlab(IWoodType type) {
		ResourceLocation texture = new ResourceLocation(Constants.MOD_ID, "block/wood/planks." + type.getSerializedName());
		registerModel("arboriculture/slabs/" + type.getSerializedName() + "_top", new ModelBuilder()
				.parent("block/slab_top")
				.texture("side", texture)
				.texture("top", texture)
				.texture("bottom", texture)
				.particle(texture));
		registerModel("arboriculture/slabs/" + type.getSerializedName(), new ModelBuilder()
				.parent("block/slab")
				.texture("side", texture)
				.texture("top", texture)
				.texture("bottom", texture)
				.particle(texture));
	}

	private void addFence(IWoodType type) {
		ResourceLocation texture = new ResourceLocation(Constants.MOD_ID, "block/wood/planks." + type.getSerializedName());
		registerModel("arboriculture/fences/" + type.getSerializedName() + "_side", new ModelBuilder()
				.parent("block/fence_side")
				.texture("texture", texture));
		registerModel("arboriculture/fences/" + type.getSerializedName(), new ModelBuilder()
				.parent("block/fence_post")
				.texture("texture", texture));
		registerModel("arboriculture/fences/" + type.getSerializedName() + "_inventory", new ModelBuilder()
				.parent("block/fence_inventory")
				.texture("texture", texture));
	}

	private void addFenceGate(IWoodType type) {
		ResourceLocation texture = new ResourceLocation(Constants.MOD_ID, "block/wood/planks." + type.getSerializedName());
		registerModel("arboriculture/fence_gates/" + type.getSerializedName() + "_wall_open", new ModelBuilder()
				.parent("block/template_fence_gate_wall_open")
				.texture("texture", texture));
		registerModel("arboriculture/fence_gates/" + type.getSerializedName() + "_wall", new ModelBuilder()
				.parent("block/template_fence_gate_wall")
				.texture("texture", texture));
		registerModel("arboriculture/fence_gates/" + type.getSerializedName() + "_open", new ModelBuilder()
				.parent("block/template_fence_gate_open")
				.texture("texture", texture));
		registerModel("arboriculture/fence_gates/" + type.getSerializedName(), new ModelBuilder()
				.parent("block/template_fence_gate")
				.texture("texture", texture));
	}

	private void addDoor(IWoodType type) {
		ResourceLocation top = new ResourceLocation(Constants.MOD_ID, "block/doors/" + type.getSerializedName() + "_upper");
		ResourceLocation bottom = new ResourceLocation(Constants.MOD_ID, "block/doors/" + type.getSerializedName() + "_lower");
		registerModel("arboriculture/doors/" + type.getSerializedName() + "_bottom", new ModelBuilder()
				.parent("block/door_bottom")
				.texture("top", top)
				.texture("bottom", bottom));
		registerModel("arboriculture/doors/" + type.getSerializedName() + "_bottom_hinge", new ModelBuilder()
				.parent("block/door_bottom_rh")
				.texture("top", top)
				.texture("bottom", bottom));
		registerModel("arboriculture/doors/" + type.getSerializedName() + "_top", new ModelBuilder()
				.parent("block/door_top")
				.texture("top", top)
				.texture("bottom", bottom));
		registerModel("arboriculture/doors/" + type.getSerializedName() + "_top_hinge", new ModelBuilder()
				.parent("block/door_top_rh")
				.texture("top", top)
				.texture("bottom", bottom));
	}
}
