package forestry.core.blocks;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.function.Supplier;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;

import forestry.core.config.Constants;
import forestry.core.render.IForestryRendererProvider;
import forestry.core.render.RenderForestryTile;
import forestry.core.tiles.TileForestry;
import forestry.modules.features.FeatureTileType;

public class MachinePropertiesTesr<T extends TileForestry> extends MachineProperties<T> implements IMachinePropertiesTesr<T> {

	private final ResourceLocation particleTexture;
	private final boolean isFullCube;

	@Nullable
	@OnlyIn(Dist.CLIENT)
	private ModelLayerLocation modelLayer;
	
	@Nullable
	@OnlyIn(Dist.CLIENT)
	private IForestryRendererProvider<? super T> renderer;

	public MachinePropertiesTesr(Supplier<FeatureTileType<? extends T>> teType, String name, IShapeProvider shape, ResourceLocation particleTexture, boolean isFullCube) {
		super(teType, name, shape);
		this.particleTexture = particleTexture;
		this.isFullCube = isFullCube;
	}

	@OnlyIn(Dist.CLIENT)
	public void setRenderer(ModelLayerLocation modelLayer, IForestryRendererProvider<? super T> renderer) {
		this.modelLayer = modelLayer;
		this.renderer = renderer;
	}

	@Override
	@Nullable
	public IForestryRendererProvider<? super T> getRenderer() {
		return renderer;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetupRenderers(EntityRenderersEvent.RegisterRenderers event) {
		if (renderer != null) {
			event.registerBlockEntityRenderer(getTeType(), (ctx) -> new RenderForestryTile<>(renderer.create(ctx.bakeLayer(modelLayer))));
		}
	}

	@Override
	public ResourceLocation getParticleTexture() {
		return particleTexture;
	}

	public boolean isFullCube(BlockState state) {
		return isFullCube;
	}
	
	@Override
	public ModelLayerLocation getModelLayer() {
		return modelLayer;
	}

	public static class Builder<T extends TileForestry> extends MachineProperties.Builder<T, Builder<T>> {
		@Nullable
		private ResourceLocation particleTexture;
		private boolean isFullCube = true;

		public Builder(Supplier<FeatureTileType<? extends T>> type, String name) {
			super(type, name);
		}

		public Builder() {
		}

		public Builder<T> setParticleTexture(String particleTexture) {
			return setParticleTexture(new ResourceLocation(Constants.MOD_ID, "block/" + particleTexture));
		}

		public Builder<T> setParticleTexture(ResourceLocation particleTexture) {
			this.particleTexture = particleTexture;
			return this;
		}

		public Builder<T> setNotFullCube() {
			isFullCube = false;
			return this;
		}

		@Override
		public MachinePropertiesTesr<T> create() {
			Preconditions.checkNotNull(type);
			Preconditions.checkNotNull(name);
			Preconditions.checkNotNull(shape);
			Preconditions.checkNotNull(particleTexture);
			return new MachinePropertiesTesr<>(type, name, shape, particleTexture, isFullCube);
		}
	}
}
