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
public class PreparedStatementWrapper {

    @Delegate
    private final PreparedStatement st;

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
            st.setObject(index, value, Types.BIGINT);
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, BigDecimal value) {
        try {
            st.setBigDecimal(index, value);
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
            st.setDate(index, value);
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
            }
            st.setTimestamp(index, java.sql.Timestamp.valueOf(value));
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }

    public void set(int index, OffsetTime value) {
        try {
            if (value == null) {
                st.setNull(index, Types.TIME);
            }
            st.setTime(index, Time.valueOf(value.toLocalTime()));
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }


    public void set(int index, OffsetDateTime value) {
        try {
            if (value == null) {
                st.setNull(index, Types.TIME);
            }
            st.setTimestamp(index, Timestamp.valueOf(value.toLocalDateTime()));
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }


    public void set(int index, ZonedDateTime value) {
        try {
            if (value == null) {
                st.setNull(index, Types.TIME);
            }
            st.setTimestamp(index, Timestamp.valueOf(value.toLocalDateTime()));
        } catch (SQLException e) {
            throw new JdbcException("can not set value: " + value, e);
        }
    }
    
}
