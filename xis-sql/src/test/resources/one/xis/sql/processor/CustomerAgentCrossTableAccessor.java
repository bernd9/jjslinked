package one.xis.sql.processor;

import one.xis.sql.api.CrossTableAccessor;
import one.xis.sql.api.CrossTableStatements;
import one.xis.sql.api.JdbcStatement;

class CustomerAgentCrossTableAccessor extends CrossTableAccessor<Long, Long> {

    CustomerAgentCrossTableAccessor() {
        super(new CrossTableStatements("customer_agent", "customer_id", "agent_id"));
    }

    protected void setFieldKey(JdbcStatement st, int index, Long fieldPk) {
        st.set(index, fieldPk);
    }

    protected void setEntityKey(JdbcStatement st, int index, Long entityPk) {
        st.set(index, entityPk);
    }

}