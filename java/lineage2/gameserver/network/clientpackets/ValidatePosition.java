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
package lineage2.gameserver.network.clientpackets;

import lineage2.gameserver.data.BoatHolder;
import lineage2.gameserver.geodata.GeoEngine;
import lineage2.gameserver.model.Player;
import lineage2.gameserver.model.entity.boat.Boat;
import lineage2.gameserver.utils.Location;

/**
 * @author Mobius
 * @version $Revision: 1.0 $
 */
public class ValidatePosition extends L2GameClientPacket
{
	private final Location _loc = new Location();
	private int _boatObjectId;
	private Location _lastClientPosition;
	private Location _lastServerPosition;
	
	/**
	 * Method readImpl.
	 */
	@Override
	protected void readImpl()
	{
		_loc.setX(readD());
		_loc.setY(readD());
		_loc.setZ(readD());
		_loc.setHeading(readD());
		_boatObjectId = readD();
	}
	
	/**
	 * Method runImpl.
	 */
	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.isTeleporting() || activeChar.isInObserverMode())
		{
			return;
		}
		
		_lastClientPosition = activeChar.getLastClientPosition();
		_lastServerPosition = activeChar.getLastServerPosition();
		
		if (_lastClientPosition == null)
		{
			_lastClientPosition = activeChar.getLoc();
		}
		
		if (_lastServerPosition == null)
		{
			_lastServerPosition = activeChar.getLoc();
		}
		
		if ((activeChar.getX() == 0) && (activeChar.getY() == 0) && (activeChar.getZ() == 0))
		{
			correctPosition(activeChar);
			return;
		}
		
		if (activeChar.isInFlyingTransform())
		{
			if (_loc.getX() > -166168)
			{
				activeChar.setTransformation(0);
				return;
			}
			
			if ((_loc.getZ() <= 0) || (_loc.getZ() >= 6000))
			{
				// activeChar.teleToLocation(activeChar.getLoc().setZ(Math.min(5950, Math.max(50, _loc.getZ()))));
				Location loc = activeChar.getLoc();
				loc.setZ(Math.min(5950, Math.max(50, _loc.getZ())));
				activeChar.teleToLocation(loc);
				return;
			}
		}
		
		double diff = activeChar.getDistance(_loc.getX(), _loc.getY());
		int dz = Math.abs(_loc.getZ() - activeChar.getZ());
		int h = _lastServerPosition.getZ() - activeChar.getZ();
		
		if (_boatObjectId > 0)
		{
			Boat boat = BoatHolder.getInstance().getBoat(_boatObjectId);
			
			if ((boat != null) && (activeChar.getBoat() == boat))
			{
				activeChar.setHeading(_loc.getHeading());
				boat.validateLocationPacket(activeChar);
			}
			
			activeChar.setLastClientPosition(_loc.setHeading(activeChar.getHeading()));
			activeChar.setLastServerPosition(activeChar.getLoc());
			return;
		}
		
		if (activeChar.isFalling())
		{
			diff = 0;
			dz = 0;
			h = 0;
		}
		
		if (h >= 256)
		{
			activeChar.falling(h);
		}
		else if (dz >= (activeChar.isFlying() ? 1024 : 512))
		{
			if (activeChar.getIncorrectValidateCount() >= 3)
			{
				activeChar.teleToClosestTown();
			}
			else
			{
				activeChar.teleToLocation(activeChar.getLoc());
				activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
			}
		}
		else if (dz >= 256)
		{
			activeChar.validateLocation(0);
		}
		else if ((_loc.getZ() < -30000) || (_loc.getZ() > 30000))
		{
			if (activeChar.getIncorrectValidateCount() >= 3)
			{
				activeChar.teleToClosestTown();
			}
			else
			{
				correctPosition(activeChar);
				activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
			}
		}
		else if (diff > 1024)
		{
			if (activeChar.getIncorrectValidateCount() >= 3)
			{
				activeChar.teleToClosestTown();
			}
			else
			{
				activeChar.teleToLocation(activeChar.getLoc());
				activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
			}
		}
		else if (diff > 256)
		{
			activeChar.validateLocation(1);
		}
		else
		{
			activeChar.setIncorrectValidateCount(0);
		}
		
		activeChar.setLastClientPosition(_loc.setHeading(activeChar.getHeading()));
		activeChar.setLastServerPosition(activeChar.getLoc());
	}
	
	/**
	 * Method correctPosition.
	 * @param activeChar Player
	 */
	private void correctPosition(Player activeChar)
	{
		if (activeChar.isGM())
		{
			activeChar.sendMessage("Server loc: " + activeChar.getLoc());
			activeChar.sendMessage("Correcting position...");
		}
		
		if ((_lastServerPosition.getX() != 0) && (_lastServerPosition.getY() != 0) && (_lastServerPosition.getZ() != 0))
		{
			if (GeoEngine.getNSWE(_lastServerPosition.getX(), _lastServerPosition.getY(), _lastServerPosition.getZ(), activeChar.getGeoIndex()) == GeoEngine.NSWE_ALL)
			{
				activeChar.teleToLocation(_lastServerPosition);
			}
			else
			{
				activeChar.teleToClosestTown();
			}
		}
		else if ((_lastClientPosition.getX() != 0) && (_lastClientPosition.getY() != 0) && (_lastClientPosition.getZ() != 0))
		{
			if (GeoEngine.getNSWE(_lastClientPosition.getX(), _lastClientPosition.getY(), _lastClientPosition.getZ(), activeChar.getGeoIndex()) == GeoEngine.NSWE_ALL)
			{
				activeChar.teleToLocation(_lastClientPosition);
			}
			else
			{
				activeChar.teleToClosestTown();
			}
		}
		else
		{
			activeChar.teleToClosestTown();
		}
	}
}