package mhfc.net.common.world;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

import mhfc.net.MHFCMain;
import mhfc.net.common.world.area.AreaConfiguration;
import mhfc.net.common.world.area.AreaRegistry;
import mhfc.net.common.world.area.IAreaType;
import mhfc.net.common.world.controller.CornerPosition;
import mhfc.net.common.world.controller.IRectanglePlacer;
import mhfc.net.common.world.controller.SimpleRectanglePlacer;
import mhfc.net.common.world.gen.AreaTypePlayfield;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;

public class MHFCWorldData extends WorldSavedData {
	public static class AreaInformation {
		public final IAreaType type;
		public final CornerPosition position;
		public final AreaConfiguration config;

		public AreaInformation(IAreaType type, CornerPosition position, AreaConfiguration config) {
			this.type = Objects.requireNonNull(type);
			this.position = Objects.requireNonNull(position);
			this.config = Objects.requireNonNull(config);
		}
	}

	private Collection<AreaInformation> spawnedAreas = new HashSet<>();
	private IRectanglePlacer rectanglePlacer;

	public MHFCWorldData(String nbtPropName) {
		super(nbtPropName);
		rectanglePlacer = new SimpleRectanglePlacer();
		this.markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTag) {
		NBTTagList list = nbtTag.getTagList("spawnedAreas", NBT.TAG_COMPOUND);
		NBTTagCompound placerTag = nbtTag.getCompoundTag("placer");
		this.rectanglePlacer.readFrom(placerTag);
		spawnedAreas.clear();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound confTag = list.getCompoundTagAt(i);
			String typeS = confTag.getString("type");
			NBTTagCompound dataTag = confTag.getCompoundTag("data");
			NBTTagCompound posTag = confTag.getCompoundTag("position");
			int posX = posTag.getInteger("posX");
			int posZ = posTag.getInteger("posZ");
			CornerPosition pos = new CornerPosition(posX, posZ);
			IAreaType type = AreaRegistry.instance.getType(typeS);
			if (type == null) {// and warn about invalid area
				MHFCMain.logger.error("Invalid area was found in save data of the hunting world!");
				AreaConfiguration config = AreaTypePlayfield.PLAYFIELD_TYPE.configForLoading();
				// TODO maybe clear the world data at this position
				continue;
			}
			AreaConfiguration config = type.configForLoading();
			config.readFrom(dataTag);
			spawnedAreas.add(new AreaInformation(type, pos, config));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTag) {
		NBTTagList list = new NBTTagList();
		for (AreaInformation conf : spawnedAreas) {
			String typeS = AreaRegistry.instance.getName(conf.type);
			if (typeS == null) {
				continue;
			}
			NBTTagCompound dataTag = new NBTTagCompound();
			conf.config.saveTo(dataTag);
			NBTTagCompound posTag = new NBTTagCompound();
			posTag.setInteger("posX", conf.position.posX);
			posTag.setInteger("posZ", conf.position.posY);
			NBTTagCompound confTag = new NBTTagCompound();
			confTag.setString("type", typeS);
			confTag.setTag("position", posTag);
			confTag.setTag("data", dataTag);
			list.appendTag(confTag);
		}
		NBTTagCompound placerTag = new NBTTagCompound();
		this.rectanglePlacer.saveTo(placerTag);
		nbtTag.setTag("spawnedAreas", list);
		nbtTag.setTag("placer", placerTag);
	}

	public CornerPosition newArea(IAreaType type, AreaConfiguration config) {
		CornerPosition pos = rectanglePlacer.addRectangle(config.getChunkSizeX() + 2, config.getChunkSizeZ() + 2);
		CornerPosition actual = new CornerPosition(pos.posX + 1, pos.posY + 1);
		spawnedAreas.add(new AreaInformation(type, actual, config));
		this.markDirty();
		return actual;
	}

	public Collection<AreaInformation> getAllSpawnedAreas() {
		return Collections.unmodifiableCollection(this.spawnedAreas);
	}

}