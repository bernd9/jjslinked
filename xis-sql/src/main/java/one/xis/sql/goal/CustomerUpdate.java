package one.xis.sql.goal;

import lombok.RequiredArgsConstructor;
import one.xis.sql.EntityStatement;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
class CustomerUpdate implements EntityStatement<CustomerImpl> {

    private final CustomerOrMapper orMapper;

    PreparedStatement preparedStatement() {
        return null;
    }

    @Override
    public int execute(CustomerImpl entity) {
        return 0;
    }
}
