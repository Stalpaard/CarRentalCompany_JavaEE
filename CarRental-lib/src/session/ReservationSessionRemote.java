package session;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationException;

@Remote
public interface ReservationSessionRemote {
    
    public void setRenterName(String name) throws RemoteException;
    
    public String getRenterName() throws RemoteException;
    
    public Set<String> getAllRentalCompanies() throws RemoteException;
    
    public List<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;
    
    public Quote createQuote(String renter, Date start, Date end, String carType, String region) throws RemoteException, ReservationException;
    
    public List<Quote> getCurrentQuotes() throws RemoteException;
    
    public List<Reservation> confirmQuotes() throws RemoteException, ReservationException;

    public String getCheapestCarType(Date start, Date end, String region) throws RemoteException;
    
}