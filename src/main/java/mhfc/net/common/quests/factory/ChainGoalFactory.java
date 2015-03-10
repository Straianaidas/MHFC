package mhfc.net.common.quests.factory;

import mhfc.net.MHFCMain;
import mhfc.net.common.quests.api.GoalDescription;
import mhfc.net.common.quests.api.IGoalFactory;
import mhfc.net.common.quests.api.QuestFactory;
import mhfc.net.common.quests.api.QuestGoal;
import mhfc.net.common.quests.descriptions.ChainGoalDescription;
import mhfc.net.common.quests.goals.ChainQuestGoal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

public class ChainGoalFactory implements IGoalFactory {

	@Override
	public QuestGoal buildQuestGoal(GoalDescription gd) {
		ChainGoalDescription description = (ChainGoalDescription) gd;
		ChainQuestGoal goal;
		GoalDescription truG = description.getTrueGoal(), sucG = description
				.getSuccessorGoal();
		QuestGoal dep1, dep2;
		if (sucG == null) {
			dep2 = null;
		} else
			dep2 = QuestFactory.constructGoal(sucG);
		boolean trueGoalNull = false;
		if (truG == null) {
			trueGoalNull = true;
			dep1 = null;
		} else
			dep1 = QuestFactory.constructGoal(truG);
		trueGoalNull |= dep1 == null;

		if (trueGoalNull) {
			MHFCMain.logger
					.warn("A chain goal used an invalid description as its goal. Using the successor goal instead of the chain goal");
			return dep2;
		}

		goal = new ChainQuestGoal(dep1, dep2);
		dep1.setSocket(goal);
		if (dep2 != null)
			dep2.setSocket(goal);
		return goal;

	}

	@Override
	public GoalDescription buildGoalDescription(JsonElement json,
			JsonDeserializationContext context) {
		// FIXME Auto-generated method stub
		return null;
	}

}