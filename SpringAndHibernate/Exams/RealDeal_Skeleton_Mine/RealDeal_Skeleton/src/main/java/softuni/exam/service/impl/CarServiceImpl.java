package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.constants.GlobalConstants;
import softuni.exam.models.dtos.CarSeedDto;
import softuni.exam.models.entities.Car;
import softuni.exam.repository.CarRepository;
import softuni.exam.service.CarService;
import softuni.exam.util.ValidationUtil;

import javax.transaction.Transactional;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static softuni.exam.constants.GlobalConstants.CARS_FILE_PATH;

@Service
@Transactional
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, Gson gson, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.carRepository = carRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {

        return this.carRepository.count() > 0;
    }

    @Override
    public String readCarsFileContent() throws IOException {
        return Files.readString(Path.of(CARS_FILE_PATH));
    }

    @Override
    public String importCars() throws IOException {

        StringBuilder result = new StringBuilder();
        CarSeedDto [] carSeedDtos = this.gson
                .fromJson(new FileReader(CARS_FILE_PATH), CarSeedDto[].class);

        Arrays.stream(carSeedDtos)
                .forEach(carSeedDto -> {
                    if (this.validationUtil.isValid(carSeedDto)){
                        if (this.carRepository.findByMakeAndModelAndKilometers(carSeedDto.getMake(),
                                carSeedDto.getModel(), carSeedDto.getKilometers()) == null){
                            Car car = this.modelMapper
                                    .map(carSeedDto, Car.class);
                            LocalDate localDate = LocalDate.parse(carSeedDto.getRegisteredOn()
                                    , DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            car.setRegisteredOn(localDate);
                            this.carRepository.saveAndFlush(car);

                            result.append(String.format("Successfully imported car - %s - %s",
                                    carSeedDto.getMake(), carSeedDto.getModel()));

                        }else {
                            result.append("Car already added in DB");
                        }

                    }else {
                        result.append("Invalid car");

                    }
                    result.append(System.lineSeparator());
                });


        return result.toString();
    }

    @Override
    public String getCarsOrderByPicturesCountThenByMake() {

        StringBuilder carInfo = new StringBuilder();

        this.carRepository.findAllSortedByPicturesCountAndMake()
                .forEach(car -> {
                    carInfo.append(String.format("Car make - %s, model - %s\n" +
                                    "\tKilometers - %d\n" +
                                    "\tRegistered on - %s\n" +
                                    "\tNumber of pictures - %d\n",car.getMake(),
                            car.getModel(), car.getKilometers(), car.getRegisteredOn(),
                            car.getPictures().size())).append(System.lineSeparator());

                    System.out.println();
                });
        return carInfo.toString();
    }

    @Override
    public Car getCarByMakeModelAndKilometers(String make, String model, int kilometers) {

        return this.carRepository.findByMakeAndModelAndKilometers(make, model, kilometers);
    }

    @Override
    public Car getCarById(long id) {
        return this.carRepository.findById(id);
    }

//    @Override
//    public String getAllCarsOrderedByPicturesDescAndMake() {
//        StringBuilder carInfo = new StringBuilder();
//
//        this.carRepository.findAllSortedByPicturesCountAndMake()
//                .forEach(car -> {
//                    carInfo.append(String.format("Car make - %s, model - %s\n" +
//                            "\tKilometers - %d\n" +
//                            "\tRegistered on - %s\n" +
//                            "\tNumber of pictures - %d\n",car.getMake(),
//                            car.getModel(), car.getKilometers(), car.getRegisteredOn(),
//                            car.getPictures().size())).append(System.lineSeparator());
//
//                    System.out.println();
//                });
//        return carInfo.toString();
//    }
}
