package session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;

@Stateless
@DeclareRoles({"Manager"})
@RolesAllowed("Manager")
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ManagerSession implements ManagerSessionRemote {
    
    @Resource
    private EJBContext context;
    
    @PersistenceContext
    private EntityManager em;
  
    @Override
    public Set<String> getAllRentalCompanies() throws RemoteException {
        return new HashSet<>(em.createNamedQuery("getAllRentalCompanyNames").getResultList());
    }
    
    @Override
    public Set<CarType> getCarTypes(String company) throws RemoteException {
        return new HashSet<>(em.createNamedQuery("getAllCarTypesInCompany")
               .setParameter("companyName", company)
               .getResultList());
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) throws RemoteException {
        return new HashSet<>(em.createNamedQuery("getAllIdsForTypeInCompany")
                .setParameter("companyName", company)
                .setParameter("type", type)
                .getResultList());
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) throws RemoteException {
        return em.createNamedQuery("getNumberOfReservationsForCarAndIDInCompany")
                .setParameter("companyName", company)
                .setParameter("name", type)
                .setParameter("id", id)
                .getResultList()
                .size();   
    }

    @Override
    public int getNumberOfReservations(String company, String type) throws RemoteException {
        return em.createNamedQuery("getNumberOfReservationsForCarInCompany")
                .setParameter("companyName", company)
                .setParameter("name", type)
                .getResultList()
                .size();
        
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addCompany(String companyCsv) throws RemoteException {
        try{
           loadRental(companyCsv); //persist can result in exception if company was already added
        }
        catch(Exception e)
        {
            context.setRollbackOnly();
            throw new RemoteException("Failed to add company: " + e.getMessage());
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeCompany(String companyName) throws RemoteException {
        CarRentalCompany crc = em.find(CarRentalCompany.class, companyName);
        if(crc == null) throw new RemoteException("Company not found in db");
        em.remove(crc); 
    }
    
    @Override
    public int getNumberOfReservationsOfRenter(String renter) throws RemoteException {
        return em.createNamedQuery("getReservationsByRenter")
                .setParameter("renter", renter)
                .getResultList()
                .size();
    }

    @Override
    public Set<String> getBestClients() throws RemoteException {
        Long best = (Long)em.createNamedQuery("getBestClientResCount").getResultList().get(0);
        return new HashSet<>(
                em.createNamedQuery("getClientsWithReservations")
                        .setParameter("resCount", best)
                        .getResultList());
    }
    
    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws RemoteException {
        
        List<CarType> carTypes = (List<CarType>) em.createNamedQuery("getMostPopularCarTypeInCompanyInYear")
                .setParameter("company", carRentalCompanyName)
                .setParameter("year", year)
                .getResultList();

        if(carTypes == null) throw new RemoteException("No cars were rented that year");
        return carTypes.get(0);
    }
    
    private void loadRental(String datafile) throws Exception {
        CrcData data = loadData(datafile);
        CarRentalCompany company = new CarRentalCompany(data.name, data.regions, data.cars);
        em.persist(company);
        Logger.getLogger(ManagerSession.class.getName()).log(Level.INFO, "Loaded {0} from file {1}", new Object[]{data.name, datafile});
    }
    
    private static CrcData loadData(String datafile)
            throws NumberFormatException, IOException {

        CrcData out = new CrcData();
        StringTokenizer csvReader;
        
        //open file from jar
        BufferedReader in = new BufferedReader(new InputStreamReader(ManagerSession.class.getClassLoader().getResourceAsStream(datafile)));
        
        try {
            while (in.ready()) {
                String line = in.readLine();
                
                if (line.startsWith("#")) {
                    // comment -> skip					
                } else if (line.startsWith("-")) {
                    csvReader = new StringTokenizer(line.substring(1), ",");
                    out.name = csvReader.nextToken();
                    out.regions = Arrays.asList(csvReader.nextToken().split(":"));
                } else {
                    csvReader = new StringTokenizer(line, ",");
                    //create new car type from first 5 fields
                    CarType type = new CarType(csvReader.nextToken(),
                            Integer.parseInt(csvReader.nextToken()),
                            Float.parseFloat(csvReader.nextToken()),
                            Double.parseDouble(csvReader.nextToken()),
                            Boolean.parseBoolean(csvReader.nextToken()));
                    //create N new cars with given type, where N is the 5th field
                    for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                        out.cars.add(new Car(type));
                    }        
                }
            } 
        } finally {
            in.close();
        }

        return out;
    }
    
    static class CrcData {
            public List<Car> cars = new LinkedList<Car>();
            public String name;
            public List<String> regions =  new LinkedList<String>();
    }
}