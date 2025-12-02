package click.klaassen.customer;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
@Cacheable
public class Customer {

    @Id
    @GeneratedValue
    public Long id;

    @Column(length = 40)
    public String name;

    public Customer() {
    }

    public Customer(String name) {
        this.name = name;
    }
}