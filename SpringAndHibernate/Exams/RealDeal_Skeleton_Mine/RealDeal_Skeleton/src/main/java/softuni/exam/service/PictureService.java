package softuni.exam.service;


import softuni.exam.models.entities.Picture;

import java.io.IOException;

public interface PictureService {

    boolean areImported();

    String readPicturesFromFile() throws IOException;
	
	String importPictures() throws IOException;

	Picture getPictureByName (String name);

}
