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
    
    /**
     * Set name of the renter associated with the session
     * @param name new name
     * @throws RemoteException 
     */
    public void setRenterName(String name) throws RemoteException;
    
    /**
     * Get the name of the renter associated with the session
     * @return string of the name
     * @throws RemoteException 
     */
    public String getRenterName() throws RemoteException;
    
    /**
     * Get a set containing all rental company names
     * @return a set of strings
     * @throws RemoteException 
     */
    public Set<String> getAllRentalCompanies() throws RemoteException;
    
    /**
     * Get a list of all available car types within a specified period
     * @param start start of the period
     * @param end end of the period
     * @return a list of CarType objects
     * @throws RemoteException 
     */
    public List<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;
    
    /**
     * Try to create a quote with the given constraints
     * @param renter renter name
     * @param start start of the reservation period
     * @param end end of the reservation period
     * @param carType car type to be reserved
     * @param region reservation region
     * @return a quote made with a car rental company consistent with the given constraints
     * @throws RemoteException
     * @throws ReservationException 
     */
    public Quote createQuote(String renter, Date start, Date end, String carType, String region) throws RemoteException, ReservationException;
    
    /**
     * Get a list of the quotes associated with the client's session
     * @return a list of Quote objects
     * @throws RemoteException 
     */
    public List<Quote> getCurrentQuotes() throws RemoteException;
    
    /**
     * Try to confirm all quotes associated with the client's session
     * @return a list of Reservation objects
     * @throws RemoteException
     * @throws ReservationException 
     */
    public List<Reservation> confirmQuotes() throws RemoteException, ReservationException;

    /**
     * Get the name of the cheapest car type within a given period
     * @param start start of the period
     * @param end end of the period
     * @param region region of the car type
     * @return a string containing the name of the car type
     * @throws RemoteException 
     */
    public String getCheapestCarType(Date start, Date end, String region) throws RemoteException;
    
}