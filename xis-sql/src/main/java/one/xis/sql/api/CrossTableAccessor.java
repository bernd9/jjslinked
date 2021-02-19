package one.xis.sql.api;

import lombok.RequiredArgsConstructor;
import one.xis.sql.JdbcException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@SuppressWarnings("unused")
@RequiredArgsConstructor
abstract class CrossTableAccessor<EID, FID> extends JdbcExecutor {
    private final String fieldColumnName;
    private final Class<EID> entityKeyType;
    private final Class<FID> fieldKeyType;

    void removeReferences(EID key) {
        try (PreparedStatement st = prepare(getDeleteReferencesSql())) {
            setEntityKey(st, key);
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement");
        }
    }

    void replaceValues(EID entityKey, List<FID> valueKeys) {
        try (PreparedStatement st = prepare(createDeleteWhereNotInSql(valueKeys.size()))) {
            setEntityKey(st, entityKey);
            for (int i = 0; i < valueKeys.size(); i++) {
                setFieldKey(st, valueKeys.get(i));
            }
            st.executeUpdate();
        } catch (SQLException e) {
            throw new JdbcException("failed to close statement");
        }
    }


    private String createDeleteWhereNotInSql(int valueCount) {
        StringBuilder s = new StringBuilder();
        s.append(getDeleteReferencesSql());
        s.append(" AND ");
        s.append("`");
        s.append(fieldColumnName);
        s.append("`");
        s.append(" NOT IN ");
        s.append("(");
        for (int i = 0; i < valueCount; i++) {
            s.append("?");
            if (s.length() < valueCount) {
                s.append(",");
            }
        }
        s.append(")");
        return s.toString();
    }


    // delete from [TABLE] where [key] = ?
    abstract String getDeleteReferencesSql();

    abstract void setEntityKey(PreparedStatement st, EID key);

    abstract void setFieldKey(PreparedStatement st, FID key);


}
