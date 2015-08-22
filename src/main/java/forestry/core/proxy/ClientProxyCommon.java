/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.proxy;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.particle.EntityExplodeFX.Factory;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.mojang.authlib.GameProfile;

import org.lwjgl.input.Keyboard;

import forestry.api.core.IMeshDefinitionObject;
import forestry.api.core.IModelObject;
import forestry.api.core.IVariantObject;
import forestry.api.core.IModelObject.ModelType;
import forestry.apiculture.render.TextureHabitatLocator;
import forestry.core.ForestryClient;
import forestry.core.TickHandlerCoreClient;
import forestry.core.WorldGenerator;
import forestry.core.config.Config;
import forestry.core.render.ModelManager;

public class ClientProxyCommon extends ProxyCommon {

	@Override
	public void bindTexture(ResourceLocation location) {
		getClientInstance().getTextureManager().bindTexture(location);
	}
	
	@Override
	public void registerItem(Item item) {
		super.registerItem(item);
		ModelManager.getInstance().registerItemModel(item);
	}
	
	@Override
	public void registerBlock(Block block, Class<? extends ItemBlock> itemClass) {
		super.registerBlock(block, itemClass);
		ModelManager.getInstance().registerItemBlockModel(block);
	}

	@Override
	public void bindTexture() {
		bindTexture(TextureMap.locationBlocksTexture);
	}

	@Override
	public void registerTickHandlers(WorldGenerator worldGenerator) {
		super.registerTickHandlers(worldGenerator);

		new TickHandlerCoreClient();
	}

	@Override
	public IResourceManager getSelectedTexturePack(Minecraft minecraft) {
		return minecraft.getResourceManager();
	}

	@Override
	public void setHabitatLocatorCoordinates(Entity player, BlockPos pos) {
		TextureHabitatLocator.getInstance().setTargetCoordinates(pos);
	}

	@Override
	public File getForestryRoot() {
		return Minecraft.getMinecraft().mcDataDir;
	}

	@Override
	public World getRenderWorld() {
		return getClientInstance().theWorld;
	}

	@Override
	public int getBlockModelIdEngine() {
		return ForestryClient.blockModelIdEngine;
	}

	@Override
	public int getByBlockModelId() {
		return ForestryClient.byBlockModelId;
	}

	@Override
	public boolean isOp(EntityPlayer player) {
		return true;
	}

	@Override
	public double getBlockReachDistance(EntityPlayer entityplayer) {
		if (entityplayer instanceof EntityPlayerSP) {
			return getClientInstance().playerController.getBlockReachDistance();
		} else {
			return 4f;
		}
	}

	@Override
	public boolean isSimulating(World world) {
		return !world.isRemote;
	}

	@Override
	public boolean isShiftDown() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}

	@Override
	public String getItemStackDisplayName(Item item) {
		return item.getItemStackDisplayName(null);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return stack.getItem().getItemStackDisplayName(stack);
	}

	@Override
	public String getCurrentLanguage() {
		return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
	}

	@Override
	public String getDisplayName(ItemStack itemstack) {
		return itemstack.getItem().getItemStackDisplayName(itemstack);
	}

	@Override
	public void playSoundFX(World world, BlockPos pos, Block block) {
		if (Proxies.common.isSimulating(world)) {
			super.playSoundFX(world, pos, block);
		} else {
			playSoundFX(world, pos, block.stepSound.getStepSound(), block.stepSound.getVolume(), block.stepSound.getFrequency());
		}
	}

	@Override
	public void playBlockBreakSoundFX(World world, BlockPos pos, Block block) {
		if (Proxies.common.isSimulating(world)) {
			super.playSoundFX(world, pos, block);
		} else {
			playSoundFX(world, pos, block.stepSound.getBreakSound(), block.stepSound.getVolume() / 4, block.stepSound.getFrequency());
		}
	}

	@Override
	public void playBlockPlaceSoundFX(World world, BlockPos pos, Block block) {
		if (Proxies.common.isSimulating(world)) {
			super.playSoundFX(world, pos, block);
		} else {
			playSoundFX(world, pos, block.stepSound.getStepSound(), block.stepSound.getVolume() / 4, block.stepSound.getFrequency());
		}
	}

	@Override
	public void playSoundFX(World world, BlockPos pos, String sound, float volume, float pitch) {
		world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getX() + 0.5, sound, volume, (1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f) * 0.7f, false);
	}

	/**
	 * Renders a EntityBiodustFX on client.
	 */
	// FIXME: This is causing crashes.
	@Override
	public void addEntityBiodustFX(World world, double d1, double d2, double d3, float f1, float f2, float f3) {
		if (!Config.enableParticleFX) {
			return;
		}

		// ModLoader.getMinecraftInstance().effectRenderer.addEffect(new EntityBiodustFX(world, d1, d2, d3, f1, f2, f3));
	}

	// FIXME: This is causing crashes.
	@Override
	public void addEntitySwarmFX(World world, double d1, double d2, double d3, float f1, float f2, float f3) {
		if (!Config.enableParticleFX) {
			return;
		}

		// ModLoader.getMinecraftInstance().effectRenderer.addEffect(new EntityHoneydustFX(world, d1, d2, d3, f1, f2, f3));
	}

	@Override
	public void addEntityExplodeFX(World world, double d1, double d2, double d3, double f1, double f2, double f3) {
		if (!Config.enableParticleFX) {
			return;
		}
		getClientInstance().effectRenderer.spawnEffectParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), d1, d2, d3, f1, f2, f3);
	}

	@Override
	public void addBlockDestroyEffects(World world, BlockPos pos, Block block, int i) {
		if (!isSimulating(world)) {
			getClientInstance().effectRenderer.addBlockDestroyEffects(pos, block.getStateFromMeta(i));
		} else {
			super.addBlockDestroyEffects(world, pos, block, i);
		}
	}

	@Override
	public void addBlockPlaceEffects(World world, BlockPos pos, Block block, int i) {
		if (!isSimulating(world)) {
			playBlockPlaceSoundFX(world, pos, block);
		} else {
			super.addBlockPlaceEffects(world, pos, block, i);
		}
	}

	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}

	@Override
	public EntityPlayer getPlayer(World world, GameProfile profile) {
		return super.getPlayer(world, profile);
	}
}
