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
package lineage2.commons.net.nio;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 * @version $Revision: 1.0 $
 * @param <T>
 */
abstract class AbstractPacket<T>
{
	/**
	 * Method getByteBuffer.
	 * @return ByteBuffer
	 */
	protected abstract ByteBuffer getByteBuffer();
	
	/**
	 * Method getClient.
	 * @return T
	 */
	public abstract T getClient();
}
