package mhfc.net.common.world.exploration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import mhfc.net.MHFCMain;
import mhfc.net.common.core.registry.MHFCExplorationRegistry;
import mhfc.net.common.quests.world.GlobalAreaManager;
import mhfc.net.common.quests.world.QuestFlair;
import mhfc.net.common.util.StagedFuture;
import mhfc.net.common.world.AreaTeleportation;
import mhfc.net.common.world.area.IActiveArea;
import mhfc.net.common.world.area.IAreaType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public abstract class ExplorationAdapter implements IExplorationManager {

	protected Set<EntityPlayerMP> playerSet;
	protected Map<EntityPlayerMP, IActiveArea> playerToArea;
	private Map<IAreaType, List<IActiveArea>> areaInstances;
	private Map<IActiveArea, Set<EntityPlayerMP>> inhabitants;

	public ExplorationAdapter() {
		playerSet = new HashSet<>();
		playerToArea = new HashMap<>();
		areaInstances = new HashMap<>();
		inhabitants = new IdentityHashMap<>();
	}

	protected abstract QuestFlair getFlairFor(IAreaType type);

	protected Set<EntityPlayerMP> getInhabitants(IActiveArea activeArea) {
		inhabitants.putIfAbsent(activeArea, new HashSet<>());
		return inhabitants.get(activeArea);
	}

	protected List<IActiveArea> getAreasOfType(IAreaType type) {
		return areaInstances.getOrDefault(type, new ArrayList<>());
	}

	protected void transferIntoNewInstance(EntityPlayerMP player, IAreaType type, Consumer<IActiveArea> callback) {
		player.addChatMessage(new ChatComponentText("Teleporting to instance when the area is ready"));
		Objects.requireNonNull(player);
		Objects.requireNonNull(type);
		StagedFuture<IActiveArea> unusedInstance = GlobalAreaManager.getInstance()
				.getUnusedInstance(type, getFlairFor(type));
		unusedInstance.asCompletionStage().handle((area, ex) -> {
			try {
				if (area != null) {
					addInstance(area);
					transferIntoInstance(player, area);
				} else {
					MHFCMain.logger
							.error("Unable to create new area for player, removing player from exploration manager");
					MHFCExplorationRegistry.releasePlayer(player);
				}
			} catch (Exception exception) {
				MHFCMain.logger.error("Error during transfer into {}", area);
				MHFCExplorationRegistry.releasePlayer(player);
				if (area != null) {
					removeInstance(area);
					area = null;
				}
			} finally {
				callback.accept(area);
			}
			return area;
		});
	}

	protected void removePlayerFromInstance(EntityPlayerMP player) {
		IActiveArea currentArea = getActiveAreaOf(player);
		Set<EntityPlayerMP> inhabitantSet = getInhabitants(currentArea);
		inhabitantSet.remove(player);
		if (currentArea == null)
			return;
		if (inhabitantSet.isEmpty()) {
			removeInstance(currentArea);
		}
	}

	protected void transferIntoInstance(EntityPlayerMP player, IActiveArea area) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(area);
		removePlayerFromInstance(player);
		playerToArea.put(player, area);
		Set<EntityPlayerMP> inhabitantSet = getInhabitants(area);
		inhabitantSet.add(player);
		AreaTeleportation.movePlayerToArea(player, area.getArea());
	}

	protected void addInstance(IActiveArea activeArea) {
		Objects.requireNonNull(activeArea);
		MHFCMain.logger.debug(
				"Adding active area instance {} of type {} to exploration manager",
				activeArea,
				activeArea.getType());
		inhabitants.put(activeArea, new HashSet<>());
		getAreasOfType(activeArea.getType()).add(activeArea);
	}

	protected void removeInstance(IActiveArea activeArea) {
		Objects.requireNonNull(activeArea);
		MHFCMain.logger.debug(
				"Removing active area instance {} of type {} from exploration manager",
				activeArea,
				activeArea.getType());
		inhabitants.remove(activeArea);
		getAreasOfType(activeArea.getType()).remove(activeArea);
		activeArea.close();
	}

	@Override
	public IActiveArea getActiveAreaOf(EntityPlayerMP player) {
		Objects.requireNonNull(player);
		return playerToArea.get(player);
	}

	protected abstract void respawnWithoutInstance(EntityPlayerMP player);

	protected abstract void respawnInInstance(EntityPlayerMP player, IActiveArea instance);

	@Override
	public void respawn(EntityPlayerMP player) throws IllegalArgumentException {
		Objects.requireNonNull(player);
		if (!playerSet.contains(player)) {
			throw new IllegalArgumentException();
		}
		if (!playerToArea.containsKey(player)) {
			transferPlayerInto(player, initialAreaType(player), (t) -> {});
			return;
		}
		IActiveArea activeAreaOf = getActiveAreaOf(player);
		if (activeAreaOf == null) {
			respawnWithoutInstance(player);
		} else {
			respawnInInstance(player, activeAreaOf);
		}
	}

	protected abstract IAreaType initialAreaType(EntityPlayerMP player);

	@Override
	public void onPlayerRemove(EntityPlayerMP player) {
		Objects.requireNonNull(player);
		playerSet.remove(player);
		removePlayerFromInstance(player);
	}

	@Override
	public void onPlayerAdded(EntityPlayerMP player) {
		Objects.requireNonNull(player);
		playerSet.add(player);
	}
}