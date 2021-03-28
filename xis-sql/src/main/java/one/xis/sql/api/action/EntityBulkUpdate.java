package one.xis.sql.api.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class EntityBulkUpdate<E> {
    private final Collection<E> entities;
}
