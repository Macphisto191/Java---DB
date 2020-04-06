package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dtos.PassengerSeedDto;
import softuni.exam.models.entities.Passenger;
import softuni.exam.models.entities.Town;
import softuni.exam.repository.PassengerRepository;
import softuni.exam.service.PassengerService;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtil;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static softuni.exam.constants.GlobalConstants.PASSENGERS_FILE_PATH;

@Service
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final TownService townService;

    @Autowired
    public PassengerServiceImpl(PassengerRepository passengerRepository,
                                ModelMapper modelMapper, Gson gson,
                                ValidationUtil validationUtil, TownService townService) {
        this.passengerRepository = passengerRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.townService = townService;
    }


    @Override
    public boolean areImported() {

        return this.passengerRepository.count() > 0;
    }

    @Override
    public String readPassengersFileContent() throws IOException {
        return Files.readString(Paths.get(PASSENGERS_FILE_PATH));
    }

    @Override
    public String importPassengers() throws IOException {
        StringBuilder resultInfo = new StringBuilder();

        PassengerSeedDto[] passengerSeedDtos = this.gson
                .fromJson(new FileReader(PASSENGERS_FILE_PATH), PassengerSeedDto[].class);

        Arrays.stream(passengerSeedDtos)
                .forEach(passengerSeedDto -> {
                    if(this.validationUtil.isValid(passengerSeedDto)){
                        if (this.passengerRepository.findByEmail(passengerSeedDto.getEmail()) == null){
                            Passenger passenger  = this.modelMapper
                                    .map(passengerSeedDto, Passenger.class);
                            Town town = this.townService.getTownByName(passengerSeedDto.getTown());
                            if (town != null){
                                passenger.setTown(town);
                                this.passengerRepository.saveAndFlush(passenger);
                                resultInfo.append(String.format("Successfully imported Passenger %s %s - %s",
                                        passengerSeedDto.getFirstName(), passengerSeedDto.getLastName(),
                                        passengerSeedDto.getEmail()));
                            }else {
                                resultInfo.append("Town not found!");
                            }



                        }else {
                            resultInfo.append("Already in DB!");
                        }

                    }else {
                        resultInfo.append("Invalid Passenger");
                    }
                    resultInfo.append(System.lineSeparator());
                });

        return resultInfo.toString();
    }

    @Override
    public String getPassengersOrderByTicketsCountDescendingThenByEmail() {

        StringBuilder resultInfo = new StringBuilder();
        List<Passenger> passengers = this.passengerRepository.findAllSorted();

        for (Passenger passenger : passengers) {
            resultInfo.append(String.format("Passenger %s  %s\n" +
                    "\tEmail - %s\n" +
                    "\tPhone - %s\n" +
                    "\tNumber of tickets - %d", passenger.getFirstName(),
                    passenger.getLastName(), passenger.getEmail(),
                    passenger.getPhoneNumber(), passenger.getTickets().size()))
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());

        }


        return resultInfo.toString();
    }

    @Override
    public Passenger getPassengerByEmail(String email) {
        return this.passengerRepository.findByEmail(email);
    }
}
