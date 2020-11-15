package client;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import session.ManagerSessionRemote;
import session.ReservationSessionRemote;

public class Main extends AbstractTestManagement<ReservationSessionRemote, ManagerSessionRemote> {
    
    private static InitialContext context;
    
    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        // TODO: use updated manager interface to load cars into companies
        context = new InitialContext();
        ManagerSessionRemote managerSession = (ManagerSessionRemote) context.lookup(ManagerSessionRemote.class.getName());
        managerSession.addCompany("dockx.csv");
        managerSession.addCompany("hertz.csv");
        new Main("trips").run();
    }

    @Override
    protected ReservationSessionRemote getNewReservationSession(String name) throws Exception {
        return (ReservationSessionRemote) context.lookup(ReservationSessionRemote.class.getName());
        
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name) throws Exception {
        return (ManagerSessionRemote) context.lookup(ManagerSessionRemote.class.getName());
    }

    @Override
    protected void getAvailableCarTypes(ReservationSessionRemote session, Date start, Date end) throws Exception {
        List<CarType> available = session.getAvailableCarTypes(start, end);
        for(CarType c : available)
            System.out.println(c.getName() + " is available");
    }

    @Override
    protected void createQuote(ReservationSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        try{
            session.createQuote(name, start, end , carType, region);  
        }
        catch(Exception e)
        {
            throw e;
        }
    }

    @Override
    protected List<Reservation> confirmQuotes(ReservationSessionRemote session, String name) throws Exception {
        try{
            return session.confirmQuotes();
        }
        catch(Exception e)
        {
            throw e;
        }
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        return ms.getNumberOfReservationsOfRenter(clientName);
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        return ms.getBestClients();
    }

    @Override
    protected String getCheapestCarType(ReservationSessionRemote session, Date start, Date end, String region) throws Exception {
        return session.getCheapestCarType(start, end, region);
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        return ms.getMostPopularCarTypeIn(carRentalCompanyName, year);
    }

    @Override
    protected int getNumberOfReservationsByCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservations(carRentalName, carType);
    }
}