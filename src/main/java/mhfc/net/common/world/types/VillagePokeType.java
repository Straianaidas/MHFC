package mhfc.net.common.world.types;

import mhfc.net.common.quests.world.SpawnControllerAdapter.SpawnInformation;
import mhfc.net.common.quests.world.SpawnControllerAdapter.Spawnable;
import mhfc.net.common.world.area.AreaConfiguration;
import mhfc.net.common.world.area.EmptyArea;
import mhfc.net.common.world.area.IArea;
import mhfc.net.common.world.area.IExtendedConfiguration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class VillagePokeType extends AreaTypeSchematic {

	public static final ResourceLocation schematicLocation = new ResourceLocation(
			"mhfc:schematics/village_poke.schematic");
	public static final VillagePokeType INSTANCE = new VillagePokeType();

	public static class VillagePokeArea extends EmptyArea {
		public VillagePokeArea(World world, AreaConfiguration config) {
			super(world, config);
		}

		public VillagePokeArea(World world) {
			super(world);
		}

		@Override
		public void teleportToSpawn(EntityPlayer player) {
			double posX = 10;
			double posY = 138;
			double posZ = 22;
			worldView.moveEntityTo(player, posX, posY, posZ);
		}

		@Override
		public String getUnlocalizedDisplayName() {
			return "area.village_poke.name";
		}

		@Override
		public SpawnInformation constructDefaultSpawnInformation(Spawnable entity) {
			return new SpawnInformation(entity, 10, 100, 10);
		}
	}

	public VillagePokeType() {
		super(schematicLocation);
	}

	@Override
	public IArea provideForLoading(World world) {
		return new VillagePokeArea(world);
	}

	@Override
	public IExtendedConfiguration configForLoading() {
		return IExtendedConfiguration.EMPTY;
	}

	@Override
	protected IArea areaToPopulate(World world, AreaConfiguration configuration) {
		return new VillagePokeArea(world, configuration);
	}

}
