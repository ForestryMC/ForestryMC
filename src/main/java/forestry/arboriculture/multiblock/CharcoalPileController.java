package forestry.arboriculture.multiblock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import forestry.api.arboriculture.EnumPileType;
import forestry.api.arboriculture.IWoodProvider;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.multiblock.ICharcoalPileComponent;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.apiculture.network.packets.PacketActiveUpdate;
import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.blocks.BlockPile;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.multiblock.MultiblockRegistry;
import forestry.core.multiblock.MultiblockValidationException;
import forestry.core.multiblock.RectangularMultiblockControllerBase;
import forestry.core.network.DataInputStreamForestry;
import forestry.core.network.DataOutputStreamForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Log;
import forestry.core.utils.Translator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CharcoalPileController extends RectangularMultiblockControllerBase implements ICharcoalPileControllerInternal {

	private int burnTime;
	private int woodBurnTime;
	private boolean active;

	public CharcoalPileController(World world) {
		super(world, CharcoalPileMultiblockSizeLimits.instance);
		active = false;
		burnTime = 0;
	}
	
	@Override
	public void setActive(boolean active) {
		if (this.active == active) {
			return;
		}

		this.active = active;

		if (worldObj != null) {
			if (worldObj.isRemote) {
				worldObj.markBlockRangeForRenderUpdate(getMinimumCoord(), getMaximumCoord());
			} else {
				Proxies.net.sendNetworkPacket(new PacketActiveUpdate(this), worldObj);
			}
		}
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public int getBurnTime() {
		return burnTime;
	}

	@Override
	public void onAttachedPartWithMultiblockData(IMultiblockComponent part, NBTTagCompound data) {
		readFromNBT(data);
	}

	@Override
	protected void onBlockAdded(IMultiblockComponent newComponent) {
	}

	@Override
	protected void onBlockRemoved(IMultiblockComponent oldComponent) {
	}

	@Override
	protected void onMachineRestored() {
	}

	@Override
	protected void onMachinePaused() {
	}

	@Override
	protected void onMachineAssembled() {
	}

	@Override
	protected void onMachineDisassembled() {
		setActive(false);
		burnTime = 0;
		woodBurnTime = 0;
	}

	@Override
	protected void onAssimilate(IMultiblockControllerInternal assimilated) {
		if (!(assimilated instanceof CharcoalPileController)) {
			Log.warning("[%s] Charcoal Kiln @ %s is attempting to assimilate a non-Charcoal Kiln machine! That machine's data will be lost!",
					worldObj.isRemote ? "CLIENT" : "SERVER", getReferenceCoord());
			return;
		}
	}
	
	@Override
	protected boolean updateServer(int tickCount) {
		if (!isActive()) {
			return false;
		}
		if(woodBurnTime == 0){
			int validComps = 0;
			int newBurnTime = 0;
			for (IMultiblockComponent part : connectedParts) {
				ICharcoalPileComponent comp = (ICharcoalPileComponent) part;
				if (comp.getTreeSpecies() != null) {
					validComps++;
					IWoodProvider woodProvider = comp.getTreeSpecies().getWoodProvider();
					int charcoalAmount = woodProvider.getCarbonization();
					while(worldObj.rand.nextFloat() < woodProvider.getCharcoalChance(charcoalAmount)){
						charcoalAmount++;
					}
					newBurnTime += charcoalAmount * 500;
				}
			}
			if(validComps > 0){
				woodBurnTime = newBurnTime;
			}else{
				woodBurnTime = 0;
			}
		}
		if (burnTime >= woodBurnTime) {
			for(IMultiblockComponent part : connectedParts) {
				BlockPos pos = part.getCoordinates();
				IBlockState state = worldObj.getBlockState(pos);
				IBlockState ashPileState = PluginArboriculture.blocks.piles.get(EnumPileType.ASH).getDefaultState().withProperty(BlockPile.PILE_POSITION, state.getValue(BlockPile.PILE_POSITION));
				
				worldObj.setBlockState(pos, ashPileState);
			}
			MultiblockRegistry.addDirtyController(worldObj, this);
		} else {
			burnTime++;
		}
		return true;
	}
	
	@Override
	public String getUnlocalizedType() {
		return "for.multiblock.charcoal.pile.type";
	}

	@Override
	public BlockPos getCoordinates() {
		BlockPos coord = getReferenceCoord();
		return new BlockPos(coord);
	}
	
	@Override
	protected void updateClient(int tickCount) {
	}

	@Override
	protected void isMachineWhole() throws MultiblockValidationException {
		int minX = getSizeLimits().getMinimumXSize();
		int minY = getSizeLimits().getMinimumYSize();
		int minZ = getSizeLimits().getMinimumZSize();
		
		if (connectedParts.size() < getSizeLimits().getMinimumNumberOfBlocksForAssembledMachine()) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.small", minX, minY, minZ));
		}
		BlockPos maximumCoord = getMaximumCoord();
		BlockPos minimumCoord = getMinimumCoord();
		
		int deltaX = maximumCoord.getX() - minimumCoord.getX() + 1;
		int deltaY = maximumCoord.getY() - minimumCoord.getY() + 1;
		int deltaZ = maximumCoord.getZ() - minimumCoord.getZ() + 1;
		int maxX = getSizeLimits().getMaximumXSize();
		int maxY = getSizeLimits().getMaximumYSize();
		int maxZ = getSizeLimits().getMaximumZSize();
		if (maxX > 0 && deltaX > maxX) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.large.x", maxX));
		}
		if (maxY > 0 && deltaY > maxY) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.large.y", maxY));
		}
		if (maxZ > 0 && deltaZ > maxZ) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.large.z", maxZ));
		}
		if (deltaX < minX) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.small.x", minX));
		}
		if (deltaY < minY) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.small.y", minY));
		}
		if (deltaZ < minZ) {
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.small.z", minZ));
		}
		
		TileEntity te;
		IMultiblockComponent part;
		Class<? extends RectangularMultiblockControllerBase> myClass = this.getClass();
		List<BlockPos> components = new ArrayList<>();
		for (int y = minimumCoord.getY(); y <= maximumCoord.getY(); y++) {
			int partsOnLayer = 0;
			int layer = y - getMinimumCoord().getY();
			
			BlockPos currentMaximumCoord = maximumCoord.add(-layer, 0, -layer);
			BlockPos currentMinimumCoord = minimumCoord.add(layer, 0, layer);
			
			for (int z = currentMinimumCoord.getZ(); z <= currentMaximumCoord.getZ(); z++) {
				for (int x = currentMinimumCoord.getX(); x <= currentMaximumCoord.getX(); x++) {
					// Okay, figure out what sort of block this should be.
					BlockPos pos = new BlockPos(x, y, z);
					te = this.worldObj.getTileEntity(pos);
					if (te instanceof IMultiblockComponent) {
						part = (IMultiblockComponent) te;
						
						// Ensure this part should actually be allowed within a cube of this controller's type
						if (!myClass.equals(part.getMultiblockLogic().getController().getClass())) {
							throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.error.invalid.part", Translator.translateToLocal(getUnlocalizedType())));
						}
					} else {
						// This is permitted so that we can incorporate certain non-multiblock parts inside interiors
						part = null;
					}
					
					// Validate block type against both part-level and material-level validators.
					int extremes = 0;

					if (x == currentMinimumCoord.getX()) {
						extremes++;
					}
					if (z == currentMinimumCoord.getZ()) {
						extremes++;
					}
					
					if (x == currentMaximumCoord.getX()) {
						extremes++;
					}
					if (y == currentMaximumCoord.getY()) {
						extremes++;
					}
					if (z == currentMaximumCoord.getZ()) {
						extremes++;
					}
					
					if (extremes >= 1) {
						// Side
						int exteriorLevel = y - currentMinimumCoord.getY();
						if (part != null) {
							isGoodForExteriorLevel(part, exteriorLevel);
						} else {
							isBlockGoodForExteriorLevel(exteriorLevel, this.worldObj, pos);
						}
					} else {
						if (part != null) {
							isGoodForInterior(part);
						} else {
							isBlockGoodForInterior(this.worldObj, pos);
						}
					}
					components.add(pos);
					partsOnLayer++;
				}
			}
			
			if (partsOnLayer < 4) {
				throw new MultiblockValidationException(Translator.translateToLocal("for.multiblock.charcoal.pile.error.small"));
			}
		}
		for(IMultiblockComponent comp : connectedParts){
			if(!components.contains(comp.getCoordinates())){
				throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.charcoal.pile.error.large"));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		data = super.writeToNBT(data);
		data.setInteger("BurnTime", burnTime);
		data.setBoolean("IsActive", active);
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		burnTime = data.getInteger("BurnTime");
		active = data.getBoolean("IsActive");
	}

	@Override
	public void formatDescriptionPacket(NBTTagCompound data) {
		writeToNBT(data);
	}

	@Override
	public void decodeDescriptionPacket(NBTTagCompound data) {
		readFromNBT(data);
	}
	
	@Override
	public void writeGuiData(DataOutputStreamForestry data) throws IOException {
		data.writeInt(burnTime);
		data.writeBoolean(active);
	}

	@Override
	public void readGuiData(DataInputStreamForestry data) throws IOException {
		burnTime = data.readInt();
		active = data.readBoolean();
	}

	@Override
	public EnumTemperature getTemperature() {
		return null;
	}

	@Override
	public EnumHumidity getHumidity() {
		return null;
	}

	@Override
	public float getExactTemperature() {
		return 0;
	}

	@Override
	public float getExactHumidity() {
		return 0;
	}

	@Override
	protected void isGoodForExteriorLevel(IMultiblockComponent part, int level) throws MultiblockValidationException {
		if(!(part instanceof ICharcoalPileComponent)){
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.charcoal.pile.error.invalid.exterior"));
		}
		ICharcoalPileComponent component = (ICharcoalPileComponent) part;
		if(component.getPileType() != EnumPileType.DIRT){
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.charcoal.pile.error.invalid.exterior"));
		}
	}

	@Override
	protected void isGoodForInterior(IMultiblockComponent part) throws MultiblockValidationException {
		if(!(part instanceof ICharcoalPileComponent)){
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.charcoal.pile.error.invalid.interior"));
		}
		ICharcoalPileComponent component = (ICharcoalPileComponent) part;
		if(component.getPileType() != EnumPileType.WOOD){
			throw new MultiblockValidationException(Translator.translateToLocalFormatted("for.multiblock.charcoal.pile.error.invalid.interior"));
		}
	}

	@Override
	public void onAssimilated(IMultiblockControllerInternal assimilator) {
	}

	@Override
	public BlockPos getMinimumCoord() {
		return super.getMinimumCoord();
	}

	@Override
	public BlockPos getMaximumCoord() {
		return super.getMaximumCoord();
	}
}
