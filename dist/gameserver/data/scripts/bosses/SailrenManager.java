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
package bosses;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import lineage2.commons.threading.RunnableImpl;
import lineage2.commons.util.Rnd;
import lineage2.gameserver.Config;
import lineage2.gameserver.ThreadPoolManager;
import lineage2.gameserver.ai.DefaultAI;
import lineage2.gameserver.listener.actor.OnDeathListener;
import lineage2.gameserver.model.Creature;
import lineage2.gameserver.model.Player;
import lineage2.gameserver.model.Zone;
import lineage2.gameserver.model.actor.listener.CharListenerList;
import lineage2.gameserver.model.instances.NpcInstance;
import lineage2.gameserver.network.serverpackets.SocialAction;
import lineage2.gameserver.scripts.Functions;
import lineage2.gameserver.scripts.ScriptFile;
import lineage2.gameserver.utils.Location;
import lineage2.gameserver.utils.Log;
import lineage2.gameserver.utils.ReflectionUtils;
import lineage2.gameserver.utils.TimeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bosses.EpicBossState.State;

public class SailrenManager extends Functions implements ScriptFile, OnDeathListener
{
	private static final Logger _log = LoggerFactory.getLogger(SailrenManager.class);
	
	private static class ActivityTimeEnd extends RunnableImpl
	{
		public ActivityTimeEnd()
		{
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void runImpl()
		{
			sleep();
		}
	}
	
	private static class CubeSpawn extends RunnableImpl
	{
		public CubeSpawn()
		{
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void runImpl()
		{
			_teleportCube = spawn(new Location(27734, -6838, -1982, 0), TeleportCubeId);
		}
	}
	
	private static class IntervalEnd extends RunnableImpl
	{
		public IntervalEnd()
		{
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void runImpl()
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();
		}
	}
	
	private static class Social extends RunnableImpl
	{
		private final int _action;
		private final NpcInstance _npc;
		
		public Social(NpcInstance npc, int actionId)
		{
			_npc = npc;
			_action = actionId;
		}
		
		@Override
		public void runImpl()
		{
			_npc.broadcastPacket(new SocialAction(_npc.getObjectId(), _action));
		}
	}
	
	private static class onAnnihilated extends RunnableImpl
	{
		public onAnnihilated()
		{
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void runImpl()
		{
			sleep();
		}
	}
	
	private static class SailrenSpawn extends RunnableImpl
	{
		private final int _npcId;
		private final Location _pos = new Location(27628, -6109, -1982, 44732);
		
		SailrenSpawn(int npcId)
		{
			_npcId = npcId;
		}
		
		@Override
		public void runImpl()
		{
			if (_socialTask != null)
			{
				_socialTask.cancel(false);
				_socialTask = null;
			}
			switch (_npcId)
			{
				case Velociraptor:
					_velociraptor = spawn(new Location(27852, -5536, -1983, 44732), Velociraptor);
					((DefaultAI) _velociraptor.getAI()).addTaskMove(_pos, false);
					if (_socialTask != null)
					{
						_socialTask.cancel(false);
						_socialTask = null;
					}
					_socialTask = ThreadPoolManager.getInstance().schedule(new Social(_velociraptor, 2), 6000);
					if (_activityTimeEndTask != null)
					{
						_activityTimeEndTask.cancel(false);
						_activityTimeEndTask = null;
					}
					_activityTimeEndTask = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), FWS_ACTIVITYTIMEOFMOBS);
					break;
				case Pterosaur:
					_pterosaur = spawn(new Location(27852, -5536, -1983, 44732), Pterosaur);
					((DefaultAI) _pterosaur.getAI()).addTaskMove(_pos, false);
					if (_socialTask != null)
					{
						_socialTask.cancel(false);
						_socialTask = null;
					}
					_socialTask = ThreadPoolManager.getInstance().schedule(new Social(_pterosaur, 2), 6000);
					if (_activityTimeEndTask != null)
					{
						_activityTimeEndTask.cancel(false);
						_activityTimeEndTask = null;
					}
					_activityTimeEndTask = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), FWS_ACTIVITYTIMEOFMOBS);
					break;
				case Tyrannosaurus:
					_tyranno = spawn(new Location(27852, -5536, -1983, 44732), Tyrannosaurus);
					((DefaultAI) _tyranno.getAI()).addTaskMove(_pos, false);
					if (_socialTask != null)
					{
						_socialTask.cancel(false);
						_socialTask = null;
					}
					_socialTask = ThreadPoolManager.getInstance().schedule(new Social(_tyranno, 2), 6000);
					if (_activityTimeEndTask != null)
					{
						_activityTimeEndTask.cancel(false);
						_activityTimeEndTask = null;
					}
					_activityTimeEndTask = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), FWS_ACTIVITYTIMEOFMOBS);
					break;
				case Sailren:
					_sailren = spawn(new Location(27810, -5655, -1983, 44732), Sailren);
					_state.setRespawnDate(getRespawnInterval() + FWS_ACTIVITYTIMEOFMOBS);
					_state.setState(EpicBossState.State.ALIVE);
					_state.update();
					_sailren.setRunning();
					((DefaultAI) _sailren.getAI()).addTaskMove(_pos, false);
					if (_socialTask != null)
					{
						_socialTask.cancel(false);
						_socialTask = null;
					}
					_socialTask = ThreadPoolManager.getInstance().schedule(new Social(_sailren, 2), 6000);
					if (_activityTimeEndTask != null)
					{
						_activityTimeEndTask.cancel(false);
						_activityTimeEndTask = null;
					}
					_activityTimeEndTask = ThreadPoolManager.getInstance().schedule(new ActivityTimeEnd(), FWS_ACTIVITYTIMEOFMOBS);
					break;
			}
		}
	}
	
	static NpcInstance _velociraptor;
	static NpcInstance _pterosaur;
	static NpcInstance _tyranno;
	static NpcInstance _sailren;
	static NpcInstance _teleportCube;
	private static ScheduledFuture<?> _cubeSpawnTask = null;
	private static ScheduledFuture<?> _monsterSpawnTask = null;
	private static ScheduledFuture<?> _intervalEndTask = null;
	static ScheduledFuture<?> _socialTask = null;
	static ScheduledFuture<?> _activityTimeEndTask = null;
	private static ScheduledFuture<?> _onAnnihilatedTask = null;
	private static final int Sailren = 29065;
	private static final int Velociraptor = 22198;
	private static final int Pterosaur = 22199;
	private static final int Tyrannosaurus = 22217;
	private static final int TeleportCubeId = 31759;
	static EpicBossState _state;
	private static Zone _zone;
	private static Location _enter = new Location(27734, -6938, -1982);
	private static final boolean FWS_ENABLESINGLEPLAYER = Boolean.TRUE;
	private static final int FWS_ACTIVITYTIMEOFMOBS = 120 * 60000;
	private static final int FWS_FIXINTERVALOFSAILRENSPAWN = 1 * 24 * 60 * 60000;
	private static final int FWS_RANDOMINTERVALOFSAILRENSPAWN = 1 * 24 * 60 * 60000;
	private static final int FWS_INTERVALOFNEXTMONSTER = 60000;
	private static boolean _isAlreadyEnteredOtherParty = false;
	private static boolean Dying = false;
	
	private static void banishForeigners()
	{
		for (Player player : getPlayersInside())
		{
			player.teleToClosestTown();
		}
	}
	
	private synchronized static void checkAnnihilated()
	{
		if ((_onAnnihilatedTask == null) && isPlayersAnnihilated())
		{
			_onAnnihilatedTask = ThreadPoolManager.getInstance().schedule(new onAnnihilated(), 5000);
		}
	}
	
	private static List<Player> getPlayersInside()
	{
		return getZone().getInsidePlayers();
	}
	
	static int getRespawnInterval()
	{
		return (int) (Config.ALT_RAID_RESPAWN_MULTIPLIER * (FWS_FIXINTERVALOFSAILRENSPAWN + Rnd.get(0, FWS_RANDOMINTERVALOFSAILRENSPAWN)));
	}
	
	public static Zone getZone()
	{
		return _zone;
	}
	
	private void init()
	{
		CharListenerList.addGlobal(this);
		_state = new EpicBossState(Sailren);
		_zone = ReflectionUtils.getZone("[sailren_epic]");
		_log.info("SailrenManager: State of Sailren is " + _state.getState() + ".");
		if (!_state.getState().equals(EpicBossState.State.NOTSPAWN))
		{
			setIntervalEndTask();
		}
		_log.info("SailrenManager: Next spawn date of Sailren is " + TimeUtils.toSimpleFormat(_state.getRespawnDate()) + ".");
	}
	
	private static boolean isPlayersAnnihilated()
	{
		for (Player pc : getPlayersInside())
		{
			if (!pc.isDead())
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void onDeath(Creature self, Creature killer)
	{
		if (self.isPlayer() && (_state != null) && (_state.getState() == State.ALIVE) && (_zone != null) && _zone.checkIfInZone(self.getX(), self.getY()))
		{
			checkAnnihilated();
		}
		else if (self == _velociraptor)
		{
			if (_monsterSpawnTask != null)
			{
				_monsterSpawnTask.cancel(false);
			}
			_monsterSpawnTask = ThreadPoolManager.getInstance().schedule(new SailrenSpawn(Pterosaur), FWS_INTERVALOFNEXTMONSTER);
		}
		else if (self == _pterosaur)
		{
			if (_monsterSpawnTask != null)
			{
				_monsterSpawnTask.cancel(false);
			}
			_monsterSpawnTask = ThreadPoolManager.getInstance().schedule(new SailrenSpawn(Tyrannosaurus), FWS_INTERVALOFNEXTMONSTER);
		}
		else if (self == _tyranno)
		{
			if (_monsterSpawnTask != null)
			{
				_monsterSpawnTask.cancel(false);
			}
			_monsterSpawnTask = ThreadPoolManager.getInstance().schedule(new SailrenSpawn(Sailren), FWS_INTERVALOFNEXTMONSTER);
		}
		else if (self == _sailren)
		{
			onSailrenDie(killer);
		}
	}
	
	private static void onSailrenDie(Creature killer)
	{
		if (Dying)
		{
			return;
		}
		Dying = true;
		_state.setRespawnDate(getRespawnInterval());
		_state.setState(EpicBossState.State.INTERVAL);
		_state.update();
		Log.add("Sailren died", "bosses");
		_cubeSpawnTask = ThreadPoolManager.getInstance().schedule(new CubeSpawn(), 10000);
	}
	
	private static void setIntervalEndTask()
	{
		setUnspawn();
		if (_state.getState().equals(EpicBossState.State.ALIVE))
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();
			return;
		}
		if (!_state.getState().equals(EpicBossState.State.INTERVAL))
		{
			_state.setRespawnDate(getRespawnInterval());
			_state.setState(EpicBossState.State.INTERVAL);
			_state.update();
		}
		_intervalEndTask = ThreadPoolManager.getInstance().schedule(new IntervalEnd(), _state.getInterval());
	}
	
	private static void setUnspawn()
	{
		banishForeigners();
		if (_velociraptor != null)
		{
			if (_velociraptor.getSpawn() != null)
			{
				_velociraptor.getSpawn().stopRespawn();
			}
			_velociraptor.deleteMe();
			_velociraptor = null;
		}
		if (_pterosaur != null)
		{
			if (_pterosaur.getSpawn() != null)
			{
				_pterosaur.getSpawn().stopRespawn();
			}
			_pterosaur.deleteMe();
			_pterosaur = null;
		}
		if (_tyranno != null)
		{
			if (_tyranno.getSpawn() != null)
			{
				_tyranno.getSpawn().stopRespawn();
			}
			_tyranno.deleteMe();
			_tyranno = null;
		}
		if (_sailren != null)
		{
			if (_sailren.getSpawn() != null)
			{
				_sailren.getSpawn().stopRespawn();
			}
			_sailren.deleteMe();
			_sailren = null;
		}
		if (_teleportCube != null)
		{
			if (_teleportCube.getSpawn() != null)
			{
				_teleportCube.getSpawn().stopRespawn();
			}
			_teleportCube.deleteMe();
			_teleportCube = null;
		}
		if (_cubeSpawnTask != null)
		{
			_cubeSpawnTask.cancel(false);
			_cubeSpawnTask = null;
		}
		if (_monsterSpawnTask != null)
		{
			_monsterSpawnTask.cancel(false);
			_monsterSpawnTask = null;
		}
		if (_intervalEndTask != null)
		{
			_intervalEndTask.cancel(false);
			_intervalEndTask = null;
		}
		if (_socialTask != null)
		{
			_socialTask.cancel(false);
			_socialTask = null;
		}
		if (_activityTimeEndTask != null)
		{
			_activityTimeEndTask.cancel(false);
			_activityTimeEndTask = null;
		}
		if (_onAnnihilatedTask != null)
		{
			_onAnnihilatedTask.cancel(false);
			_onAnnihilatedTask = null;
		}
	}
	
	static void sleep()
	{
		setUnspawn();
		if (_state.getState().equals(EpicBossState.State.ALIVE))
		{
			_state.setState(EpicBossState.State.NOTSPAWN);
			_state.update();
		}
	}
	
	public synchronized static void setSailrenSpawnTask()
	{
		if (_monsterSpawnTask == null)
		{
			_monsterSpawnTask = ThreadPoolManager.getInstance().schedule(new SailrenSpawn(Velociraptor), FWS_INTERVALOFNEXTMONSTER);
		}
	}
	
	public static boolean isEnableEnterToLair()
	{
		return _state.getState() == EpicBossState.State.NOTSPAWN;
	}
	
	public static int canIntoSailrenLair(Player pc)
	{
		if (!FWS_ENABLESINGLEPLAYER && (pc.getParty() == null))
		{
			return 4;
		}
		else if (_isAlreadyEnteredOtherParty)
		{
			return 2;
		}
		else if (_state.getState().equals(EpicBossState.State.NOTSPAWN))
		{
			return 0;
		}
		else if (_state.getState().equals(EpicBossState.State.ALIVE) || _state.getState().equals(EpicBossState.State.DEAD))
		{
			return 1;
		}
		else if (_state.getState().equals(EpicBossState.State.INTERVAL))
		{
			return 3;
		}
		else
		{
			return 0;
		}
	}
	
	public static void entryToSailrenLair(Player pc)
	{
		if (pc.getParty() == null)
		{
			pc.teleToLocation(Location.findPointToStay(_enter, 80, pc.getGeoIndex()));
		}
		else
		{
			List<Player> members = new ArrayList<>();
			for (Player mem : pc.getParty().getPartyMembers())
			{
				if ((mem != null) && !mem.isDead() && mem.isInRange(pc, 1000))
				{
					members.add(mem);
				}
			}
			for (Player mem : members)
			{
				mem.teleToLocation(Location.findPointToStay(_enter, 80, mem.getGeoIndex()));
			}
		}
		_isAlreadyEnteredOtherParty = true;
	}
	
	@Override
	public void onLoad()
	{
		init();
	}
	
	@Override
	public void onReload()
	{
		sleep();
	}
	
	@Override
	public void onShutdown()
	{
	}
}