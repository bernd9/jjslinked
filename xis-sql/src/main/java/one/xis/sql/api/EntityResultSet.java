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

    char getChar(String columnName) throws SQLException{
        String s = getString(columnName);
        if (s == null || s.length() > 1) {
            throw new FailedConversionException(s, char.class);
        }
        return s.toCharArray()[0];
    }

    Boolean getBooleanObject(String columnName) throws SQLException{
        return getObject(columnName) == null ? null : getBoolean(columnName);
    }

    Character getCharacter(String columnName) throws SQLException{
        return getObject(columnName) == null ? null : getChar(columnName);
    }

    Byte getByteObject(String columnName) throws SQLException{
        return getObject(columnName) == null ? null : getByte(columnName);
    }

    Short getShortObject(String columnName)  throws SQLException{
        return getObject(columnName) == null ? null : getShort(columnName);
    }

    Integer getInteger(String columnName)  throws SQLException{
        return getObject(columnName) == null ? null : getInt(columnName);
    }

    Float getFloatObject(String columnName)  throws SQLException{
        return getObject(columnName) == null ? null : getFloat(columnName);
    }

    Double getDoubleObject(String columnName)  throws SQLException {
        return getObject(columnName) == null ? null : getDouble(columnName);
    }

    BigInteger getBigInteger(String columnName) throws SQLException {
        return new BigInteger(getString(columnName)); // TODO does it work ?
    }


    LocalDate getLocalDate(String columnName) throws SQLException {
        Date date =  getDate(columnName);
        if (date == null) return null;
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    // TODO more
}
