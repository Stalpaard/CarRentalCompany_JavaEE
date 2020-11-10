package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ReservationSession implements ReservationSessionRemote {

    @Resource
    private EJBContext context;
    
    @PersistenceContext
    private EntityManager em;
    
    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();

    @Override
    public Set<String> getAllRentalCompanies() {
        throw new UnsupportedOperationException("JPQL coming soon");
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        throw new UnsupportedOperationException("JPQL coming soon");
    }

    @Override
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException {
        throw new UnsupportedOperationException("JPQL coming soon");
        /*
        try {
            //Query to retrieve all rental companies
            //Quote out = RentalStore.getRental(company).createQuote(constraints, renter);
            //quotes.add(out);
            //return out;
        } catch(Exception e) {
            throw new ReservationException(e);
        }
        */
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<Reservation>();
        try {
            for (Quote quote : quotes) {
                CarRentalCompany crc = em.find(CarRentalCompany.class, quote.getRentalCompany());
                if(crc == null) throw new ReservationException("Company doesn't exist anymore");
                done.add(crc.confirmQuote(quote));
                em.merge(crc);
            }
        } catch (Exception e) {
            context.setRollbackOnly();
            //for(Reservation r:done)
               // RentalStore.getRental(r.getRentalCompany()).cancelReservation(r);
            throw new ReservationException(e);
        }
        return done;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    @Override
    public String getRenterName() {
        return renter;
    }
}