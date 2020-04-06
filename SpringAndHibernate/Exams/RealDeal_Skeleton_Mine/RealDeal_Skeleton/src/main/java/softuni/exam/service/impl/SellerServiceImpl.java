package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.constants.GlobalConstants;
import softuni.exam.models.dtos.SellerSeedRootDto;
import softuni.exam.models.entities.Seller;
import softuni.exam.repository.SellerRepository;
import softuni.exam.service.SellerService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static softuni.exam.constants.GlobalConstants.SELLERS_FILE_PATH;

@Service
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    public SellerServiceImpl(SellerRepository sellerRepository,
                             XmlParser xmlParser,
                             ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.sellerRepository = sellerRepository;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        return this.sellerRepository.count() > 0;
    }

    @Override
    public String readSellersFromFile() throws IOException {
        return Files.readString(Path.of(SELLERS_FILE_PATH));
    }

    @Override
    public String importSellers() throws IOException, JAXBException {
        StringBuilder result = new StringBuilder();
        SellerSeedRootDto sellerSeedRootDto  = this.xmlParser
                .parseXml(SellerSeedRootDto.class, SELLERS_FILE_PATH);

        sellerSeedRootDto.getSellerSeedDtos()
                .forEach(sellerSeedDto -> {
                    if (this.validationUtil.isValid(sellerSeedDto)){
                        if(this.sellerRepository.findByEmail(sellerSeedDto.getEmail())== null){

                            Seller seller = this.modelMapper
                                    .map(sellerSeedDto, Seller.class);


                            this.sellerRepository.saveAndFlush(seller);
                            result.append(String.format("Successfully import seller %s - %s",
                                    sellerSeedDto.getLastName(), sellerSeedDto.getEmail()));

                        }else{
                            result.append("Already added to DB");
                        }

                    }else {
                        result.append("Invalid seller");
                    }
                    result.append(System.lineSeparator());

                });


        return result.toString();
    }

    @Override
    public Seller getSellerByEmail(String email) {

        return this.sellerRepository.findByEmail(email);
    }

    @Override
    public Seller getSellerById(long id) {
        return this.sellerRepository.findById(id);
    }
}
