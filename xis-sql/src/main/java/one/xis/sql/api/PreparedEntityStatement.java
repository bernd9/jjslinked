package one.xis.sql.api;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import one.xis.sql.JdbcException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.*;
import java.util.Date;

@RequiredArgsConstructor
public class PreparedEntityStatement implements PreparedStatement {

    // TODO Blobs byte[], tiemstampt, time
    @Delegate(types = {PreparedStatement.class, Wrapper.class})
    private final PreparedStatement st;

    public void set(int index, Object o) {
        if (o == null) {
            try {
                st.setNull(index, Types.NULL); // TODO check if this works fine
            } catch (SQLException e) {
                throw new JdbcException("can not set value: " + o, e);
            }
        } else {
            if (o.getClass().equals(BigDecimal.class)) {
                set(index, (BigDecimal) o);
            } else if (o.getClass().equals(BigInteger.class)) {
                set(index, (BigInteger) o);
            } else if (o.getClass().equals(Boolean.class)) {
                set(index, (Boolean) o);
            } else if (o.getClass().equals(Byte.class)) {
                set(index, (Byte) o);
            } else if (o.getClass().equals(Character.class)) {
                set(index, (Character) o);
            } else if (o.getClass().equals(Date.class)) {
                set(index, (Date) o);
            } else if (o.getClass().equals(Double.class)) {
                set(index, (Double) o);
            } else if (o.getClass().equals(Float.class)) {
                set(index, (Float) o);
            } else if (o.getClass().equals(Integer.class)) {
                set(index, (Integer) o);
            } else if (o.getClass().equals(java.sql.Date.class)) {
                set(index, (java.sql.Date) o);
            } else if (o.getClass().equals(LocalDate.class)) {
                set(index, (LocalDate) o);
            } else if (o.getClass().equals(LocalDateTime.class)) {
                set(index, (LocalDateTime) o);
            } else if (o.getClass().equals(Long.class)) {
                set(index, (Long) o);
            } else if (o.getClass().equals(OffsetDateTime.class)) {
                set(index, (OffsetDateTime) o);
            } else if (o.getClass().equals(OffsetTime.class)) {
                set(index, (OffsetTime) o);
            } else if (o.getClass().equals(Short.class)) {
                set(index, (Short) o);
            } else if (o.getClass().equals(ZonedDateTime.class)) {
                set(index, (ZonedDateTime) o);
            } else if (o.getClass().equals(BigDecimal.class)) {
                set(index, (BigDecimal) o);
            }
        }

    }


    public void set(int index, int value) {
        try {
            st.setInt(index, value);
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, char value) {
        try {
            st.setString(index, String.valueOf(value));
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, short value) {
        try {
            st.setShort(index, value);
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, long value) {
        try {
            st.setLong(index, value);
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, byte value) {
        try {
            st.setByte(index, value);
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, boolean value) {
        try {
            st.setBoolean(index, value);
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, float value) {
        try {
            st.setFloat(index, value);
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, double value) {
        try {
            st.setDouble(index, value);
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, Integer value) {
        try {
            if (value == null) {
                st.setNull(index, Types.INTEGER);
            } else {
                st.setInt(index, value);
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, Character value) {
        try {
            if (value == null) {
                st.setNull(index, Types.CHAR);
            } else {
                st.setString(index, String.valueOf(value));
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, Short value) {
        try {
            if (value == null) {
                st.setNull(index, Types.INTEGER);
            } else {
                st.setShort(index, value);
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, Long value) {
        try {
            if (value == null) {
                st.setNull(index, Types.BIGINT);
            } else {
                st.setLong(index, value);
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, Byte value) {
        try {
            if (value == null) {
                st.setNull(index, Types.SMALLINT);
            } else {
                st.setByte(index, value);
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, Boolean value) {
        try {
            if (value == null) {
                st.setNull(index, Types.BOOLEAN);
            } else {
                st.setBoolean(index, value);
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, Float value) {
        try {
            if (value == null) {
                st.setNull(index, Types.DECIMAL);
            } else {
                st.setFloat(index, value);
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, Double value) {
        try {
            if (value == null) {
                st.setNull(index, Types.DECIMAL);
            } else {
                st.setDouble(index, value);
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, BigInteger value) {
        try { // TODO check if this really works fine
            if (value == null) {
                st.setNull(index, Types.BIGINT);
            } else {
                st.setObject(index, value, Types.BIGINT);
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, BigDecimal value) {
        try {
            if (value == null) {
                st.setNull(index, Types.DECIMAL);
            } else {
                st.setBigDecimal(index, value);
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, Date value) {
        try {
            if (value == null) {
                st.setNull(index, Types.TIMESTAMP);
            } else {
                st.setTimestamp(index, new java.sql.Timestamp(value.getTime()));
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, java.sql.Date value) {
        try {
            if (value == null) {
                st.setNull(index, Types.DATE);
            } else {
                st.setDate(index, value);
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, LocalDate value) {
        try {
            if (value == null) {
                st.setNull(index, Types.DATE);
            } else {
                st.setDate(index, java.sql.Date.valueOf(value));
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, LocalDateTime value) {
        try {
            if (value == null) {
                st.setNull(index, Types.TIMESTAMP);
            } else {
                st.setTimestamp(index, Timestamp.valueOf(value));
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, OffsetTime value) {
        try {
            if (value == null) {
                st.setNull(index, Types.TIME);
            } else {
                st.setTime(index, Time.valueOf(value.toLocalTime()));
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }


    public void set(int index, OffsetDateTime value) {
        try {
            if (value == null) {
                st.setNull(index, Types.TIME);
            } else {
                st.setTimestamp(index, Timestamp.valueOf(value.toLocalDateTime()));
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }


    public void set(int index, ZonedDateTime value) {
        try {
            if (value == null) {
                st.setNull(index, Types.TIME);
            } else {
                st.setTimestamp(index, Timestamp.valueOf(value.toLocalDateTime()));
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, byte[] value) {
        try {
            if (value == null) {
                st.setNull(index, Types.BLOB);
            } else {
                st.setBytes(index, value);
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, Timestamp value) {
        try {
            if (value == null) {
                st.setNull(index, Types.BLOB);
            } else {
                st.setTimestamp(index, value);
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, Time value) {
        try {
            if (value == null) {
                st.setNull(index, Types.TIME);
            } else {
                st.setTime(index, value);
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, String value) {
        try {
            if (value == null) {
                st.setNull(index, Types.VARCHAR);
            } else {
                st.setString(index, value);
            }
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }


}
