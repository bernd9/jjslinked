package com.ejc.sql.api.transaction;

import com.ejc.sql.api.entity.EntityProxy;
import lombok.Getter;

import java.util.Comparator;
import java.util.TreeSet;

public class CommandStack {
    private static ThreadLocal<CommandStack> threadLocal = ThreadLocal.withInitial(CommandStack::new);

    public static CommandStack getInstance() {
        return threadLocal.get();
    }

    @Getter
    private TreeSet<SortableCommand> commands = new TreeSet<>(Comparator.comparing(SortableCommand::getPriority));

    public <T> void addEntity(T entity, int priority) {
        if (entity instanceof EntityProxy) {
            EntityProxy entityProxy = (EntityProxy) entity;
            commands.add(new EntityUpdate<>(entityProxy, priority));
        } else {
            commands.add(new EntityInsert<>(entity, priority));
        }
    }


}

