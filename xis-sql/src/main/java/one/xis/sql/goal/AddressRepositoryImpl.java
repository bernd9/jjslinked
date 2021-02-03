package one.xis.sql.goal;

import com.ejc.Singleton;

import java.util.List;

@Singleton
class AddressRepositoryImpl implements AddressRepository {

    @Override
    public void save(Address address) {

    }

    @Override
    public Address findById(Long aLong) {
        return null;
    }

    @Override
    public List<Address> findAll() {
        return null;
    }

    @Override
    public void delete(Address entity) {

    }

    void saveImpl(AddressImpl address) {

    }
}
