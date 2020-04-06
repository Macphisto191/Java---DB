package softuni.exam.util;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

public interface XmlParser {

    <T> T parseXml(Class<T> objectClass, String filePath) throws JAXBException, FileNotFoundException;

    <T> void writeToFile(String filePath, T rootDto) throws JAXBException;
}
