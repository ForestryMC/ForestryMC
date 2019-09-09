package genetics.parser;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.io.IOUtils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import genetics.api.GeneticsResourceType;
import genetics.api.alleles.AlleleInfo;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleType;

import genetics.utils.NBTUtils;

import io.netty.util.internal.StringUtil;

public class GeneticParser implements ISelectiveResourceReloadListener {
	public final Map<ResourceLocation, IAlleleType> types = new HashMap<>();
	public static final int PATH_PREFIX_LENGTH = "genetics/alleles/".length();
	public static final int PATH_SUFFIX_LENGTH = ".json".length();
	public static final Gson GSON = (new GsonBuilder()).disableHtmlEscaping().create();
	private static final Deque<ResourceLocation> loadingAlleles = Queues.newArrayDeque();

	@Override
	public void onResourceManagerReload(IResourceManager manager, Predicate<IResourceType> resourcePredicate) {
		if (!resourcePredicate.test(GeneticsResourceType.MUTATIONS)) {
			return;
		}

		Multimap<ResourceLocation, CompoundNBT> alleleData = HashMultimap.create();

		for (ResourceLocation location : manager.getAllResourceLocations("genetics/alleles", filename -> filename.endsWith(".json"))) {
			String path = location.getPath();
			ResourceLocation readableLocation = new ResourceLocation(location.getNamespace(), path.substring(PATH_PREFIX_LENGTH, path.length() - PATH_SUFFIX_LENGTH));
			try (IResource resource = manager.getResource(location)) {
				for (ResourceLocation loading : loadingAlleles) {
					if (location.getClass() == loading.getClass() && location.equals(loading)) {
						//LOGGER.error("Circular allele dependencies, stack: [" + Joiner.on(", ").join(loadingAlleles) + "]");
					}
				}
				loadingAlleles.addLast(location);
				JsonObject object = JSONUtils.fromJson(GSON, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
				if (object == null) {
					//LOGGER.error("Couldn't load allele {} as it's null or empty", readableLocation);
				} else {
					alleleData.put(location, JsonToNBT.getTagFromJson(object.toString()));
					//AlleleInfo info = new AlleleInfo();
					//deserialize(location, object, info);
				}
			} catch (CommandSyntaxException exception) {
				//LOGGER.error("Invalid NBT Entry: " + e.toString());
			} catch (IllegalArgumentException | JsonParseException exception) {
				//LOGGER.error("Parsing error loading allele {}", readableLocation, exception);
			} catch (IOException exception) {
				//LOGGER.error("Couldn't read custom allele {} from {}", readableLocation, location, exception);
			} finally {
				ResourceLocation popLoc = loadingAlleles.removeLast();
				if (popLoc != location) {
					//LOGGER.error("Corrupted loading allele stack: " + popLoc + " != " + location);
				}
			}
		}
		for (ResourceLocation location : alleleData.keySet()) {
			CompoundNBT compound = new CompoundNBT();
			List<CompoundNBT> compounds = new LinkedList<>(alleleData.get(location));
			if (compounds.size() > 1) {
				compounds.stream().filter(tag -> tag.getBoolean("replace")).max(Comparator.comparingInt(a -> a.getInt("weight"))).ifPresent(compound::merge);
			}
			if (compounds.size() > 1) {
				compounds.stream().sorted(Comparator.comparingInt(a -> -a.getInt("weight"))).forEach(compound::merge);
			}
			AlleleInfo info = new AlleleInfo(location, compound);
			info.parent = NBTUtils.getString(compound, "parent", StringUtil.EMPTY_STRING);
			info.dominant = NBTUtils.getBoolean(compound, "dominant", false);
			info.weight = NBTUtils.getInt(compound, "weight", 0);
			info.replace = NBTUtils.getBoolean(compound, "replace", false);
			info.replaced = info.replace && compounds.size() > 1;
			info.type = NBTUtils.getString(compound, "type");
			info.name = NBTUtils.getString(compound, "name");
			deserialize(info);
		}
	}

	public IAllele deserialize(AlleleInfo info) {
		ResourceLocation typeName = new ResourceLocation(info.type);
		IAlleleType type = types.get(typeName);
		if (type == null) {
			throw new IllegalStateException("Failed to find the allele type for the id (" + typeName + ").");
		}
		return type.deserialize(info);
	}
}
