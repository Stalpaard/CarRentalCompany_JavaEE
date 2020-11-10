package session;

import java.rmi.RemoteException;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Reservation;

@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType> getCarTypes(String company) throws RemoteException;
    
    public Set<Integer> getCarIds(String company,String type) throws RemoteException;
    
    public int getNumberOfReservations(String company, String type, int carId) throws RemoteException;
    
    public int getNumberOfReservations(String company, String type) throws RemoteException;
    
    public void addCompany(String companyCsv) throws RemoteException;
    
    public void removeCompany(String companyName) throws RemoteException;
      
}