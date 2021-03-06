package mhfc.net.common.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mhfc.net.common.quests.api.GoalDescription;
import mhfc.net.common.quests.api.QuestDescription;

public class QuestDescriptionRegistryData {

	public static interface IQuestDescriptionDirector {
		public void construct(QuestDescriptionRegistryData data);
	}

	public static class QuestGroupData {
		private final Map<String, Set<String>> groupMapping = new HashMap<String, Set<String>>();
		private final LinkedHashSet<String> groupIDs = new LinkedHashSet<String>();

		public Map<String, Set<String>> getGroupMapping() {
			return groupMapping;
		}

		public LinkedHashSet<String> getGroupIDs() {
			return groupIDs;
		}

		private void ensureGroupID(String groupID) {
			if (groupIDs.contains(groupID))
				return;
			groupIDs.add(groupID);
			groupMapping.put(groupID, new HashSet<String>());
		}

		public void addGroupID(String id) {
			ensureGroupID(id);
		}

		public void addQuestToGroup(String groupID, String questID) {
			ensureGroupID(groupID);
			groupMapping.get(groupID).add(questID);
		}

		public void addQuestsToGroup(String groupID, Collection<String> quests) {
			ensureGroupID(groupID);
			groupMapping.get(groupID).addAll(quests);
		}

		/**
		 * Merges the data parameter into this data. The parameter overrides previous data held.
		 */
		public void addInto(QuestGroupData data) {
			groupMapping.putAll(data.groupMapping);
			groupIDs.addAll(data.groupIDs);
		}

		public void clear() {
			groupMapping.clear();
			groupIDs.clear();
		}

		/**
		 * Orders the group identifiers as they are ordered in the iterable. If a group doesn't exist already, it is
		 * created empty.
		 */
		public void orderGroups(Iterable<String> groupIDsInOrder) {
			LinkedHashSet<String> tempOrdering = new LinkedHashSet<String>();
			for (String groupID : groupIDsInOrder) {
				ensureGroupID(groupID);
				tempOrdering.add(groupID);
			}
			tempOrdering.addAll(groupIDs);
			groupIDs.clear();
			groupIDs.addAll(tempOrdering);
		}
	}

	private final HashMap<String, QuestDescription> questDescriptions = new HashMap<String, QuestDescription>();
	private final HashMap<String, GoalDescription> goalDescriptions = new HashMap<String, GoalDescription>();
	private final QuestGroupData groupData = new QuestGroupData();

	public void fillQuestDescriptions(Map<String, QuestDescription> mapData) {
		questDescriptions.putAll(mapData);
	}

	public void putQuestDescription(String identifier, QuestDescription questDescription) {
		questDescriptions.put(identifier, questDescription);
	}

	public void fillGoalDescriptions(Map<String, GoalDescription> mapData) {
		goalDescriptions.putAll(mapData);
	}

	public void putGoalDescription(String identifier, GoalDescription questDescription) {
		goalDescriptions.put(identifier, questDescription);
	}

	public void addGroups(QuestGroupData data) {
		groupData.addInto(data);
	}

	public void addGroup(String groupID, Collection<String> quests) {
		groupData.addQuestsToGroup(groupID, quests);
	}

	public QuestDescription getQuestDescription(String id) {
		QuestDescription qd = questDescriptions.get(id);
		return qd;
	}

	public GoalDescription getGoalDescription(String id) {
		GoalDescription qd = goalDescriptions.get(id);
		return qd;
	}

	public Map<String, QuestDescription> getFullQuestDescriptionMap() {
		return Collections.unmodifiableMap(questDescriptions);
	}

	public Map<String, GoalDescription> getFullGoalDescriptionMap() {
		return Collections.unmodifiableMap(goalDescriptions);
	}

	/**
	 * <b>WARNING:</b> The objected returned here is backed by the real map
	 */
	public Map<String, Set<String>> getFullGroupMap() {
		return Collections.unmodifiableMap(groupData.groupMapping);
	}

	public List<String> getGroupsInOrder() {
		return new ArrayList<>(groupData.groupIDs);
	}

	public Set<String> getQuestIdentifiersFor(String group) {
		Set<String> identifiers = groupData.groupMapping.get(group);
		if (identifiers == null) {
			return new HashSet<>();
		}
		return new HashSet<String>(identifiers);
	}

	public void clearData() {
		questDescriptions.clear();
		goalDescriptions.clear();
		groupData.clear();
	}

}
