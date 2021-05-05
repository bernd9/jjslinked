package one.xis.sql.processor;

import one.xis.sql.api.CrossTableAccessor;
import one.xis.sql.api.CrossTableStatements;
import one.xis.sql.api.JdbcStatement;

class CustomerAgentCrossTableAccessor extends CrossTableAccessor<Long, Agent, Long> {
    private static CustomerAgentCrossTableAccessor instance = new CustomerAgentCrossTableAccessor();

    CustomerAgentCrossTableAccessor() {
        super(new CrossTableStatements("customer_agent", "customer_id", "agent_id", new AgentStatements()), new AgentFunctions());
    }

    public static CustomerAgentCrossTableAccessor getInstance() {
        return instance;
    }

    protected void setFieldKey(JdbcStatement st, int index, Long fieldPk) {
        st.set(index, fieldPk);
    }

    protected void setEntityKey(JdbcStatement st, int index, Long entityPk) {
        st.set(index, entityPk);
    }

}