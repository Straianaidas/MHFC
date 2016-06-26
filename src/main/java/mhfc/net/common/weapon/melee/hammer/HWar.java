package mhfc.net.common.weapon.melee.hammer;

import mhfc.net.common.helper.MHFCWeaponMaterialHelper;
import mhfc.net.common.util.lib.MHFCReference;

public class HWar extends HammerClass {

	public HWar() {
		super(MHFCWeaponMaterialHelper.HWarHammer, 500);
		labelWeaponRarity(1);
		elementalType(false, false, false, false, false, false, false, false);
		setUnlocalizedName(MHFCReference.weapon_hm_war_name);
	}

}