package one.xis.sql.goal;

import lombok.RequiredArgsConstructor;

import javax.annotation.processing.Generated;
import java.sql.SQLException;

@Generated("xis-sql")
@RequiredArgsConstructor
public class CustomerRepositoryImpl {

    private final CustomerSaveHandler customerSaveHandler;

    public void save(Customer customer) {
        customerSaveHandler.save(customer);
    }


}
