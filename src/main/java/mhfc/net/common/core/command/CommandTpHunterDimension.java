package mhfc.net.common.core.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mhfc.net.MHFCMain;
import mhfc.net.common.core.registry.MHFCDimensionRegistry;
import mhfc.net.common.util.world.WorldHelper;
import mhfc.net.common.world.area.AreaRegistry;
import mhfc.net.common.world.area.IActiveArea;
import mhfc.net.common.world.area.IArea;
import mhfc.net.common.world.area.IAreaType;
import mhfc.net.common.world.gen.ChunkManagerQuesting;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class CommandTpHunterDimension implements ICommand {
	private Map<EntityPlayerMP, Vec3> teleportPoints = new HashMap<EntityPlayerMP, Vec3>();

	private class AreaTeleporter extends Teleporter {
		private final IArea area;

		public AreaTeleporter(WorldServer server, IArea area) {
			super(server);
			this.area = area;
		}

		@Override
		public void placeInPortal(Entity entity, double posX, double posY, double posZ, float rotationYaw) {
			if (entity instanceof EntityPlayer && area != null) {
				area.teleportToSpawn((EntityPlayer) entity);
			} else {
				ChunkCoordinates coords = entity.worldObj.getSpawnPoint();
				Vec3 spawnAt = teleportPoints
						.getOrDefault(entity, Vec3.createVectorHelper(coords.posX, coords.posY, coords.posZ));
				entity.setLocationAndAngles(spawnAt.xCoord, spawnAt.yCoord, spawnAt.zCoord, rotationYaw, 0.0F);
				if (entity.worldObj.checkBlockCollision(entity.getBoundingBox())) {
					spawnAt.yCoord = entity.worldObj
							.getTopSolidOrLiquidBlock((int) spawnAt.xCoord, (int) spawnAt.zCoord);
					entity.setLocationAndAngles(spawnAt.xCoord, spawnAt.yCoord, spawnAt.zCoord, rotationYaw, 0.0F);
				}
			}
			entity.motionX = entity.motionY = entity.motionZ = 0.0D;
		}

		@Override
		public boolean makePortal(Entity e) {
			return false;
		}

		@Override
		public void removeStalePortalLocations(long time) {
			;
		}

	}

	@Override
	public int compareTo(Object o) {
		return -1;
	}

	@Override
	public String getCommandName() {
		return "mhfctp";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/" + getCommandName() + "[entity-selector]";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.emptyList();
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (sender instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) sender;
			EntityPlayerMP[] players = new EntityPlayerMP[] { player };
			// players = args.length > 0 ? PlayerSelector.matchPlayers(sender, args[0]) : players;
			ServerConfigurationManager mg = MinecraftServer.getServer().getConfigurationManager();
			int questWorldID = MHFCDimensionRegistry.getQuestingDimensionID();
			WorldServer server = MinecraftServer.getServer().worldServerForDimension(questWorldID);
			String areaName = args.length > 0 ? args[0] : AreaRegistry.NAME_PLAYFIELD;
			IAreaType areaType = AreaRegistry.instance.getType(areaName);
			if (areaType == null) {
				sender.addChatMessage(new ChatComponentText("Could not find requested target area type."));
				MHFCMain.logger.debug("No area type found for " + areaName);
				return;
			}
			ChunkManagerQuesting manager = (ChunkManagerQuesting) server.getWorldChunkManager();
			try (IActiveArea active = manager.getAreaManager().getUnusedInstance(areaType)) {
				Teleporter tpOverworld = new AreaTeleporter(server, null);
				Teleporter tpArea = new AreaTeleporter(server, active.getArea());
				for (EntityPlayerMP toTp : players) {
					if (toTp.dimension == questWorldID) {
						mg.transferPlayerToDimension(toTp, 0, tpOverworld);
					} else {
						teleportPoints.put(toTp, WorldHelper.getVectorOfEntity(toTp));
						mg.transferPlayerToDimension(toTp, questWorldID, tpArea);
					}
				}
			}
			MHFCMain.logger.debug("Teleported to/from dimension " + questWorldID);
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		if (sender instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) sender;
			boolean isOp = MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile());
			return isOp || player.capabilities.isCreativeMode;
		}
		return false;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
		return Collections.emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}

}