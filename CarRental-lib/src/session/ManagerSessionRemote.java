package session;

import java.rmi.RemoteException;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;

@Remote
public interface ManagerSessionRemote {
    
    /**
     * Get a set containing all rental company names
     * @return a set of strings
     * @throws RemoteException 
     */
    public Set<String> getAllRentalCompanies() throws RemoteException;
    
    /**
     * Get a set of all car types provided by a specified car rental company
     * @param company name of the company
     * @return a set of CarType objects
     * @throws RemoteException 
     */
    public Set<CarType> getCarTypes(String company) throws RemoteException;
    
    /**
     * Get a set of the IDs of all cars of a particular type in a company
     * @param company name of the company
     * @param type name of the car type
     * @return a set of Integer objects
     * @throws RemoteException 
     */
    public Set<Integer> getCarIds(String company,String type) throws RemoteException;
    
    /**
     * Get the number of reservations for a particular car in a car rental company
     * @param company name of the company
     * @param type name of the car type
     * @param carId id of the car
     * @return int
     * @throws RemoteException 
     */
    public int getNumberOfReservations(String company, String type, int carId) throws RemoteException;
    
    /**
     * Get the number of reservations for a particular car type in a car rental company
     * @param company name of the company
     * @param type name of the car type
     * @return int
     * @throws RemoteException 
     */
    public int getNumberOfReservations(String company, String type) throws RemoteException;
    
    /**
     * Add a new company to the car rental agency using a .csv file
     * @param companyCsv path to .csv file
     * @throws RemoteException 
     */
    public void addCompany(String companyCsv) throws RemoteException;
    
    /**
     * Remove a company from the car rental agency
     * @param companyName name of the company to be removed
     * @throws RemoteException 
     */
    public void removeCompany(String companyName) throws RemoteException;
    
    /**
     * Get the number of reservations made by a specific renter
     * @param renter name of the renter
     * @return int
     * @throws RemoteException 
     */
    public int getNumberOfReservationsOfRenter(String renter) throws RemoteException;
    
    /**
     * Get a set of all the best clients of the agency
     * @return a set of String
     * @throws RemoteException 
     */
    public Set<String> getBestClients() throws RemoteException;
    
    /**
     * Get the most popular car type of a car rental company for a given calendar year
     * @param carRentalCompanyName name of the company
     * @param year calendar year
     * @return CarType object
     * @throws RemoteException 
     */
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws RemoteException;
}