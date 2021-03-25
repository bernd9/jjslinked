package one.xis.sql.api;

import com.ejc.util.ObjectUtils;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Session {

    private static final ThreadLocal<Session> sessions = ThreadLocal.withInitial(Session::new);

    private Map<Class<?>, Map<Integer,Object>> sessionEntities = new HashMap<>();

    public static Session getInstance() {
        return sessions.get();
    }

    public <E> void register(E o, UnaryOperator<E> cloneOperator) {
        storeClone(cloneOperator.apply(o), System.identityHashCode(o));
    }

    <E> SqlSaveAction getSaveAction(E o, EntityFunctions<E,?> functions) {
        return getRegisteredClone(o).map(clone -> getSaveAction(o, clone, functions)).orElse(SqlSaveAction.INSERT);
    }

    private <E> SqlSaveAction getSaveAction(E orig, E clone, EntityFunctions<E,?> functions) {
        checkPrimaryKeyUnchanged(orig, clone, functions::getPk);
        return getSaveAction(orig, clone, functions::compareColumnValues);
    }

    private <E> SqlSaveAction getSaveAction(E orig, E clone, BiFunction<E,E, Boolean> compareFunction) {
        return compareFunction.apply(orig, clone) ? SqlSaveAction.NOOP : SqlSaveAction.UPDATE;
    }

    private <E> void checkPrimaryKeyUnchanged(E orig, E clone, Function<E, ?> getPkFunction) {
        if (ObjectUtils.equals(getPkFunction.apply(orig), getPkFunction.apply(clone))) {
            throw new IllegalStateException("primary key changed: " + orig);
        }
    }

    private <E> void storeClone(E clone, int hashCode) {
        sessionEntities.computeIfAbsent(clone.getClass(), c -> new HashMap<>()).put(hashCode, clone);
    }

    @SuppressWarnings("unchecked")
    private <E> Optional<E> getRegisteredClone(@NonNull E orig) {
        return Optional.ofNullable((E) sessionEntities.getOrDefault(orig.getClass(), Collections.EMPTY_MAP).get(System.identityHashCode(orig)));
    }


    public void clear() {
        sessionEntities.clear();
    }

}
