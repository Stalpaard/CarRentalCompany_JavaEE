package session;

import java.rmi.RemoteException;
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
    public Set<String> getAllRentalCompanies() throws RemoteException {
        return new HashSet<>(em.createNamedQuery("getAllRentalCompanyNames").getResultList());
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        List<CarType> available = em.createNamedQuery("getAvailableCarTypesInPeriod")
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
        return available;
    }

    @Override
    public Quote createQuote(String renter, Date start, Date end, String carType, String region) throws RemoteException, ReservationException {
        try {
            for(CarRentalCompany crc : (List<CarRentalCompany>)em.createNamedQuery("getAllRentalCompanies").getResultList())
            {
                try{
                    Quote q = crc.createQuote(new ReservationConstraints(start, end, carType, region), renter);
                    quotes.add(q);
                    return q; 

                }
                catch(ReservationException | IllegalArgumentException e)
                {
                    System.out.println(e.getMessage());
                }
            }
        } catch(Exception e) {
            throw new RemoteException(e.getMessage());
        }
        throw new ReservationException("No quotes possible with given constraints");
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<>();
        try {
            for (Quote quote : quotes) {
                CarRentalCompany crc = em.find(CarRentalCompany.class, quote.getRentalCompany());
                if(crc == null) throw new ReservationException("Company doesn't exist anymore");
                done.add(crc.confirmQuote(quote));
                em.merge(crc);
            }
        } catch (ReservationException e) {
            context.setRollbackOnly();
            quotes.clear();
            throw e;
        }
        quotes.clear();
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
    
    @Override
    public String getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        CarType cheapest = (CarType)em.createNamedQuery("getCheapestCarTypeInPeriodAndRegion")
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("region", region)
                .getResultList()
                .get(0);
        if(cheapest == null) throw new RemoteException("No cheapest car type available");
        return cheapest.getName();
    }
}