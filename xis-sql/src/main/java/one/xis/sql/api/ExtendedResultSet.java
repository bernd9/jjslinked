package one.xis.sql.api;

import com.ejc.api.context.UsedInGeneratedCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.time.*;
import java.util.Date;

@RequiredArgsConstructor
public class ExtendedResultSet implements ResultSet {

    @Delegate(types = {ResultSet.class, Wrapper.class})
    private final ResultSet resultSet;

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public String get_String(String columnName) throws SQLException {
        return getString(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public char get_char(String columnName) throws SQLException {
        String s = getString(columnName);
        if (s == null || s.length() > 1) {
            throw new FailedConversionException(s, char.class);
        }
        return s.toCharArray()[0];
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Boolean get_Boolean(String columnName) throws SQLException {
        return getObject(columnName) == null ? null : getBoolean(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public boolean get_boolean(String columnName) throws SQLException {
        return getObject(columnName) == null ? null : getBoolean(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Character get_Character(String columnName) throws SQLException {
        return getObject(columnName, Character.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Byte get_Byte(String columnName) throws SQLException {
        return getObject(columnName, Byte.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public byte get_byte(String columnName) throws SQLException {
        return getByte(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Short get_Short(String columnName) throws SQLException {
        return getObject(columnName, Short.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public short get_short(String columnName) throws SQLException {
        return getShort(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Integer get_Integer(String columnName) throws SQLException {
        return getObject(columnName, Integer.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public long get_long(String columnName) throws SQLException {
        return getLong(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Long get_Long(String columnName) throws SQLException {
        return getObject(columnName, Long.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Float get_Float(String columnName) throws SQLException {
        return getObject(columnName, Float.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public float get_float(String columnName) throws SQLException {
        return getFloat(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Double get_Double(String columnName) throws SQLException {
        return getObject(columnName, Double.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public double get_double(String columnName) throws SQLException {
        return getDouble(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public BigInteger get_BigInteger(String columnName) throws SQLException {
        return new BigInteger(getString(columnName)); // TODO does it work ?
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public LocalDate get_LocalDate(String columnName) throws SQLException {
        Date date = getDate(columnName);
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public LocalDateTime get_LocalDateTime(String columnName) throws SQLException {
        Date date = getDate(columnName);
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public LocalTime get_LocalTime(String columnName) throws SQLException {
        Date date = getDate(columnName);
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public OffsetDateTime get_OffsetDateTime(String columnName) throws SQLException {
        Date date = getDate(columnName);
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public OffsetTime get_OffsetTime(String columnName) throws SQLException {
        OffsetDateTime offsetDateTime = get_OffsetDateTime(columnName); // TODO correct ?
        return offsetDateTime == null ? null : offsetDateTime.toOffsetTime();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public ZonedDateTime get_ZonedDateTime(String columnName) throws SQLException {
        OffsetDateTime offsetDateTime = get_OffsetDateTime(columnName);
        return offsetDateTime == null ? null : offsetDateTime.toZonedDateTime();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public byte[] get_bytes(String columnName) throws SQLException {
        return getBytes(columnName);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public String get_String(int columnIndex) throws SQLException {
        return getString(columnIndex);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public char get_char(int columnIndex) throws SQLException {
        String s = getString(columnIndex);
        if (s == null || s.length() > 1) {
            throw new FailedConversionException(s, char.class);
        }
        return s.toCharArray()[0];
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Boolean get_Boolean(int columnIndex) throws SQLException {
        return getObject(columnIndex) == null ? null : getBoolean(columnIndex);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public boolean get_boolean(int columnIndex) throws SQLException {
        return getObject(columnIndex) == null ? null : getBoolean(columnIndex);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Character get_Character(int columnIndex) throws SQLException {
        return getObject(columnIndex, Character.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Byte get_Byte(int columnIndex) throws SQLException {
        return getObject(columnIndex, Byte.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public byte get_byte(int columnIndex) throws SQLException {
        return getByte(columnIndex);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Short get_Short(int columnIndex) throws SQLException {
        return getObject(columnIndex, Short.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public short get_short(int columnIndex) throws SQLException {
        return getShort(columnIndex);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Integer get_Integer(int columnIndex) throws SQLException {
        return getObject(columnIndex, Integer.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public long get_long(int columnIndex) throws SQLException {
        return getLong(columnIndex);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Long get_Long(int columnIndex) throws SQLException {
        return getObject(columnIndex, Long.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Float get_Float(int columnIndex) throws SQLException {
        return getObject(columnIndex, Float.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public float get_float(int columnIndex) throws SQLException {
        return getFloat(columnIndex);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public Double get_Double(int columnIndex) throws SQLException {
        return getObject(columnIndex, Double.class);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public double get_double(int columnIndex) throws SQLException {
        return getDouble(columnIndex);
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public BigInteger get_BigInteger(int columnIndex) throws SQLException {
        return new BigInteger(getString(columnIndex)); // TODO does it work ?
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public LocalDate get_LocalDate(int columnIndex) throws SQLException {
        Date date = getDate(columnIndex);
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public LocalDateTime get_LocalDateTime(int columnIndex) throws SQLException {
        Date date = getDate(columnIndex);
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public LocalTime get_LocalTime(int columnIndex) throws SQLException {
        Date date = getDate(columnIndex);
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public OffsetDateTime get_OffsetDateTime(int columnIndex) throws SQLException {
        Date date = getDate(columnIndex);
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public OffsetTime get_OffsetTime(int columnIndex) throws SQLException {
        OffsetDateTime offsetDateTime = get_OffsetDateTime(columnIndex); // TODO correct ?
        return offsetDateTime == null ? null : offsetDateTime.toOffsetTime();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public ZonedDateTime get_ZonedDateTime(int columnIndex) throws SQLException {
        OffsetDateTime offsetDateTime = get_OffsetDateTime(columnIndex);
        return offsetDateTime == null ? null : offsetDateTime.toZonedDateTime();
    }

    @UsedInGeneratedCode
    @SuppressWarnings("unused")
    public byte[] get_bytes(int columnIndex) throws SQLException {
        return getBytes(columnIndex);
    }


}
