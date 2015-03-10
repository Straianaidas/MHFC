package mhfc.net.common.quests.descriptions;

import mhfc.net.common.core.registry.MHFCQuestsRegistry;
import mhfc.net.common.quests.api.GoalDescription;

public class HuntingGoalDescription extends GoalDescription {

	private int amount;
	private Class<?> huntedClass;

	public HuntingGoalDescription(Class<?> huntedClass, int amount) {
		super(MHFCQuestsRegistry.GOAL_HUNTING_TYPE);
		this.huntedClass = huntedClass;
		this.amount = amount;
	}

	public Class<?> getHuntedClass() {
		return huntedClass;
	}

	public int getAmount() {
		return amount;
	}
}
