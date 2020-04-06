package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entities.Car;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Car findByMakeAndModelAndKilometers(String make, String model,
    int kilometers);

    Car findById(long id);


    @Query("SELECT c FROM Car as c LEFT JOIN Picture as p on c.id = p.car.id GROUP BY c.id " +
            "ORDER BY COUNT(p.id) DESC, c.make")
    List<Car> findAllSortedByPicturesCountAndMake();

}
