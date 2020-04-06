package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dtos.TicketSeedRootDto;
import softuni.exam.models.entities.Passenger;
import softuni.exam.models.entities.Plane;
import softuni.exam.models.entities.Ticket;
import softuni.exam.models.entities.Town;
import softuni.exam.repository.TicketRepository;
import softuni.exam.service.PassengerService;
import softuni.exam.service.PlaneService;
import softuni.exam.service.TicketService;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static softuni.exam.constants.GlobalConstants.TICKETS_FILE_PATH;

@Service
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;
    private final TownService townService;
    private final PlaneService planeService;
    private final PassengerService passengerService;

    public TicketServiceImpl(TicketRepository ticketRepository,
                             ModelMapper modelMapper,
                             ValidationUtil validationUtil,
                             XmlParser xmlParser, TownService townService, PlaneService planeService, PassengerService passengerService) {
        this.ticketRepository = ticketRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
        this.townService = townService;
        this.planeService = planeService;
        this.passengerService = passengerService;
    }

    @Override
    public boolean areImported() {

        return this.ticketRepository.count() > 0;
    }

    @Override
    public String readTicketsFileContent() throws IOException {

        return Files.readString(Path.of(TICKETS_FILE_PATH));
    }

    @Override
    public String importTickets() throws JAXBException, FileNotFoundException {
        StringBuilder resultInfo = new StringBuilder();
        TicketSeedRootDto ticketSeedRootDto = this.xmlParser
                .parseXml(TicketSeedRootDto.class, TICKETS_FILE_PATH);
        ticketSeedRootDto.getTickets()
                .forEach(ticketSeedDto -> {
                    if(this.validationUtil.isValid(ticketSeedDto)){
                        if(this.ticketRepository.findBySerialNumber(ticketSeedDto.getSerialNumber()) == null){

                            Ticket ticket = this.modelMapper.map(ticketSeedDto, Ticket.class);
                            Town fromTown = this.townService.getTownByName(ticketSeedDto.getFromTown().getName());
                            if(fromTown != null){
                                ticket.setFromTown(fromTown);
                            }else {
                                resultInfo.append("Invalid fromTown!");
                            }
                            Town toTown = this.townService.getTownByName(ticketSeedDto.getToTown().getName());
                            if(toTown != null){
                                ticket.setToTown(toTown);
                            }else {
                                resultInfo.append("Invalid toTown!");
                            }
                            Passenger passenger = this.passengerService.getPassengerByEmail(ticketSeedDto
                                    .getPassenger().getEmail());
                            if(passenger != null){
                                ticket.setPassenger(passenger);
                            }else {
                                resultInfo.append("Invalid passenger!");

                            }

                            Plane plane = this.planeService.getPlaneByRegisterNumber(ticketSeedDto
                                    .getPlane().getRegisterNumber());
                            if(plane != null){
                                ticket.setPlane(plane);
                            }else {
                                resultInfo.append("Invalid plane!");
                            }

                            this.ticketRepository.saveAndFlush(ticket);
                            resultInfo.append(String.format("Successfully imported Ticket %s - %s",
                                    ticket.getFromTown().getName(),
                                    ticket.getToTown().getName()));



                        }else {
                            resultInfo.append("Already in DB!");
                        }

                    }else {
                        resultInfo.append("Invalid Ticket");

                    }
                    resultInfo.append(System.lineSeparator());
                });


        return resultInfo.toString();
    }

    @Override
    public Ticket getBySerialNumber(String serialNumber) {

        return this.ticketRepository.findBySerialNumber(serialNumber);
    }
}
