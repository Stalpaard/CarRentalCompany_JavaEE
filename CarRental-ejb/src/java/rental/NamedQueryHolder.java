package rental;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


@NamedQueries({
    /**
     * Company related queries
     */
    @NamedQuery(
            name = "getAllRentalCompanies",
            query = "SELECT crc FROM CarRentalCompany crc"),
    @NamedQuery(
            name = "getAllRentalCompanyNames",
            query = "SELECT crc.name FROM CarRentalCompany crc"),
    /**
     * Car related queries
     */
    @NamedQuery(
            name = "getAllCarTypesInCompany",
            query = "SELECT c.type FROM Car c, CarRentalCompany crc "
                    + "WHERE crc.name = :companyName AND c MEMBER OF crc.cars"),
    @NamedQuery(
            name = "getAllIdsForTypeInCompany",
            query = "SELECT c.id FROM Car c, CarRentalCompany crc "
                    + "WHERE crc.name = :companyName AND c.type.name = :type AND c MEMBER OF crc.cars"),
    @NamedQuery(
            name = "getAvailableCarTypesInPeriod",
            query = "SELECT c.type FROM Car c WHERE (SELECT COUNT(res) FROM Reservation res "
                    + "WHERE res.carId = c.id AND res.startDate >= :start AND res.endDate <= :end) <= 0"),
    @NamedQuery(
            name = "getCheapestCarTypeInPeriodAndRegion",
            query = "SELECT c.type FROM CarRentalCompany crc JOIN crc.regions r JOIN Car c JOIN CarType t WHERE "
                    + "c.type.rentalPricePerDay = (SELECT MIN(c.type.rentalPricePerDay) FROM CarRentalCompany crc JOIN Car c JOIN CarType t JOIN crc.regions r WHERE "
                    + "r = :region "
                    + "AND (SELECT COUNT(res) FROM Reservation res WHERE res.carId = c.id AND res.startDate >= :start AND res.endDate <= :end) <= 0)"),
    @NamedQuery(
            name = "getMostPopularCarTypeInCompanyInYear",
            query = "SELECT c.type FROM Car c, Reservation res "
                    + "WHERE res.rentalCompany = :company AND EXTRACT(YEAR FROM res.startDate) = :year AND c.id = res.carId GROUP BY c.type "
                    + "ORDER BY COUNT(res) DESC"),
    /**
     * Reservation related queries
     */
    @NamedQuery(
            name = "getNumberOfReservationsForCarAndIDInCompany",
            query = "SELECT res FROM Car c, CarRentalCompany crc, Reservation res "
                    + "WHERE crc.name = :companyName AND c.type.name = :name AND c.id = :id AND res MEMBER OF c.reservations"),
    @NamedQuery(
            name = "getNumberOfReservationsForCarInCompany",
            query = "SELECT res FROM Car c, CarRentalCompany crc, Reservation res "
                    + "WHERE crc.name = :companyName AND c.type.name = :name AND res MEMBER OF c.reservations"),
    @NamedQuery(
            name = "getReservationsByRenter",
            query = "SELECT res FROM Reservation res "
                    + "WHERE res.carRenter = :renter"),
    @NamedQuery(
            name = "getBestClientResCount",
            query = "SELECT COUNT(res.carRenter) FROM Reservation res "
                    + "GROUP BY res.carRenter ORDER BY COUNT(res.carRenter) DESC"),
    @NamedQuery(
            name = "getClientsWithReservations",
            query = "SELECT res.carRenter FROM Reservation res "
                    + "GROUP BY res.carRenter HAVING COUNT(res) = :resCount")
    })

@Entity
public class NamedQueryHolder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
