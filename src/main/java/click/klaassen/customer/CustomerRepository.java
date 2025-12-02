package click.klaassen.customer;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomerRepository implements PanacheRepository<Customer> {
    
    public List<Customer> findByName(String name) {
        return find("name", name).list();
    }
}
