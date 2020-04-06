
package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dtos.OfferSeedRootDto;
import softuni.exam.models.entities.Car;
import softuni.exam.models.entities.Offer;
import softuni.exam.models.entities.Seller;
import softuni.exam.repository.OfferRepository;
import softuni.exam.service.CarService;
import softuni.exam.service.OfferService;
import softuni.exam.service.SellerService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static softuni.exam.constants.GlobalConstants.OFFERS_FILE_PATH;

@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final CarService carService;
    private final SellerService sellerService;

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository,
                            ValidationUtil validationUtil,
                            ModelMapper modelMapper,
                            XmlParser xmlParser,
                            CarService carService, SellerService sellerService) {
        this.offerRepository = offerRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.carService = carService;
        this.sellerService = sellerService;
    }


    @Override
    public boolean areImported() {

        return this.offerRepository.count() > 0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        return Files.readString(Path.of(OFFERS_FILE_PATH));
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
        StringBuilder result = new StringBuilder();

        OfferSeedRootDto offerSeedRootDto = this.xmlParser
                .parseXml(OfferSeedRootDto.class, OFFERS_FILE_PATH);
        offerSeedRootDto.getOfferSeedDtos()
                .forEach(offerSeedDto -> {
                    if (this.validationUtil.isValid(offerSeedDto)){
                        if (this.offerRepository.findByAddedOnAndDescription(offerSeedDto.getAddedOn()
                        , offerSeedDto.getDescription()) == null){
                            Offer offer = this.modelMapper
                                    .map(offerSeedDto, Offer.class);

//                            LocalDateTime localDateTime = LocalDateTime.parse(offerSeedDto.getDateAndTime(),
//                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//                            offer.setAddedOn(localDateTime);

                            Car car = this.carService.getCarById(offerSeedDto.getCar().getId());
                            offer.setCar(car);

                            Seller seller = this.sellerService.getSellerById(offerSeedDto.getSeller().getId());

                            offer.setSeller(seller);


                            this.offerRepository.saveAndFlush(offer);
                            result.append(String
                                    .format("Successfully import offer %s - %s", offerSeedDto.getAddedOn(),
                                            offerSeedDto.isHasGoldStatus()));

                        }else{
                            result.append("Already Added to DB!");
                        }

                    }else {
                        result.append("Invalid offer");
                    }
                    result.append(System.lineSeparator());
                });




        return result.toString();
    }

    @Override
    public Offer getOfferByAddedOnAndDescription(LocalDateTime addedOn, String description) {
        return this.offerRepository.findByAddedOnAndDescription(addedOn, description);
    }
}
