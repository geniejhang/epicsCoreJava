package org.epics.vtype;

import java.util.List;

import org.epics.util.array.ArrayInteger;
import org.epics.util.array.ListInteger;

public abstract class VStringArray extends Array implements AlarmProvider, TimeProvider {

    @Override
    public abstract List<String> getData();


    public static VStringArray of(List<String> data, ListInteger sizes, Alarm alarm, Time time) {
        return new IVStringArray(data, sizes, alarm, time);
    }
    
    public static VStringArray of(List<String> data, Alarm alarm, Time time) {
        return of(data, ArrayInteger.of(data.size()), alarm, time);
    }

    public static VStringArray of(List<String> data) {
        return of(data, Alarm.none(), Time.now());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Class type = typeOf(this);
        builder.append(type.getSimpleName())
                .append("[");
        builder.append(getData());
        builder.append(", size ")
                .append(getSizes())
                .append(", ")
                .append(getAlarm())
                .append(", ")
                .append(getTime())
                .append(']');
        return builder.toString();
    }

}
