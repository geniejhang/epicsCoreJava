/**
 * Copyright - See the COPYRIGHT that is included with this distribution.
 * EPICS JavaIOC is distributed subject to a Software License Agreement found
 * in file LICENSE that is included with this distribution.
 */
package org.epics.pvData.factory;

import java.nio.ByteBuffer;

import org.epics.pvData.pv.Array;
import org.epics.pvData.pv.MessageType;
import org.epics.pvData.pv.PVArray;
import org.epics.pvData.pv.PVStructure;

/**
 * Abstract base class for any PVArray field.
 * Any code that implements a PVArray field for an IOC database should extend this class.
 * @author mrk
 *
 */
public abstract class AbstractPVArray extends AbstractPVField implements PVArray{
    /**
     * For use by derived classes.
     */
    protected int length = 0;
    /**
     * For use by derived classes.
     */
    protected int capacity = 0;
    /**
     * For use by derived classes.
     */
    protected boolean capacityMutable = true;
    
    /**
     * Constructor that derived classes must call.
     * @param parent The parent interface.
     * @param array The reflection interface for the PVArray data.
     */
    protected AbstractPVArray(PVStructure parent,Array array) {
        super(parent,array);
    }
    /* (non-Javadoc)
     * @see org.epics.pvData.pv.PVArray#setCapacity(int)
     */
    abstract public void setCapacity(int capacity);
    /* (non-Javadoc)
     * @see org.epics.pvData.factory.AbstractPVField#setImmutable()
     */
    @Override
    public void setImmutable() {
        capacityMutable = false;
        super.setImmutable();
    }
    /* (non-Javadoc)
     * @see org.epics.pvData.pv.PVArray#getArray()
     */
    @Override
    public Array getArray() {
        return (Array)getField();
    }
    /* (non-Javadoc)
     * @see org.epics.pvData.pv.PVArray#isCapacityMutable()
     */
    @Override
    public boolean isCapacityMutable() {
        return capacityMutable;
    }
    /* (non-Javadoc)
     * @see org.epics.pvData.pv.PVArray#setCapacityMutable(boolean)
     */
    @Override
    public void setCapacityMutable(boolean isMutable) {
        if(isMutable && super.isImmutable()) {
            super.message("field is immutable", MessageType.error);
            return;
        }
        capacityMutable = isMutable;
    }
    /* (non-Javadoc)
     * @see org.epics.pvData.pv.PVArray#getCapacity()
     */
    @Override
    public int getCapacity() {
        return capacity;
    }
    /* (non-Javadoc)
     * @see org.epics.pvData.pv.PVArray#getLength()
     */
    @Override
    public int getLength() {
        return length;
    }
    /* (non-Javadoc)
     * @see org.epics.pvData.pv.PVArray#setLength(int)
     */
    @Override
    public void setLength(int len) {
        if(super.isImmutable()) {
            super.message("field is immutable", MessageType.error);
            return;
        }
        if(len>capacity) setCapacity(len);
        length = len;
    }
    /* (non-Javadoc)
	 * @see org.epics.pvData.pv.Serializable#serialize(java.nio.ByteBuffer)
	 */
    @Override
	public void serialize(ByteBuffer buffer) {
		serialize(buffer, 0, -1);
	}
	
}
