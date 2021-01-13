package com.ejc.sql.processor.model;


import com.ejc.sql.Entity;
import com.ejc.sql.ForeignKeyRef;
import com.ejc.sql.Id;
import com.ejc.util.FieldUtils;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

public class EntityTree {
    private final String tableName;
    private final TypeElement entity;
    private final ProcessingEnvironment processingEnvironment;


    public EntityTree(TypeElement entity, ProcessingEnvironment processingEnvironment) {
        this.entity = entity;
        this.processingEnvironment = processingEnvironment;
        this.tableName = entity.getAnnotation(Entity.class).value();
        init();
    }

    private void init() {
        entity.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(VariableElement.class::cast)
                .forEach(this::processField);
    }

    private void processField(VariableElement field) {

    }

    class EntityElement {
        private Collection<ExternalKeyRelation> externalKeyRelations = new HashSet<>();
        private Collection<LocalKeyRelation> localKeyRelations = new HashSet<>();
        private Collection<CrossTableRelation> crossTableRelations = new HashSet<>();


    }

    @RequiredArgsConstructor
    class Column {
        private final String table;
        private final String column;
    }

    interface EntityRelation {

    }


    @Data
    class ExternalKeyRelation implements EntityRelation {
        private final EntityElement localEntity;
        private final EntityElement externalKeyEntity;
        private final LocalKeyRelation localKeyRelationInExternalEntity;
        private final Supplier<Object> externalEntitySupplier;

    }

    @Data
    class LocalKeyRelation implements EntityRelation {
        private final EntityElement localEntity;
        private final String localKeyColumn;
        private final EntityElement targetEntity;
    }

    @Data
    class CrossTableRelation implements EntityRelation {
        private final EntityElement localEntity;
        private final EntityElement externalEntity;
        private final String crossTableName;
        private final String crossTableKeyColumnForLocal;
        private final String crossTableKeyColumnForExternal;
    }


    @Getter
    @RequiredArgsConstructor
    class EntityField implements Cloneable {
        private final String fieldName;
        private final String columnName;
        private final SQLType sqlType;

        void setColumnValue(int index, Object value, PreparedStatement st) throws SQLException {
            st.setObject(index, value, sqlType);
        }

        void setFieldValue(int index, Object entity, ResultSet rs) throws SQLException {
            FieldUtils.setFieldValue(entity, fieldName, rs.getObject(index, (Map<String, Class<?>>) null));
        }

        Object getFieldValue(Object entity) {
            return FieldUtils.getFieldValue(entity, columnName);
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Getter
    @RequiredArgsConstructor
    class StoredEntity<E> {
        private final E wrappedEntity;
        private final PrimaryKey primaryKey;

        boolean isEdited() {
            return true;
        }
    }

    @Getter
    @RequiredArgsConstructor
    class FieldValue {
        private final EntityField entityField;
        private final Object value;
    }


    @Getter
    class PrimaryKey implements Cloneable {
        private Object[] values;
        private EntityField[] fields;


        PrimaryKey(int length) {
            this.values = new Object[length];
            this.fields = new EntityField[length];
        }

        void setValue(int index, Object value) {
            this.values[index] = value;
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static abstract class FieldMapper<E, F> {
        abstract F getValue(E entity);
    }

    static abstract class PrimaryKeyMapper<E, PK> {
        abstract PK getPrimaryKey(E entity);
    }

    class AddressPrimaryKeyMapper extends PrimaryKeyMapper<Address, Long> {

        @Override
        Long getPrimaryKey(Address entity) {
            return entity.getId();
        }
    }

    @Getter
    @RequiredArgsConstructor
    static abstract class ForeignKeyMapper<E, KEY> {
        abstract KEY getForeignKey(E entity);
    }

    abstract static class EntityManager<E> {

        private static final EntityField[] PK_FIELDS = new EntityField[0];
        private static final EntityField[] SIMPLE_FIELDS = new EntityField[0];
        private static final EntityField[] FK_FIELDS = new EntityField[0];
        // FK_FIELD überschreibt getFieldValue() in EntityField;

        private static ThreadLocal<Set<Statement>> openStatements = ThreadLocal.withInitial(HashSet::new);

        private static final String INSERT = "";


        private static final String UPDATE = "";

        private static final String DELETE = "";

        private PreparedStatement insertStatement;

        private PreparedStatement updateStatement;

        private PreparedStatement deleteStatement;

        void save(E entity) throws SQLException {
            if (entity instanceof StoredEntity) {
                StoredEntity<E> storedEntity = (StoredEntity<E>) entity;
                if (storedEntity.isEdited()) {
                    updateEntity(storedEntity.getWrappedEntity());
                }
            } else if (isNew(entity)) {
                insertEntity(entity);
            } else {
                updateEntity(entity);
            }
        }

        void delete(E entity) throws SQLException {
            if (entity instanceof StoredEntity) {
                StoredEntity<E> storedEntity = (StoredEntity<E>) entity;
                doDelete(storedEntity.getWrappedEntity());
            } else {
                doDelete(entity);
            }
        }

        private void doDelete(E entity) throws SQLException {
            deleteEntity(entity, deleteStatement());
        }

        private PreparedStatement deleteStatement() throws SQLException {
            synchronized (DELETE) {
                if (deleteStatement == null) {
                    deleteStatement = getConnection().prepareStatement(INSERT);
                    openStatements.get().add(deleteStatement);
                }
                return deleteStatement;
            }
        }

        protected void deleteEntity(Object entity, PreparedStatement st) throws SQLException {
            // Generated:
            st.clearParameters();
            // WHERE = PK
            PK_FIELDS[0].setColumnValue(5, PK_FIELDS[0].getFieldValue(entity), st);
            PK_FIELDS[1].setColumnValue(6, PK_FIELDS[1].getFieldValue(entity), st);
            st.executeUpdate();
        }


        private boolean isNew(E entity) {
            return !pkComplete(entity);
        }

        private boolean pkComplete(E entity) {
            // GENERATED CODE
            if (PK_FIELDS[0].getFieldValue(entity) == null) {
                return false;
            }
            if (PK_FIELDS[1].getFieldValue(entity) == null) {
                return false;
            }
            return true;
        }


        private void insertEntity(Object entity) throws SQLException {
            insertEntity(entity, insertStatement());
        }

        private PreparedStatement insertStatement() throws SQLException {
            synchronized (INSERT) {
                if (insertStatement == null) {
                    insertStatement = getConnection().prepareStatement(INSERT);
                    openStatements.get().add(insertStatement);
                }
                return insertStatement;
            }
        }

        protected void insertEntity(Object entity, PreparedStatement st) throws SQLException {
            // Generated:
            st.clearParameters();
            // Simple fields
            SIMPLE_FIELDS[0].setColumnValue(0, SIMPLE_FIELDS[0].getFieldValue(entity), st);
            SIMPLE_FIELDS[1].setColumnValue(1, SIMPLE_FIELDS[1].getFieldValue(entity), st);
            SIMPLE_FIELDS[2].setColumnValue(2, SIMPLE_FIELDS[2].getFieldValue(entity), st);
            // FK-Fields
            FK_FIELDS[0].setColumnValue(3, FK_FIELDS[0].getFieldValue(entity), st);
            FK_FIELDS[1].setColumnValue(4, FK_FIELDS[1].getFieldValue(entity), st);
            // WHERE = PK
            PK_FIELDS[0].setColumnValue(5, PK_FIELDS[0].getFieldValue(entity), st);
            PK_FIELDS[1].setColumnValue(6, PK_FIELDS[1].getFieldValue(entity), st);
            st.executeUpdate();
        }

        private void updateEntity(Object entity) throws SQLException {
            updateEntity(entity, updateStatement());
        }

        private PreparedStatement updateStatement() throws SQLException {
            synchronized (UPDATE) {
                if (updateStatement == null) {
                    updateStatement = getConnection().prepareStatement(UPDATE);
                    openStatements.get().add(updateStatement);
                }
                return updateStatement;
            }
        }

        protected void updateEntity(Object entity, PreparedStatement st) throws SQLException {
            // Generated:
            st.clearParameters();
            // Simple fields
            SIMPLE_FIELDS[0].setColumnValue(0, SIMPLE_FIELDS[0].getFieldValue(entity), st);
            SIMPLE_FIELDS[1].setColumnValue(1, SIMPLE_FIELDS[1].getFieldValue(entity), st);
            SIMPLE_FIELDS[2].setColumnValue(2, SIMPLE_FIELDS[2].getFieldValue(entity), st);
            // FK-Fields
            FK_FIELDS[0].setColumnValue(3, FK_FIELDS[0].getFieldValue(entity), st);
            FK_FIELDS[1].setColumnValue(4, FK_FIELDS[1].getFieldValue(entity), st);
            // WHERE = PK
            PK_FIELDS[0].setColumnValue(5, PK_FIELDS[0].getFieldValue(entity), st);
            PK_FIELDS[1].setColumnValue(6, PK_FIELDS[1].getFieldValue(entity), st);
            st.executeUpdate();
        }

        protected Connection getConnection() {
            return null;
        }

    }


    protected PreparedStatement prepareInsert() {
        return null;
    }

}


@RequiredArgsConstructor
class FieldSupplier<T, V> implements Supplier<V> {

    private final T owner;
    private final Field field;


    @Override
    public V get() {
        return null;
    }
}

@Entity("city")
class City {

}

@Entity("customer")
class Customer {

    @ForeignKeyRef(table = "address", column = "customer_id")
    private Address address;

    @ForeignKeyRef(table = "order", column = "customer_id")
    private List<Order> orders;
}

@Getter
@Entity("address")
class Address {
    @Id
    private Long id;
    @ForeignKeyRef(table = "address", column = "city_id")
    private City city;
}

@Entity("order")
class Order {

}


