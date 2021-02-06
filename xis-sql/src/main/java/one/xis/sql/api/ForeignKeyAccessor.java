package one.xis.sql.api;

import java.util.stream.Stream;

class ForeignKeyAccessor<EID, FID> {

    void deleteAllChildNodes(EID parentId) {

    }

    void setFkToNullForAllChildNodes(EID parentId) {

    }

    public void deleteWhereNotIn(Stream<FID> retainFieldIds) {
    }

    public void setFkToNullExcept(Stream<FID> retainFieldIds) {

    }
}
