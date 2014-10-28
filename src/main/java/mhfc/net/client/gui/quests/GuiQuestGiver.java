package mhfc.net.client.gui.quests;

import java.util.ArrayList;
import java.util.List;

import mhfc.net.client.gui.ClickableGuiList;
import mhfc.net.client.gui.ClickableGuiList.GuiListStringItem;
import mhfc.net.client.quests.MHFCRegQuestVisual;
import mhfc.net.common.quests.QuestVisualInformation;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

public class GuiQuestGiver extends GuiScreen {

	private List<String> groupIDsDisplayed;
	private ClickableGuiList<GuiListStringItem> groupList;
	private GuiButton start, left, right;
	private List<String> questIdentifiers;
	private int selectedIdentifier;

	public GuiQuestGiver(String[] groupIDs, EntityPlayer accessor) {
		groupIDsDisplayed = new ArrayList<String>();
		for (int i = 0; i < groupIDs.length; i++)
			groupIDsDisplayed.add(groupIDs[i]);
		groupList = new ClickableGuiList<ClickableGuiList.GuiListStringItem>(
				width, height);
		for (int i = 0; i < groupIDs.length; i++)
			groupList.add(new GuiListStringItem(groupIDs[i]));
		start = new GuiButton(0, 0, 20, 40, 0x404040, "Take Quest") {

		};
		left = new GuiButton(selectedIdentifier, selectedIdentifier,
				selectedIdentifier, selectedIdentifier, selectedIdentifier, "<") {

		};
		right = new GuiButton(selectedIdentifier, selectedIdentifier,
				selectedIdentifier, selectedIdentifier, selectedIdentifier, ">") {

		};
	}

	@Override
	protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) {
		super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
		groupList.handleClick(p_73864_1_ - groupList.getPosX(), p_73864_2_
				- groupList.getPosY(), p_73864_3_);
		String selectedList = groupList.getSelectedItem()
				.getInitializationString();
		questIdentifiers.clear();
		questIdentifiers.addAll(MHFCRegQuestVisual
				.getIdentifierList(selectedList));
		selectedIdentifier = questIdentifiers.size() > 0 ? 0 : -1;
	}

	@Override
	public void drawScreen(int positionX, int positionY, float partialTick) {
		super.drawScreen(positionX, positionY, partialTick);
		groupList.draw();
		if (selectedIdentifier > 0) {
			left.visible = true;
			right.visible = true;
			start.enabled = false;
			QuestVisualInformation info = MHFCRegQuestVisual
					.getVisualInformation(questIdentifiers
							.get(selectedIdentifier));
			FontRenderer fontRenderer = mc.fontRenderer;
			int page = 0;
			// info.drawInformation(0, 0, 220, 220, page, fontRenderer);
		} else {
			// There is nothing to select and can't be
			start.enabled = false;
			left.visible = false;
			right.visible = false;
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
	}

	@Override
	public void initGui() {
		buttonList.add(start);
		buttonList.add(left);
		buttonList.add(right);
		super.initGui();
	}

	@Override
	public void updateScreen() {
		groupList.setPosition(5, 5);
		groupList.setWidthAndHeight(width, height);
		super.updateScreen();
	}

}
