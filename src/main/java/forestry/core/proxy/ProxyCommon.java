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
import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.API;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;

import forestry.Forestry;
import forestry.core.TickHandlerCoreServer;
import forestry.core.WorldGenerator;
import forestry.core.config.Defaults;
import forestry.core.config.ForestryBlock;
import forestry.core.config.ForestryItem;
import forestry.core.multiblock.MultiblockServerTickHandler;
import forestry.core.network.PacketCoordinates;
import forestry.core.network.PacketFXSignal;
import forestry.core.network.PacketId;
import forestry.core.render.SpriteSheet;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginManager;

public class ProxyCommon {

	public String getMinecraftVersion() {
		return Loader.instance().getMinecraftModContainer().getVersion();
	}

	public void registerTickHandlers(WorldGenerator worldGenerator) {
		TickHandlerCoreServer tickHandlerCoreServer = new TickHandlerCoreServer(worldGenerator);
		FMLCommonHandler.instance().bus().register(tickHandlerCoreServer);
		MinecraftForge.EVENT_BUS.register(tickHandlerCoreServer);

		FMLCommonHandler.instance().bus().register(new MultiblockServerTickHandler());
	}

	public void registerBlock(Block block, Class<? extends ItemBlock> itemClass) {
		if (!EnumSet.of(PluginManager.Stage.PRE_INIT).contains(PluginManager.getStage())) {
			throw new RuntimeException("Tried to register Block outside of Pre-Init");
		}
		GameRegistry.registerBlock(block, itemClass, StringUtil.cleanBlockName(block));
	}

	public void registerItem(Item item) {
		GameRegistry.registerItem(item, StringUtil.cleanItemName(item));
	}

	/**
	 * As addRecipe, except that the recipe is injected into the front of the
	 * crafting manager to avoid certain generic collisions. Notably, all
	 * Forestry wood crafting into oak stairs & slabs.
	 */
	@SuppressWarnings("unchecked")
	public void addPriorityRecipe(ItemStack itemstack, Object... obj) {
		cleanRecipe(obj);
		CraftingManager.getInstance().getRecipeList().add(0, new ShapedOreRecipe(itemstack, obj));
	}

	/**
	 * As addShapelessRecipe, except that the recipe is injected into the front
	 * of the crafting manager to avoid certain generic collisions. Notably, all
	 * Forestry wood crafting into oak stairs & slabs.
	 */
	@SuppressWarnings("unchecked")
	public void addPriorityShapelessRecipe(ItemStack itemstack, Object... obj) {
		cleanRecipe(obj);
		CraftingManager.getInstance().getRecipeList().add(0, new ShapelessOreRecipe(itemstack, obj));
	}

	@SuppressWarnings("unchecked")
	public void addRecipe(ItemStack itemstack, Object... obj) {
		cleanRecipe(obj);
		CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(itemstack, obj));
	}

	@SuppressWarnings("unchecked")
	public void addShapelessRecipe(ItemStack itemstack, Object... obj) {
		cleanRecipe(obj);
		CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(itemstack, obj));
	}

	private void cleanRecipe(Object... obj) {
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] instanceof ForestryItem) {
				obj[i] = ((ForestryItem) obj[i]).item();
			} else if (obj[i] instanceof ForestryBlock) {
				obj[i] = ((ForestryBlock) obj[i]).block();
			}
		}
	}

	public void addSmelting(ItemStack res, ItemStack prod) {
		addSmelting(res, prod, 0.0f);
	}

	public void addSmelting(ItemStack res, ItemStack prod, float xp) {
		if (res == null || res.getItem() == null) {
			throw new IllegalArgumentException("Tried to register smelting recipe with null input");
		}
		if (prod == null || prod.getItem() == null) {
			throw new IllegalArgumentException("Tried to register smelting recipe with null output");
		}
		GameRegistry.addSmelting(res, prod, xp);
	}

	public void dropItemPlayer(EntityPlayer player, ItemStack stack) {
		player.dropPlayerItemWithRandomChoice(stack, true);
	}

	public void setHabitatLocatorCoordinates(Entity player, ChunkCoordinates coordinates) {
		if (coordinates != null) {
			Forestry.packetHandler.sendPacket(new PacketCoordinates(PacketId.HABITAT_BIOME_POINTER, coordinates).getPacket(), (EntityPlayerMP) player);
		}
	}

	public void removePotionEffect(EntityPlayer player, Potion effect) {
		player.clearActivePotions();
	}

	public String getCurrentLanguage() {
		return null;
	}

	public String getItemStackDisplayName(Item item) {
		return null;
	}

	public String getDisplayName(ItemStack itemstack) {
		return null;
	}

	public File getForestryRoot() {
		return new File(".");
	}

	public int getByBlockModelId() {
		return 0;
	}

	public boolean isOp(EntityPlayer player) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return server.getConfigurationManager().func_152596_g(player.getGameProfile());
	}

	public double getBlockReachDistance(EntityPlayer entityplayer) {
		return 4f;
	}

	public boolean isSimulating(World world) {
		return true;
	}

	public boolean isShiftDown() {
		return false;
	}

	public boolean isItemStackTagEqual(ItemStack stack1, ItemStack stack2) {
		return ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	public String getItemStackDisplayName(ItemStack stack) {
		return null;
	}

	public boolean setBlockWithNotify(World world, int x, int y, int z, Block block) {
		return world.setBlock(x, y, z, block, 0, Defaults.FLAG_BLOCK_SYNCH);
	}

	public void playSoundFX(World world, int x, int y, int z, Block block) {
		Proxies.net.sendNetworkPacket(new PacketFXSignal(PacketFXSignal.SoundFXType.LEAF, x, y, z, block, 0));
	}

	public void playSoundFX(World world, int x, int y, int z, String sound, float volume, float pitch) {
	}

	public void addEntityBiodustFX(World world, double d1, double d2, double d3, float f1, float f2, float f3) {
	}

	public void addEntitySwarmFX(World world, double d1, double d2, double d3, float f1, float f2, float f3) {
	}

	public void addEntityExplodeFX(World world, double d1, double d2, double d3, float f1, float f2, float f3) {
	}

	public void addEntitySnowFX(World world, double d1, double d2, double d3, float f1, float f2, float f3) {
	}

	public void addBlockDestroyEffects(World world, int xCoord, int yCoord, int zCoord, Block block, int i) {
		sendFXSignal(PacketFXSignal.VisualFXType.BLOCK_DESTROY, PacketFXSignal.SoundFXType.BLOCK_DESTROY, world, xCoord, yCoord, zCoord, block, i);
	}

	public void addBlockPlaceEffects(World world, int xCoord, int yCoord, int zCoord, Block block, int i) {
		sendFXSignal(PacketFXSignal.VisualFXType.NONE, PacketFXSignal.SoundFXType.BLOCK_PLACE, world, xCoord, yCoord, zCoord, block, i);
	}

	public void playBlockBreakSoundFX(World world, int x, int y, int z, Block block) {
	}

	public void playBlockPlaceSoundFX(World world, int x, int y, int z, Block block) {
	}

	public void sendFXSignal(PacketFXSignal.VisualFXType visualFX, PacketFXSignal.SoundFXType soundFX, World world, int xCoord, int yCoord, int zCoord,
			Block block, int i) {
		if (Proxies.common.isSimulating(world)) {
			Proxies.net.sendNetworkPacket(new PacketFXSignal(visualFX, soundFX, xCoord, yCoord, zCoord, block, i));
		}
	}

	public IResourceManager getSelectedTexturePack(Minecraft minecraft) {
		return null;
	}

	public World getRenderWorld() {
		return null;
	}

	public Minecraft getClientInstance() {
		return FMLClientHandler.instance().getClient();
	}

	/* DEPENDENCY HANDLING */
	public boolean isModLoaded(String modname) {
		return Loader.isModLoaded(modname);
	}

	public boolean isModLoaded(String modname, String versionRangeString) {
		if (!isModLoaded(modname)) {
			return false;
		}

		if (versionRangeString != null) {
			ModContainer mod = Loader.instance().getIndexedModList().get(modname);
			ArtifactVersion modVersion = mod.getProcessedVersion();

			VersionRange versionRange = VersionParser.parseRange(versionRangeString);
			DefaultArtifactVersion requiredVersion = new DefaultArtifactVersion(modname, versionRange);

			if (!requiredVersion.containsVersion(modVersion)) {
				return false;
			}
		}

		return true;
	}

	public boolean isAPILoaded(String apiName) {
		return isAPILoaded(apiName, null);
	}

	public boolean isAPILoaded(String apiName, String versionRangeString) {
		Package apiPackage = Package.getPackage(apiName);
		if (apiPackage == null) {
			return false;
		}

		API apiAnnotation = apiPackage.getAnnotation(API.class);
		if (apiAnnotation == null) {
			return false;
		}

		if (versionRangeString != null) {
			String apiVersionString = apiAnnotation.apiVersion();
			if (apiVersionString == null) {
				return false;
			}

			VersionRange versionRange = VersionParser.parseRange(versionRangeString);

			DefaultArtifactVersion givenVersion = new DefaultArtifactVersion(apiName, apiVersionString);
			DefaultArtifactVersion requiredVersion = new DefaultArtifactVersion(apiName, versionRange);

			if (!requiredVersion.containsVersion(givenVersion)) {
				return false;
			}
		}

		return true;
	}

	public Object instantiateIfModLoaded(String modname, String className) {
		return instantiateIfModLoaded(modname, null, className);
	}

	public Object instantiateIfModLoaded(String modname, String versionRangeString, String className) {

		if (isModLoaded(modname, versionRangeString)) {
			try {
				Class<?> clas = Class.forName(className, true, Loader.instance().getModClassLoader());
				return clas.newInstance();
			} catch (Exception ex) {
				Proxies.log.severe("Failed to load " + className + " even though mod " + modname + " was available.");
				return null;
			}
		} else {
			return null;
		}

	}

	public void bindTexture(ResourceLocation location) {
	}

	public void bindTexture(SpriteSheet spriteSheet) {
	}

	public EntityPlayer getPlayer() {
		return null;
	}

	/**
	 * Get a player for a given World and GameProfile.
	 * If they are not in the World, returns a FakePlayer.
	 * Do not store references to the return value, to prevent worlds staying in memory.
	 */
	public EntityPlayer getPlayer(World world, GameProfile profile) {
		if (world == null) {
			throw new IllegalArgumentException("World cannot be null");
		}

		if (profile == null || profile.getName() == null) {
			return FakePlayerFactory.getMinecraft((WorldServer) world);
		}

		EntityPlayer player = world.getPlayerEntityByName(profile.getName());
		if (player != null) {
			return player;
		} else {
			return FakePlayerFactory.get((WorldServer) world, profile);
		}
	}
}
