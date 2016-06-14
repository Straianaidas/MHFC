package mhfc.net.common.item.tools;

import mhfc.net.MHFCMain;
import mhfc.net.common.core.registry.MHFCItemRegistry;
import mhfc.net.common.item.AbstractSubTypedItem;
import mhfc.net.common.item.ItemColor;
import mhfc.net.common.item.tools.ItemPaintball.PaintballType;
import mhfc.net.common.util.SubTypedItem;
import mhfc.net.common.util.lib.MHFCReference;
import net.minecraft.item.Item;

public class ItemPaintball extends AbstractSubTypedItem<PaintballType> {

	public static enum PaintballType implements SubTypedItem.SubTypeEnum<Item> {
		BLACK("black", ItemColor.BLACK),
		RED("red", ItemColor.RED),
		GREEN("green", ItemColor.GREEN),
		BROWN("brown", ItemColor.BROWN),
		BLUE("blue", ItemColor.BLUE),
		PURPLE("purple", ItemColor.PURPLE),
		CYAN("cyan", ItemColor.CYAN),
		SILVER("silver", ItemColor.SILVER),
		GRAY("gray", ItemColor.GRAY),
		PINK("pink", ItemColor.PINK),
		LIME("lime", ItemColor.LIME),
		YELLOW("yellow", ItemColor.YELLOW),
		LIBLUE("light_blue", ItemColor.LIBLUE),
		MAGNTA("magenta", ItemColor.MAGNTA),
		ORANGE("orange", ItemColor.ORANGE),
		WHITE("white", ItemColor.WHITE);

		public final String name;
		public final String texture;
		public final ItemColor color;

		private PaintballType(String name, ItemColor color) {
			this.name = name;
			this.texture = MHFCReference.base_monster_gem;
			this.color = color;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getTexPath() {
			return texture;
		}

		@Override
		public Item getBaseItem() {
			return MHFCItemRegistry.MHFCItemPaintball;
		}

		@Override
		public ItemColor getColor() {
			return color;
		}
	}

	public ItemPaintball() {
		super(PaintballType.class);
		setUnlocalizedName(MHFCReference.item_paintball_basename);
		setCreativeTab(MHFCMain.mhfctabs);
		setMaxStackSize(64);
	}
}
