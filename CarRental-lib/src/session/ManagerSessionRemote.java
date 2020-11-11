package session;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Reservation;

@Remote
public interface ManagerSessionRemote {
    
    public Set<String> getAllRentalCompanies() throws RemoteException;
    
    public Set<CarType> getCarTypes(String company) throws RemoteException;
    
    public Set<Integer> getCarIds(String company,String type) throws RemoteException;
    
    public int getNumberOfReservations(String company, String type, int carId) throws RemoteException;
    
    public int getNumberOfReservations(String company, String type) throws RemoteException;
    
    public void addCompany(String companyCsv) throws RemoteException;
    
    public void removeCompany(String companyName) throws RemoteException;
    
    public List<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;
           
    public int getNumberOfReservationsOfRenter(String renter) throws RemoteException;
    
    public Set<String> getBestClients() throws RemoteException;
    
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws Exception;

    public String getCheapestCarType(Date start, Date end, String region) throws RemoteException;
}