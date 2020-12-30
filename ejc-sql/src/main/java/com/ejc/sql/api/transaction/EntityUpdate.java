package com.ejc.sql.api.transaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EntityUpdate<E> implements SortableCommand {
    private final E entity;
    private final int priority;
}
