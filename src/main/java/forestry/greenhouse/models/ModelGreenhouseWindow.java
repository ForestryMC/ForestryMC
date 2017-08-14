package forestry.greenhouse.models;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.greenhouse.api.greenhouse.GreenhouseManager;
import forestry.core.blocks.properties.UnlistedBlockAccess;
import forestry.core.blocks.properties.UnlistedBlockPos;
import forestry.core.models.DefaultTextureGetter;
import forestry.core.models.ModelBlockCustomCached;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ModelUtil;
import forestry.greenhouse.blocks.BlockGreenhouseWindow;
import forestry.greenhouse.blocks.State;
import forestry.greenhouse.tiles.TileGreenhouseWindow;

@SideOnly(Side.CLIENT)
public class ModelGreenhouseWindow extends ModelBlockCustomCached<BlockGreenhouseWindow, ModelGreenhouseWindow.Key> {
	public ModelGreenhouseWindow() {
		super(BlockGreenhouseWindow.class);
	}

	@Override
	protected Key getInventoryKey(ItemStack stack) {
		NBTTagCompound tagCompound = stack.getTagCompound();
		String glassName = "glass";
		if (tagCompound != null) {
			glassName = tagCompound.getString("Glass");
		}
		return new Key(glassName, true, true, null);
	}

	@Override
	protected Key getWorldKey(IBlockState state) {
		IExtendedBlockState stateExtended = (IExtendedBlockState) state;
		IBlockAccess world = stateExtended.getValue(UnlistedBlockAccess.BLOCKACCESS);
		BlockPos pos = stateExtended.getValue(UnlistedBlockPos.POS);
		TileGreenhouseWindow window = TileUtil.getTile(world, pos, TileGreenhouseWindow.class);
		String glassName = "glass";
		if (window != null) {
			glassName = window.getGlass();
		}
		return new Key(glassName, state.getValue(State.PROPERTY) == State.ON, false, state.getValue(BlockGreenhouseWindow.FACING));
	}

	@Override
	protected IBakedModel bakeBlock(BlockGreenhouseWindow block, Key key, boolean inventory) {
		ImmutableMap.Builder<String, String> textures = ImmutableMap.builder();
		String texture = GreenhouseManager.helper.getGlassTexture(key.glassName);
		textures.put("texture", texture);
		textures.put("particle", texture);

		IModel model = null;
		IModelState state = null;
		String stateName = "forestry:models/item/greenhouse_window";
		String modelName = "forestry:block/greenhouse_window_";
		if (block.isRoofWindow()) {
			stateName += "_up";
			modelName += "up_";
		}
		modelName += key.isOpen ? "open" : "closed";
		try {
			model = ModelLoaderRegistry.getModel(new ResourceLocation(modelName));
			state = ModelUtil.loadModelState(new ResourceLocation(stateName));
		} catch (Exception e) {
			Throwables.propagate(e);
		}
		if (model == null || state == null) {
			throw new IllegalArgumentException("Could not bake greenhouse window model");
		}
		EnumFacing facing = key.facing;
		if (facing == EnumFacing.EAST) {
			state = new ModelStateComposition(state, ModelRotation.X0_Y270);
		} else if (facing == EnumFacing.WEST) {
			state = new ModelStateComposition(state, ModelRotation.X0_Y90);
		} else if (facing == EnumFacing.NORTH) {
			state = new ModelStateComposition(state, ModelRotation.X0_Y180);
		}
		IModel retexturedModel = model.retexture(textures.build());
		return retexturedModel.bake(state, key.inventory ? DefaultVertexFormats.ITEM : DefaultVertexFormats.BLOCK, DefaultTextureGetter.INSTANCE);
	}

	public static class Key {
		public final boolean inventory;
		public final String glassName;
		public final boolean isOpen;
		@Nullable
		public final EnumFacing facing;

		public Key(String glassName, boolean isOpen, boolean inventory, EnumFacing facing) {
			this.glassName = glassName;
			this.isOpen = isOpen;
			this.inventory = inventory;
			this.facing = facing;
		}

		@Override
		public int hashCode() {
			return Boolean.hashCode(inventory) + Boolean.hashCode(isOpen) + glassName.hashCode() + (facing != null ? facing.hashCode() : 0);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Key)) {
				return false;
			}
			Key key = (Key) obj;
			return key.inventory == inventory && key.isOpen == isOpen && facing == key.facing && key.glassName.equals(glassName);
		}
	}
}
