package mhfc.net.common.entity.mob;

import org.lwjgl.opengl.GL11;

import com.github.worldsender.mcanm.client.model.mcanmmodel.data.RenderPassInformation;

import mhfc.net.common.ai.entity.deviljho.DeviljhoBiteA;
import mhfc.net.common.ai.entity.deviljho.DeviljhoBiteB;
import mhfc.net.common.ai.entity.deviljho.DeviljhoIdle;
import mhfc.net.common.ai.entity.deviljho.DeviljhoRoar;
import mhfc.net.common.ai.entity.deviljho.DeviljhoTailWhip;
import mhfc.net.common.entity.type.EntityMHFCBase;
import mhfc.net.common.entity.type.EntityMHFCPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


public class EntityDeviljho extends EntityMHFCBase<EntityDeviljho> {

	public EntityDeviljho(World WORLD) {
		super(WORLD);
		setSize(4,5);
		targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		attackManager.registerAttack(new DeviljhoIdle());
		attackManager.registerAttack(new DeviljhoRoar());
		attackManager.registerAttack(new DeviljhoBiteA());
		attackManager.registerAttack(new DeviljhoBiteB());
		attackManager.registerAttack(new DeviljhoTailWhip());
	//	attackManager.registerAttack(new DeviljhoWander());
	}

	@Override
	public EntityMHFCPart[] getParts() {
		return null;
	}
	
	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttributeMap().getAttributeInstance(SharedMonsterAttributes.followRange).setBaseValue(
				128d);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance)	.setBaseValue(
				1.3D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(	healthbaseHP(
				17432D, 23874D, 29100D));
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(
				35D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(
				0.32D);
	}
	
	 public RenderPassInformation preRenderCallback(float scale, RenderPassInformation sub){
		 GL11.glScaled(3.5, 3.5, 3.5);
		 return super.preRenderCallback(scale, sub);
		 
	 }

}
