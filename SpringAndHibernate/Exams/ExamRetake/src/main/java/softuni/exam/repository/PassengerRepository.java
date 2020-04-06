package softuni.exam.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entities.Passenger;

import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {

    Passenger findByEmail(String email);

//    @Query("select p FROM Passenger p join Ticket t on p.id = t.passenger.id " +
//            "group by t.passenger.id " +
//            "order by count(t.passenger.id) desc, p.email")
//    List<Passenger> findAllByTicketsCountDescThenByEmail ();

    List<Passenger> findAllByOrderByTicketsDescEmail ();

    @Query("select p FROM Passenger p join Ticket as t on p.id = t.passenger.id " +
            " group by t.passenger.id " +
            "order by count(p.tickets.size)DESC, p.email")
    List<Passenger> findAllSorted ();

}
