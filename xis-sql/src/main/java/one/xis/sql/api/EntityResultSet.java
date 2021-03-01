package one.xis.sql.api;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@RequiredArgsConstructor
public abstract class EntityResultSet<E> implements ResultSet {

    @Delegate(types = {ResultSet.class, Wrapper.class})
    private final ResultSet resultSet;

    public abstract E getEntity() throws SQLException;

    protected String get_String(String columnName) throws SQLException {
        return getString(columnName);
    }

    protected char get_char(String columnName) throws SQLException{
        String s = getString(columnName);
        if (s == null || s.length() > 1) {
            throw new FailedConversionException(s, char.class);
        }
        return s.toCharArray()[0];
    }

    protected Boolean get_Boolean(String columnName) throws SQLException{
        return getObject(columnName) == null ? null : getBoolean(columnName);
    }

    protected boolean get_boolean(String columnName) throws SQLException{
        return getObject(columnName) == null ? null : getBoolean(columnName);
    }


    protected Character get_Character(String columnName) throws SQLException{
        return getObject(columnName) == null ? null : get_char(columnName);
    }

    protected Byte get_Byte(String columnName) throws SQLException{
        return getObject(columnName) == null ? null : getByte(columnName);
    }

    protected byte get_byte(String columnName) throws SQLException{
        return getByte(columnName);
    }

    protected  Short get_Short(String columnName)  throws SQLException{
        return getObject(columnName) == null ? null : getShort(columnName);
    }

    protected short get_short(String columnName)  throws SQLException{
        return getShort(columnName);
    }

    protected Integer get_Integer(String columnName)  throws SQLException{
        return getObject(columnName) == null ? null : getInt(columnName);
    }


    protected long get_long(String columnName)  throws SQLException{
        return getLong(columnName);
    }

    protected Long get_Long(String columnName)  throws SQLException{
        return getObject(columnName) == null ? null : getLong(columnName);
    }



    protected Float get_Float(String columnName)  throws SQLException{
        return getObject(columnName) == null ? null : getFloat(columnName);
    }

    protected float get_float(String columnName)  throws SQLException{
        return getFloat(columnName);
    }

    protected Double get_Double(String columnName)  throws SQLException {
        return getObject(columnName) == null ? null : getDouble(columnName);
    }

    protected double get_double(String columnName)  throws SQLException {
        return getDouble(columnName);
    }

    protected BigInteger get_BigInteger(String columnName) throws SQLException {
        return new BigInteger(getString(columnName)); // TODO does it work ?
    }


    protected LocalDate get_LocalDate(String columnName) throws SQLException {
        Date date =  getDate(columnName);
        if (date == null) return null;
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    // TODO more
}
