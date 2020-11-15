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
        for(CarType c : available) System.out.println(c.toString() + " is available");
        return available;
    }

    @Override
    public Quote createQuote(String renter, Date start, Date end, String carType, String region) throws RemoteException, ReservationException {
        try {
            Quote q = null;
            try{
               for(String name : (List<String>)em.createNamedQuery("getAllRentalCompanyNames").getResultList())
                {
                    CarRentalCompany crc = (CarRentalCompany)em.find(CarRentalCompany.class, name);
                    q = crc.createQuote(new ReservationConstraints(start, end, carType, region), renter);
                    if(q != null)
                    {
                        quotes.add(q);
                        return q;  
                    }
                } 
            }
            catch(ReservationException e)
            {
                //shit happens
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
    
    @Override
    public String getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        Object cheapest = em.createNamedQuery("getCheapestCarTypeInPeriodAndRegion")
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("region", region)
                .getFirstResult();
        if(cheapest == null)
        {
            throw new RemoteException();
        }
        else
        {
            return (String) cheapest;
        }
    }
}