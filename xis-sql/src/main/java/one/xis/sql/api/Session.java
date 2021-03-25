package one.xis.sql.api;

import com.ejc.util.ObjectUtils;
import lombok.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Session {

    private static final ThreadLocal<Session> sessions = ThreadLocal.withInitial(Session::new);

    private Map<Class<?>, Map<Integer,Object>> sessionEntities = new HashMap<>();

    public static Session getInstance() {
        return sessions.get();
    }

    public void register(Object o, UnaryOperator<Object> cloneOperator) {
        storeClone(cloneOperator.apply(o), System.identityHashCode(o));
    }

    SqlSaveAction getSaveAction(Object o, EntityFunctions functions) {
        return getRegisteredClone(o).map(clone -> getSaveAction(o, clone, functions)).orElse(SqlSaveAction.INSERT);
    }

    private SqlSaveAction getSaveAction(Object orig, Object clone, EntityFunctions functions) {
        checkPrimaryKeyUnchanged(orig, clone, functions::getPk);
        return getSaveAction(orig, clone, functions::compareColumnValues);
    }

    private SqlSaveAction getSaveAction(Object orig, Object clone, BiFunction<Object,Object, Boolean> compareFunction) {
        return compareFunction.apply(orig, clone) ? SqlSaveAction.NOOP : SqlSaveAction.UPDATE;
    }

    private void checkPrimaryKeyUnchanged(Object orig, Object clone, Function<Object, Object> getPkFunction) {
        if (ObjectUtils.equals(getPkFunction.apply(orig), getPkFunction.apply(clone))) {
            throw new IllegalStateException("primary key changed: " + orig);
        }
    }

    private void storeClone(Object clone, int hashCode) {
        sessionEntities.computeIfAbsent(clone.getClass(), c -> new HashMap<>()).put(hashCode, clone);
    }

    @SuppressWarnings("unchecked")
    private Optional<Object> getRegisteredClone(@NonNull Object orig) {
        return Optional.ofNullable(sessionEntities.getOrDefault(orig.getClass(), Collections.EMPTY_MAP).get(System.identityHashCode(orig)));
    }


    public void clear() {
        sessionEntities.clear();
    }

}
