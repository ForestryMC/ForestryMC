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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.mojang.authlib.GameProfile;

import org.lwjgl.input.Keyboard;

import forestry.apiculture.render.TextureHabitatLocator;
import forestry.core.ForestryClient;
import forestry.core.TickHandlerCoreClient;
import forestry.core.WorldGenerator;
import forestry.core.multiblock.MultiblockClientTickHandler;
import forestry.core.render.EntityHoneydustFX;
import forestry.core.render.EntityIgnitionFX;
import forestry.core.render.EntitySnowFX;

public class ClientProxyCommon extends ProxyCommon {

	@Override
	public void bindTexture(ResourceLocation location) {
		getClientInstance().getTextureManager().bindTexture(location);
	}

	@Override
	public void bindTexture() {
		bindTexture(TextureMap.LOCATION_MISSING_TEXTURE);
	}

	@Override
	public void registerTickHandlers(WorldGenerator worldGenerator) {
		super.registerTickHandlers(worldGenerator);

		FMLCommonHandler.instance().bus().register(new TickHandlerCoreClient());
		FMLCommonHandler.instance().bus().register(new MultiblockClientTickHandler());
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
	public int getByBlockModelId() {
		return ForestryClient.byBlockModelId;
	}

	@Override
	public boolean isOp(EntityPlayer player) {
		return false;
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
	public void playSoundFX(World world, BlockPos pos, IBlockState state) {
		if (Proxies.common.isSimulating(world)) {
			super.playSoundFX(world, pos, state);
		} else {
			playSoundFX(world, pos.getX(), pos.getY(), pos.getZ(), state.getBlock().stepSound.getStepSound(),state.getBlock().stepSound.getVolume(), state.getBlock().stepSound.getFrequency());
		}
	}

	@Override
	public void playBlockBreakSoundFX(World world, BlockPos pos, IBlockState state) {
		if (Proxies.common.isSimulating(world)) {
			super.playSoundFX(world, pos, state);
		} else {
			playSoundFX(world, pos.getX(), pos.getY(), pos.getZ(), state.getBlock().stepSound.getBreakSound(), state.getBlock().stepSound.getVolume() / 4,state.getBlock().stepSound.getFrequency());
		}
	}

	@Override
	public void playBlockPlaceSoundFX(World world, BlockPos pos, IBlockState state) {
		if (Proxies.common.isSimulating(world)) {
			super.playSoundFX(world, pos, state);
		} else {
			playSoundFX(world, pos.getX(), pos.getY(), pos.getZ(),  state.getBlock().stepSound.getStepSound(),  state.getBlock().stepSound.getVolume() / 4, state.getBlock().stepSound.getFrequency());
		}
	}

	@Override
	public void playSoundFX(World world, int x, int y, int z, String sound, float volume, float pitch) {
		world.playSound(x + 0.5, y + 0.5, z + 0.5, sound, volume, (1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f) * 0.7f, false);
	}

	@Override
	public void addEntitySwarmFX(World world, double d1, double d2, double d3) {
		if (!ClientProxyRender.shouldSpawnParticle(world)) {
			return;
		}

		getClientInstance().effectRenderer.addEffect(new EntityHoneydustFX(world, d1, d2, d3, 0, 0, 0));
	}

	@Override
	public void addEntityExplodeFX(World world, double d1, double d2, double d3) {
		if (!ClientProxyRender.shouldSpawnParticle(world)) {
			return;
		}

		getClientInstance().effectRenderer.addEffect(getClientInstance().effectRenderer.spawnEffectParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), d1, d2, d3, 0, 0, 0));
	}

	@Override
	public void addEntitySnowFX(World world, double d1, double d2, double d3) {
		if (!ClientProxyRender.shouldSpawnParticle(world)) {
			return;
		}

		getClientInstance().effectRenderer.addEffect(new EntitySnowFX(world, d1 + world.rand.nextGaussian(), d2, d3 + world.rand.nextGaussian()));
	}

	@Override
	public void addEntityIgnitionFX(World world, double d1, double d2, double d3) {
		if (!ClientProxyRender.shouldSpawnParticle(world)) {
			return;
		}

		getClientInstance().effectRenderer.addEffect(new EntityIgnitionFX(world, d1, d2, d3));
	}

	@Override
	public void addEntityPotionFX(World world, double d1, double d2, double d3, int color) {
		if (!ClientProxyRender.shouldSpawnParticle(world)) {
			return;
		}

		float red = (color >> 16 & 255) / 255.0F;
		float green = (color >> 8 & 255) / 255.0F;
		float blue = (color & 255) / 255.0F;

		EntityFX entityfx = getClientInstance().effectRenderer.spawnEffectParticle(EnumParticleTypes.SPELL.getParticleID(), d1, d2, d3, 0, 0, 0);
		entityfx.setRBGColorF(red, green, blue);

		getClientInstance().effectRenderer.addEffect(entityfx);
	}

	@Override
	public void addBlockDestroyEffects(World world, BlockPos pos, IBlockState state) {
		if (!isSimulating(world)) {
			getClientInstance().effectRenderer.addBlockDestroyEffects(pos, state);
		} else {
			super.addBlockDestroyEffects(world,  pos, state);
		}
	}

	@Override
	public void addBlockPlaceEffects(World world, BlockPos pos, IBlockState state) {
		if (!isSimulating(world)) {
			playBlockPlaceSoundFX(world, pos, state);
		} else {
			super.addBlockPlaceEffects(world, pos, state);
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
