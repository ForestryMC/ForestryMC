package forestry.core.render;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class RenderHelper {
	public static final Vector3f ORIGIN = new Vector3f(0.0F, 0.0F, 0.0F);

	// The current partial ticks
	public float partialTicks;
	// The current transformation
	public PoseStack transformation;
	public MultiBufferSource buffer;
	public int combinedLight;
	public int packetLight;
	public float rColor = 1.0f;
	public float bColor = 1.0f;
	public float gColor = 1.0f;
	public float alpha = 1.0f;

	@Nullable
	private ItemEntity dummyEntityItem;
	private long lastTick;

	private Vector3f baseRotation = ORIGIN;

	public void update(float partialTicks, PoseStack transformation, MultiBufferSource buffer, int combinedLight, int packetLight) {
		this.packetLight = packetLight;
		this.combinedLight = combinedLight;
		this.partialTicks = partialTicks;
		this.transformation = transformation;
		this.buffer = buffer;
	}

	private ItemEntity dummyItem(Level world) {
		if (dummyEntityItem == null) {
			dummyEntityItem = new ItemEntity(world, 0, 0, 0);
		} else {
			dummyEntityItem.level = world;
		}
		return dummyEntityItem;
	}

	public void renderItem(ItemStack stack, Level world) {
		ItemEntity dummyItem = dummyItem(world);
		dummyItem.setItem(stack);

		if (world.getGameTime() != lastTick) {
			lastTick = world.getGameTime();
			dummyItem.tick();
		}

		EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
		renderManager.render(dummyItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, transformation, buffer, combinedLight);

		dummyItem.level = null;
	}

	public void setRotation(Vector3f baseRotation) {
		this.baseRotation = baseRotation;
	}

	public void rotate(Quaternion rotation) {
		transformation.mulPose(rotation);
	}

	public void translate(double x, double y, double z) {
		transformation.translate(x, y, z);
	}

	public void scale(float x, float y, float z) {
		transformation.scale(x, y, z);
	}

	public void color(float rColor, float gColor, float bColor) {
		color(rColor, gColor, bColor, 1.0f);
	}

	public void color(float rColor, float gColor, float bColor, float alpha) {
		this.rColor = rColor;
		this.gColor = gColor;
		this.bColor = bColor;
		this.alpha = alpha;
	}

	public void pop() {
		transformation.popPose();
	}

	public void push() {
		transformation.pushPose();
	}

	public void renderModel(VertexConsumer builder, ModelPart... renderers) {
		renderModel(builder, ORIGIN, renderers);
	}

	public void renderModel(ResourceLocation location, ModelPart... renderers) {
		renderModel(location, ORIGIN, renderers);
	}

	public void renderModel(VertexConsumer builder, Vector3f rotation, ModelPart... renderers) {
		for (ModelPart renderer : renderers) {
			renderer.xRot = baseRotation.x() + rotation.x();
			renderer.yRot = baseRotation.y() + rotation.y();
			renderer.zRot = baseRotation.z() + rotation.z();
			renderer.render(transformation, builder, combinedLight, packetLight, rColor, gColor, bColor, alpha);
		}
	}

	public void renderModel(ResourceLocation location, Vector3f rotation, ModelPart... renderers) {
		renderModel(buffer.getBuffer(RenderType.entityCutout(location)), rotation, renderers);
	}
}
