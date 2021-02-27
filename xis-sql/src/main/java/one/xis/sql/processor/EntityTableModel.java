package one.xis.sql.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Table model does implement {@link Comparable} in oder to
 * avoid database deadlocks.
 */
@Getter
@RequiredArgsConstructor
class EntityTableModel implements Comparable<EntityTableModel> {
    private final String tableName;
    private final String pkColumnName;

    /**
     * Foreign keys referring another table.
     */
    private Map<String, EntityTableModel> outgoingKeys = new HashMap<>();


    @Override
    public int compareTo(EntityTableModel attributes) {
        if (isReferredBy(attributes) && attributes.isReferredBy(this)) {
            return 0;
        }
        if (isReferredBy(attributes)) {
            return -1;
        }
        if (attributes.isReferredBy(this)) {
            return 1;
        }
        return 0;
    }


    private boolean isReferredBy(EntityTableModel attributes) {
        return attributes.getOutgoingKeys().containsValue(this);
    }


    @Override
    public int hashCode() {
        return tableName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        EntityTableModel attributes = (EntityTableModel) obj;
        return tableName.equals(attributes.getTableName());
    }
}


