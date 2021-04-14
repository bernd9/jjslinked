package one.xis.sql.processor;

import one.xis.sql.CrudRepository;
import one.xis.sql.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {


}