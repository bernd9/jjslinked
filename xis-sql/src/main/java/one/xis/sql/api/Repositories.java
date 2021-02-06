package one.xis.sql.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import one.xis.sql.CrudRepository;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Repositories {

    static <E, ID> CrudRepository<E, ID> getCrudRepository(Class<E> entity) {
        return null;
    }

}
