package one.xis.sql.api;

import com.ejc.api.context.UsedInGeneratedCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Wrapper;
import java.time.*;
import java.util.Date;

@RequiredArgsConstructor
public abstract class EntityResultSet<E> implements ResultSet {

    @Delegate(types = {ResultSet.class, Wrapper.class})
    private final ResultSet resultSet;

    public abstract E getEntity() throws SQLException;

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected String get_String(String columnName) throws SQLException {
        return getString(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected char get_char(String columnName) throws SQLException{
        String s = getString(columnName);
        if (s == null || s.length() > 1) {
            throw new FailedConversionException(s, char.class);
        }
        return s.toCharArray()[0];
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected Boolean get_Boolean(String columnName) throws SQLException{
        return getObject(columnName) == null ? null : getBoolean(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected boolean get_boolean(String columnName) throws SQLException{
        return getObject(columnName) == null ? null : getBoolean(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected Character get_Character(String columnName) throws SQLException{
        return getObject(columnName, Character.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected Byte get_Byte(String columnName) throws SQLException{
        return getObject(columnName, Byte.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected byte get_byte(String columnName) throws SQLException{
        return getByte(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected  Short get_Short(String columnName)  throws SQLException{
        return getObject(columnName, Short.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected short get_short(String columnName)  throws SQLException{
        return getShort(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected Integer get_Integer(String columnName)  throws SQLException{
        return getObject(columnName, Integer.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected long get_long(String columnName)  throws SQLException{
        return getLong(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected Long get_Long(String columnName)  throws SQLException{
        return getObject(columnName, Long.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected Float get_Float(String columnName)  throws SQLException{
        return getObject(columnName, Float.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected float get_float(String columnName)  throws SQLException{
        return getFloat(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected Double get_Double(String columnName)  throws SQLException {
        return getObject(columnName, Double.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected double get_double(String columnName)  throws SQLException {
        return getDouble(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected BigInteger get_BigInteger(String columnName) throws SQLException {
        return new BigInteger(getString(columnName)); // TODO does it work ?
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected LocalDate get_LocalDate(String columnName) throws SQLException {
        Date date =  getDate(columnName);
        if (date == null) return null;
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected LocalDateTime get_LocalDateTime(String columnName) throws SQLException {
        Date date =  getDate(columnName);
        if (date == null) return null;
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected LocalTime get_LocalTime(String columnName) throws SQLException {
        Date date =  getDate(columnName);
        if (date == null) return null;
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected OffsetDateTime get_OffsetDateTime(String columnName) throws SQLException {
        Date date =  getDate(columnName);
        if (date == null) return null;
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime();
    }
    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected OffsetTime get_OffsetTime(String columnName) throws SQLException {
        OffsetDateTime offsetDateTime = get_OffsetDateTime(columnName); // TODO correct ?
        return offsetDateTime == null ? null : offsetDateTime.toOffsetTime();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected ZonedDateTime get_ZonedDateTime(String columnName) throws SQLException {
        OffsetDateTime offsetDateTime = get_OffsetDateTime(columnName);
        return offsetDateTime == null ? null : offsetDateTime.toZonedDateTime();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    protected byte[] get_bytes(String columnName)  throws SQLException{
        return getBytes(columnName);
    }

}
