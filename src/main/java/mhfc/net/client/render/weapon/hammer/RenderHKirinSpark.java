package mhfc.net.client.render.weapon.hammer;

import org.lwjgl.opengl.GL11;

import mhfc.net.client.model.weapon.hammer.ModelHKirinSpark;
import mhfc.net.client.render.weapon.RenderWeapon;
import mhfc.net.common.util.lib.MHFCReference;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;

public class RenderHKirinSpark extends RenderWeapon<ModelHKirinSpark> {

	public RenderHKirinSpark() {
		super(new ModelHKirinSpark(), MHFCReference.weapon_hm_kirin_tex, 1.5f);
	}

	@Override
	public void preEquipped(RenderBlocks render, EntityLivingBase entityLiving) {
		GL11.glScalef(scale, scale, scale);
		GL11.glRotatef(0F, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(-5F, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(-120F, 0.0f, 0.0f, 1.0f);
		GL11.glTranslatef(-0.4F, -0.6F, -0.1F);
	}

	@Override
	public void preFirstPerson(RenderBlocks render, EntityLivingBase entityLiving) {
		float scale = 1.4f;
		GL11.glScalef(scale, scale, scale);
		GL11.glRotatef(0F, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(-5F, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(-150F, 0.0f, 0.0f, 1.0f);
		GL11.glTranslatef(-0.4F, -0.4F, -0.1F);
	}

	@Override
	public void preEntityItem(RenderBlocks render, EntityItem entityItem) {
		float scale = 3F;
		GL11.glScalef(scale, scale, scale);
		GL11.glRotatef(90F, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(0F, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(45F, 0.0f, 0.0f, 1.0f);
		GL11.glTranslatef(-0.2F, -0.6F, 0F);
	}

	@Override
	public void preInventory(RenderBlocks render) {
		float scale = 1.2F;
		GL11.glScalef(scale, scale, scale);
		GL11.glRotatef(200F, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(-80F, 0.0f, 1.0f, 0.0f);
		GL11.glTranslatef(-0.0F, -0.3F, -0.0F);
	}

}
