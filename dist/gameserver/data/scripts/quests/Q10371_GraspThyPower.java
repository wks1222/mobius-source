/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests;

import lineage2.gameserver.model.instances.NpcInstance;
import lineage2.gameserver.model.quest.Quest;
import lineage2.gameserver.model.quest.QuestState;
import lineage2.gameserver.scripts.ScriptFile;
import lineage2.gameserver.utils.Util;

public class Q10371_GraspThyPower extends Quest implements ScriptFile
{
	private static final int gerkenshtein = 33648;
	private static final int[] classesav =
	{
		88,
		89,
		90,
		91,
		92,
		93,
		94,
		95,
		96,
		97,
		98,
		99,
		100,
		101,
		102,
		103,
		104,
		105,
		106,
		107,
		108,
		109,
		110,
		111,
		112,
		113,
		114,
		115,
		116,
		117,
		118,
		136,
		135,
		134,
		132,
		133
	};
	private static final int Soldier = 23181;
	private static final int Warrior = 23182;
	private static final int Archer = 23183;
	private static final int Shaman = 23184;
	private static final int Bloody = 23185;
	private static final String Soldier_item = "Soldier";
	private static final String Warrior_item = "Warrior";
	private static final String Archer_item = "Archer";
	private static final String Shaman_item = "Shaman";
	private static final String Bloody_item = "Bloody";
	
	public Q10371_GraspThyPower()
	{
		super(false);
		addStartNpc(gerkenshtein);
		addTalkId(gerkenshtein);
		addKillNpcWithLog(1, Soldier_item, 12, Soldier);
		addKillNpcWithLog(1, Warrior_item, 12, Warrior);
		addKillNpcWithLog(1, Archer_item, 8, Archer);
		addKillNpcWithLog(1, Shaman_item, 8, Shaman);
		addKillNpcWithLog(1, Bloody_item, 5, Bloody);
		addLevelCheck(76, 81);
		addQuestCompletedCheck(Q10370_MenacingTimes.class);
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		
		switch (event)
		{
			case "quest_ac":
				st.setState(STARTED);
				st.setCond(1);
				st.playSound(SOUND_ACCEPT);
				htmltext = "0-4.htm";
				break;
			
			case "quest_rev":
				htmltext = "0-8.htm";
				st.getPlayer().addExpAndSp(22641900, 25729500);
				st.giveItems(57, 484990);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState qs)
	{
		String htmltext = "noquest";
		final int cond = qs.getCond();
		final int classid = qs.getPlayer().getClassId().getId();
		
		if (qs.isCompleted())
		{
			htmltext = "0-c.htm";
		}
		else if (cond == 0)
		{
			if (isAvailableFor(qs.getPlayer()) && Util.contains(classesav, classid))
			{
				htmltext = "start.htm";
			}
			else
			{
				htmltext = TODO_FIND_HTML;
			}
		}
		else if (cond == 1)
		{
			htmltext = "0-5.htm";
		}
		else if (cond == 2)
		{
			htmltext = "0-6.htm";
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (updateKill(npc, qs))
		{
			qs.unset(Soldier_item);
			qs.unset(Shaman_item);
			qs.unset(Warrior_item);
			qs.unset(Bloody_item);
			qs.unset(Archer_item);
			qs.setCond(2);
		}
		
		return null;
	}
	
	@Override
	public void onLoad()
	{
	}
	
	@Override
	public void onReload()
	{
	}
	
	@Override
	public void onShutdown()
	{
	}
}
