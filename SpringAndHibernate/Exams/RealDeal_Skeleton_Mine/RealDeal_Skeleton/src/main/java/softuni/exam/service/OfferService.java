package softuni.exam.service;

import softuni.exam.models.entities.Offer;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.LocalDateTime;

public interface OfferService {

    boolean areImported();

    String readOffersFileContent() throws IOException;
	
	String importOffers() throws IOException, JAXBException;

	Offer getOfferByAddedOnAndDescription (LocalDateTime addedOn, String description);
}
