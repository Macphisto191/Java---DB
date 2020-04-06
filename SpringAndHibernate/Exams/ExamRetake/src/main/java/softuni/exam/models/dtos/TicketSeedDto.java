package softuni.exam.models.dtos;

import org.hibernate.validator.constraints.Length;
import softuni.exam.adapters.LocalDateTimeAdapter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@XmlRootElement(name = "ticket")
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketSeedDto {

    @XmlElement(name = "serial-number")
    private String serialNumber;
    @XmlElement
    private BigDecimal price;
    @XmlElement(name = "take-off")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime takeOff;
    @XmlElement(name = "from-town")
    private TicketSeedFromTownDto fromTown;
    @XmlElement(name = "to-town")
    private TicketSeedToTownDto toTown;
    @XmlElement
    private TicketSeedPassengerDto passenger;
    @XmlElement
    private TicketSeedPlaneDto plane;

    public TicketSeedDto() {
    }

    @NotNull
    @Length(min = 2)
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }


    @NotNull
    @DecimalMin(value = "0.01", message = "Price cannot be negative!")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @NotNull
    public LocalDateTime getTakeOff() {
        return takeOff;
    }

    public void setTakeOff(LocalDateTime takeOff) {
        this.takeOff = takeOff;
    }

    @NotNull
    public TicketSeedFromTownDto getFromTown() {
        return fromTown;
    }

    public void setFromTown(TicketSeedFromTownDto fromTown) {
        this.fromTown = fromTown;
    }

    @NotNull
    public TicketSeedToTownDto getToTown() {
        return toTown;
    }

    public void setToTown(TicketSeedToTownDto toTown) {
        this.toTown = toTown;
    }

    @NotNull
    public TicketSeedPassengerDto getPassenger() {
        return passenger;
    }

    public void setPassenger(TicketSeedPassengerDto passenger) {
        this.passenger = passenger;
    }

    @NotNull
    public TicketSeedPlaneDto getPlane() {
        return plane;
    }

    public void setPlane(TicketSeedPlaneDto plane) {
        this.plane = plane;
    }
}
