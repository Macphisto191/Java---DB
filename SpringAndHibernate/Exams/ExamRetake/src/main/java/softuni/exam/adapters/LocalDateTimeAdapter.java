package softuni.exam.adapters;

import softuni.exam.constants.GlobalConstants;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
    @Override
    public LocalDateTime unmarshal(String s) throws Exception {
        return LocalDateTime.parse(s, DateTimeFormatter
                .ofPattern(GlobalConstants.LOCAL_DATE_TIME_FORMAT));
    }

    @Override
    public String marshal(LocalDateTime localDateTime) throws Exception {
        return localDateTime.toString();
    }
}
