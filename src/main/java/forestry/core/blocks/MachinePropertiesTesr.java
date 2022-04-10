package forestry.core.blocks;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.render.IForestryRenderer;
import forestry.core.render.RenderForestryTile;
import forestry.core.tiles.TileForestry;
import forestry.modules.features.FeatureTileType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class MachinePropertiesTesr<T extends TileForestry> extends MachineProperties<T> implements IMachinePropertiesTesr<T> {

	private final ResourceLocation particleTexture;
	private final boolean isFullCube;

	@Nullable
	@OnlyIn(Dist.CLIENT)
	private IForestryRenderer<? super T> renderer;

	public MachinePropertiesTesr(Supplier<FeatureTileType<? extends T>> teType, String name, IShapeProvider shape, ResourceLocation particleTexture, boolean isFullCube) {
		super(teType, name, shape);
		this.particleTexture = particleTexture;
		this.isFullCube = isFullCube;
	}

	@OnlyIn(Dist.CLIENT)
	public void setRenderer(IForestryRenderer<? super T> renderer) {
		this.renderer = renderer;
	}

	@Override
	@Nullable
	public IForestryRenderer<? super T> getRenderer() {
		return renderer;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		if (renderer != null) {
			ClientRegistry.bindTileEntityRenderer(getTeType(), (dispatcher) -> new RenderForestryTile<>(dispatcher, renderer));
		}
	}

	@Override
	public ResourceLocation getParticleTexture() {
		return particleTexture;
	}

	public boolean isFullCube(BlockState state) {
		return isFullCube;
	}

	public static Item.Properties setRenderer(Item.Properties properties, IBlockType type) {
		Proxies.render.setRenderer(properties, type);
		return properties;
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
