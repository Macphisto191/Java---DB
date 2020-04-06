package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.constants.GlobalConstants;
import softuni.exam.models.dtos.PictureSeedDto;
import softuni.exam.models.entities.Car;
import softuni.exam.models.entities.Picture;
import softuni.exam.repository.PictureRepository;
import softuni.exam.service.CarService;
import softuni.exam.service.PictureService;
import softuni.exam.util.ValidationUtil;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static softuni.exam.constants.GlobalConstants.PICTURES_FILE_PATH;

@Service
public class PictureServiceImpl implements PictureService {
    private final PictureRepository pictureRepository;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final CarService carService;

    public PictureServiceImpl(PictureRepository pictureRepository,
                              ModelMapper modelMapper,
                              Gson gson, ValidationUtil validationUtil, CarService carService) {
        this.pictureRepository = pictureRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.carService = carService;
    }

    @Override
    public boolean areImported() {
        return this.pictureRepository.count() > 0;
    }

    @Override
    public String readPicturesFromFile() throws IOException {
        return Files.readString(Path.of(PICTURES_FILE_PATH));
    }

    @Override
    public String importPictures() throws IOException {
        StringBuilder result = new StringBuilder();

        PictureSeedDto[] pictureSeedDtos = this.gson
                .fromJson(new FileReader(PICTURES_FILE_PATH), PictureSeedDto[].class);

        Arrays.stream(pictureSeedDtos)
                .forEach(pictureSeedDto -> {
                    if (this.validationUtil.isValid(pictureSeedDto)){
                        if (this.pictureRepository.findByName(pictureSeedDto.getName()) == null){

                            Picture picture = this.modelMapper
                                    .map(pictureSeedDto, Picture.class);
                            LocalDateTime localDateTime = LocalDateTime.parse(pictureSeedDto.getDateAndTime(),
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                            picture.setDateAndTime(localDateTime);

                            Car car = this.carService.getCarById((long)pictureSeedDto.getCar());
//                            if (car != null) {
                                picture.setCar(car);

                                this.pictureRepository.saveAndFlush(picture);
//                            }else {
//                                result.append("Car not found by id");
//                            }


                            result.append(String.format("Successfully import picture %s",
                                    pictureSeedDto.getName()));

                        }else {
                            result.append("Picture already added to DB");
                        }


                    }else {
                        result.append("Invalid picture");
                    }
                    result.append(System.lineSeparator());
                });





        return result.toString();
    }

    @Override
    public Picture getPictureByName(String name) {
        return this.pictureRepository.findByName(name);
    }
}
