package mhfc.net.common.entity.mob;

import mhfc.net.common.ai.AIStancedActionManager;
import mhfc.net.common.ai.IExecutableAction;
import mhfc.net.common.ai.IStancedActionManager;
import mhfc.net.common.ai.IStancedManagedActions;
import mhfc.net.common.ai.rathalos.*;
import mhfc.net.common.entity.type.EntityMHFCBase;
import mhfc.net.common.entity.type.EntityMHFCPart;
import mhfc.net.common.entity.type.IConfusable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityRathalos extends EntityMHFCBase<EntityRathalos>
	implements
		IStancedManagedActions<EntityRathalos, EntityRathalos.Stances>,
		IConfusable {

	public static enum Stances
		implements
			IStancedActionManager.Stance<EntityRathalos, Stances> {
		GROUND,
		FLYING,
		FALLING,
		BLINDED {
			@Override
			public void onAttackStart(
				IExecutableAction<? super EntityRathalos> attack,
				EntityRathalos entity) {
				entity.confusedAttacks++;
			}

			@Override
			public void onAttackEnd(
				IExecutableAction<? super EntityRathalos> attack,
				EntityRathalos entity) {
				if (entity.getNumberOfConfusedAttacks() == 3) {
					entity.getAttackManager().setNextStance(GROUND);
				}
			}
		};

		@Override
		public void onAttackEnd(
			IExecutableAction<? super EntityRathalos> attack,
			EntityRathalos entity) {
		}

		@Override
		public void onAttackStart(
			IExecutableAction<? super EntityRathalos> attack,
			EntityRathalos entity) {
		}

		@Override
		public void onStanceStart() {
		}

		@Override
		public void onStanceEnd() {
		}

	}

	private final AIStancedActionManager<EntityRathalos, Stances> attackManager;
	private int confusedAttacks;

	public EntityRathalos(World world) {
		super(world);
		tasks.removeTask(super.attackManager);
		this.attackManager = new AIStancedActionManager<EntityRathalos, Stances>(
			this, Stances.GROUND);
		attackManager.registerAttack(new BiteAttack());
		attackManager.registerAttack(new ChargeAttack());
		attackManager.registerAttack(new FireballAttack());
		attackManager.registerAttack(new FlyStart());
		attackManager.registerAttack(new JumpFireball());
		attackManager.registerAttack(new TailSpin());
		attackManager.registerAttack(new FlyLand());
		tasks.addTask(0, attackManager);
	}

	@Override
	protected void applyEntityAttributes() {

		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(
			healthbaseHP(6000D, 7800D, 8600D));

	}

	@Override
	public EntityMHFCPart[] getParts() {
		return null;
	}

	@Override
	public AIStancedActionManager<EntityRathalos, Stances> getAttackManager() {
		return attackManager;
	}

	@Override
	public void confuse() {
		confusedAttacks = 0;
		attackManager.setNextStance(Stances.BLINDED);
	}

	@Override
	public int getNumberOfConfusedAttacks() {
		return confusedAttacks;
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		//
	}

	/**
	 * FIXME This should disable falling during flying but it also makes
	 * collision weird I don't know if it disables AI movement as well
	 */
	@Override
	protected void updateFallState(double par1, boolean par3) {

		if (attackManager.getCurrentStance() == Stances.FLYING) {
			this.motionY = 0;
			this.fallDistance = 0;
			par1 = 0;
		}

		super.updateFallState(par1, par3);
	}
}
