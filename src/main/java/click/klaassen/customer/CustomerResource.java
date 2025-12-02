package click.klaassen.customer;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.transaction.Transactional;

@Path("/customer")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class CustomerResource {

    @Inject
    CustomerRepository customerRepository;

    @GET
    public List<Customer> getCustomers() {
        return customerRepository.listAll();
    }

    @GET
    @Path("/{id}")
    public Customer getCustomer(@PathParam("id") Long id) {
        return customerRepository.findById(id);
    }

    @GET
    @Path("/name")
    public List<Customer> getCustomerByName(@QueryParam("name") String name) {
        return customerRepository.findByName(name);
    }

    @POST
    @Transactional
    public Response createCustomer(Customer customer) {
        customerRepository.persist(customer);
        return Response.status(Response.Status.CREATED).entity(customer).build();
    }

}