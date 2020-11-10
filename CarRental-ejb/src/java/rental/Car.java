package rental;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Car {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private CarType type;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reservation> reservations;

    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Car(int uid, CarType type) {
    	this.id = uid;
        this.type = type;
        this.reservations = new HashSet<Reservation>();
    }

    /******
     * ID *
     ******/
    
    public int getId() {
    	return id;
    }
    
    /************
     * CAR TYPE *
     ************/
    
    public CarType getType() {
        return type;
    }
	
	public void setType(CarType type) {
		this.type = type;
	}
    /****************
     * RESERVATIONS *
     ****************/

    public boolean isAvailable(Date start, Date end) {
        if(!start.before(end))
            throw new IllegalArgumentException("Illegal given period");

        for(Reservation reservation : reservations) {
            if(reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }
    
    public void addReservation(Reservation res) {
        reservations.add(res);
    }
    
    public void removeReservation(Reservation reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }
}