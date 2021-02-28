package one.xis.sql.api;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.math.BigDecimal;
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

    char getChar(int columnIndex) throws SQLException{
        String s = getString(columnIndex);
        if (s == null || s.length() > 1) {
            throw new FailedConversionException(s, char.class);
        }
        return s.toCharArray()[0];
    }

    Boolean getBooleanObject(int columnIndex) throws SQLException{
        return getObject(columnIndex) == null ? null : getBoolean(columnIndex);
    }

    Character getCharacter(int columnIndex) throws SQLException{
        return getObject(columnIndex) == null ? null : getChar(columnIndex);
    }

    Byte getByteObject(int columnIndex) throws SQLException{
        return getObject(columnIndex) == null ? null : getByte(columnIndex);
    }

    Short getShortObject(int columnIndex)  throws SQLException{
        return getObject(columnIndex) == null ? null : getShort(columnIndex);
    }

    Integer getInteger(int columnIndex)  throws SQLException{
        return getObject(columnIndex) == null ? null : getInt(columnIndex);
    }

    Float getFloatObject(int columnIndex)  throws SQLException{
        return getObject(columnIndex) == null ? null : getFloat(columnIndex);
    }

    Double getDoubleObject(int columnIndex)  throws SQLException {
        return getObject(columnIndex) == null ? null : getDouble(columnIndex);
    }

    BigInteger getBigInteger(int columnIndex) throws SQLException {
        return new BigInteger(getString(columnIndex)); // TODO does it work ?
    }


    LocalDate getLocalDate(int columnIndex) throws SQLException {
        Date date =  getDate(columnIndex);
        if (date == null) return null;
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    // TODO more
}
