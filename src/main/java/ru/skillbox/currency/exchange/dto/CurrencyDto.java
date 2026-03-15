package ru.skillbox.currency.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDto {

    public interface GetAllCurrencies {
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Max(1000000)
    private Long count;

    private Long id;

    @JsonView(GetAllCurrencies.class)
    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "Nominal")
    private Long nominal;

    @JsonView(GetAllCurrencies.class)
    @XmlElement(name = "Value")
    private String value;

    @XmlElement(name = "NumCode")
    @Min(10)
    @Max(999)
    @NotNull
    private Long isoNumCode;

    @XmlElement(name = "CharCode")
    private String charCode;

    @Override
    public String toString() {
        return name + " "  + value + " " + " " + isoNumCode;
    }
}