package mhfc.net.common.core.registry;

import mhfc.net.common.crafting.MHFCCraftingManager;
import mhfc.net.common.item.materials.ItemTigrex.TigrexSubType;
import mhfc.net.common.util.SubTypedItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class MHFCHunterCraftingRegistry {

	public static void init() {
		/**
		 * Samples for Shaped Recipe & Shapeless addShapedRecipe(new
		 * ItemStack(Item.bow, 1), new Object[] {" II", "I S", "I S", "I S",
		 * " II", 'I', Item.stick, 'S', Item.silk}); addShapedRecipe(new
		 * ItemStack(Block.anvil, 1), new Object[] {"III", " i ", "iii", 'I',
		 * Block.blockIron, 'i', Item.ingotIron});
		 */

		// x.addShapedRecipe(new ItemStack(z.mhfcitemkirinhelm, 1), new Object[]
		// {} );
		MHFCCraftingManager man = MHFCCraftingManager.getInstance();

		man.addShapedRecipe(
				new ItemStack(MHFCItemRegistry.weapon_hm_tigrex, 1),
				new Object[]{"XXX", "XTX", "TXT", " S ", " S ", 'X',
						SubTypedItem.fromSubItem(TigrexSubType.SHELL, 1), 'T',
						SubTypedItem.fromSubItem(TigrexSubType.SCALE, 1), 'S',
						Items.stick});
		man.addShapedRecipe(new ItemStack(MHFCItemRegistry.weapon_gs_bone, 1),
				new Object[]{" X ", "TXT", "TXT", " S ", " S ", 'X',
						Items.iron_ingot, 'T', Items.bone, 'S', Items.stick});
		man.addShapedRecipe(
				new ItemStack(MHFCItemRegistry.MHFCItemTrapTool, 1),
				new Object[]{"   ", "   ", "XXX", "XXA", "AXA", 'X',
						MHFCItemRegistry.MHFCItemBombMaterial, 'A',
						Items.gunpowder});
		// Blocks
		man.addShapedRecipe(new ItemStack(MHFCBlockRegistry.mhfcblockstuntrap,
				1), new Object[]{"ADX", "DXF", "XXA", "FXD", "XXX", 'X',
				MHFCItemRegistry.MHFCItemTrapTool, 'A', Items.redstone, 'D',
				Items.iron_ingot, 'F', MHFCItemRegistry.MHFCItemBombMaterial});

	}

}
