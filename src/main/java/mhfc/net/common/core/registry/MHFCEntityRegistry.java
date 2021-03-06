package mhfc.net.common.core.registry;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.EntityRegistry;
import mhfc.net.MHFCMain;
import mhfc.net.common.core.MHFCMobList;
import mhfc.net.common.entity.monster.EntityBarroth;
import mhfc.net.common.entity.monster.EntityDeviljho;
import mhfc.net.common.entity.monster.EntityGreatJaggi;
import mhfc.net.common.entity.monster.EntityLagiacrus;
import mhfc.net.common.entity.monster.EntityNargacuga;
import mhfc.net.common.entity.monster.EntityTigrex;
import mhfc.net.common.entity.particle.EntityPaintParticleEmitter;
import mhfc.net.common.entity.projectile.EntityBreathe;
import mhfc.net.common.entity.projectile.EntityBullet;
import mhfc.net.common.entity.projectile.EntityFlashBomb;
import mhfc.net.common.entity.projectile.EntityPaintball;
import mhfc.net.common.entity.projectile.EntityProjectileBlock;
import mhfc.net.common.entity.projectile.EntityRathalosFireball;
import mhfc.net.common.entity.projectile.EntityWyverniaArrow;
import mhfc.net.common.entity.quests.EntityQuestGiver;
import mhfc.net.common.item.ItemColor;
import mhfc.net.common.util.lib.MHFCReference;
import net.minecraft.entity.Entity;

public class MHFCEntityRegistry {
	private static int entityID = 0;
	private static final MHFCMain mod;
	private static final List<Class<? extends Entity>> registeredMobs;
	private static final List<Class<? extends Entity>> registeredProjectiles;

	public static final int tigrexID;
	//public static final int kirinID;
	//public static final int rathalosID;
	public static final int greatjaggiID;
	public static final int deviljhoID;
	public static final int nargacugaID;
	public static final int barrothID;
	//public static final int delexID;
	//public static final int giapreyID;
	//public static final int ukanlosID;
	public static final int lagiacrusID;
	//public static final int gargwaID;

	public static final int questGiverID;

	public static final int projectileBlockID;
	public static final int rathalosFireballID;
	public static final int breatheID;
	public static final int bulletID;
	public static final int flashbombID;
	public static final int paintballID;
	public static final int paintemitterID;
	public static final int arrowID;

	static {
		MHFCMain.checkPreInitialized();
		mod = MHFCMain.instance;
		registeredMobs = new ArrayList<>();
		registeredProjectiles = new ArrayList<>();

		// popoID = getMobID(EntityPopo.class, MHFCReference.mob_popo_name,
		// 0xf8248234, 0x193192);
		tigrexID = getMobID(EntityTigrex.class, MHFCReference.mob_tigrex_name, ItemColor.YELLOW, ItemColor.LIBLUE);
		//kirinID = getMobID(EntityKirin.class, MHFCReference.mob_kirin_name, 0xfff85814, 0xff851f15);
		//rathalosID = getMobID(EntityRathalos.class,	MHFCReference.mob_rathalos_name, 0xff749819, 0xf838818);
		greatjaggiID = getMobID(
				EntityGreatJaggi.class,
				MHFCReference.mob_greatjaggi_name,
				ItemColor.PURPLE,
				ItemColor.PINK);
		deviljhoID = getMobID(EntityDeviljho.class, MHFCReference.mob_deviljho_name, ItemColor.GREEN, ItemColor.SILVER);
		nargacugaID = getMobID(EntityNargacuga.class, MHFCReference.mob_nargacuga_name, 0xf351631, 0x516f13f);
		barrothID = getMobID(EntityBarroth.class, MHFCReference.mob_barroth_name, ItemColor.ORANGE, ItemColor.GRAY);
		//delexID = getMobID(EntityDelex.class, MHFCReference.mob_delex_name, 0x6f33333, 0x654321);
		//giapreyID = getMobID(EntityGiaprey.class, MHFCReference.mob_giaprey_name, 0x6f41512, 0x654321);
		//ukanlosID = getMobID(EntityUkanlos.class, MHFCReference.mob_ukanlos_name, 0x33333333, 0x654321);
		lagiacrusID = getMobID(EntityLagiacrus.class, MHFCReference.mob_lagiacrus_name, 0x6fff512, 0x6ff14f1);
		//gargwaID = getMobID(EntityGargwa.class, MHFCReference.mob_gagua_name, 0x319292, 0x2187ff20);

		questGiverID = getMobID(EntityQuestGiver.class, MHFCReference.mob_questGiver_name);

		projectileBlockID = getProjectileID(EntityProjectileBlock.class, MHFCReference.entity_tigrexBlock_name);
		bulletID = getProjectileID(EntityBullet.class, MHFCReference.entity_bullet_name);
		rathalosFireballID = getProjectileID(EntityRathalosFireball.class, MHFCReference.entity_rathalosFireball_name);
		flashbombID = getProjectileID(
				EntityFlashBomb.class,
				MHFCReference.entity_flashbomb_name,
				EntityFlashBomb.FALL_OFF_END);

		paintballID = getProjectileID(EntityPaintball.class, MHFCReference.entity_paintball_name);

		paintemitterID = getMobID(EntityPaintParticleEmitter.class, MHFCReference.mob_paint_emitter_name);
		arrowID = getProjectileID(EntityWyverniaArrow.class, MHFCReference.projectile_wyverniaarrow_name);
		breatheID = getProjectileID(EntityBreathe.class, MHFCReference.projectile_wyverniaarrow_name);
	}

	public static void init() {}

	/**
	 * returns a new (unique) mob id for the clazz provided. If the entity clazz is already registered this simply
	 * returns <code>-1</code>.
	 *
	 * @param clazz
	 * @param name
	 * @return
	 */
	private static int getMobID(Class<? extends Entity> clazz, String name) {
		if (isRegistered(clazz)) {
			return -1;
		}
		int monsterID = getMobID();
		EntityRegistry.registerModEntity(clazz, name, monsterID, mod, 64, 3, true);
		registeredMobs.add(clazz);
		MHFCMobList.addMapping(clazz, name, monsterID);
		return monsterID;
	}

	private static int getMobID(Class<? extends Entity> clazz, String name, int foreground, int background) {
		if (isRegistered(clazz)) {
			return -1;
		}
		int monsterID = getMobID();
		EntityRegistry.registerModEntity(clazz, name, monsterID, mod, 64, 3, true);
		registeredMobs.add(clazz);
		MHFCMobList.addMapping(clazz, name, monsterID, foreground, background);
		return monsterID;
	}

	private static int getMobID(
			Class<? extends Entity> clazz,
			String name,
			ItemColor foreground,
			ItemColor background) {
		return getMobID(clazz, name, foreground.getRGB(), background.getRGB());
	}

	private static int getProjectileID(Class<? extends Entity> clazz, String name) {
		if (isRegistered(clazz)) {
			return -1;
		}
		int projectileID = getProjID();
		EntityRegistry.registerModEntity(clazz, name, projectileID, mod, 64, 10, true);
		registeredProjectiles.add(clazz);
		return projectileID;
	}

	private static int getProjectileID(Class<? extends Entity> clazz, String name, int customTrackingRange) {
		if (isRegistered(clazz)) {
			return -1;
		}
		int projectileID = getProjID();
		EntityRegistry.registerModEntity(clazz, name, projectileID, mod, customTrackingRange, 10, true);
		registeredProjectiles.add(clazz);
		return projectileID;
	}

	private static int getMobID() {
		return entityID++;
	}

	private static int getProjID() {
		return entityID++;
	}

	private static boolean isRegistered(Class<?> clazz) {
		return registeredMobs.contains(clazz) || registeredProjectiles.contains(clazz);
	}

}
