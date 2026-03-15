package ru.skillbox.currency.exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "ValCurs")
@XmlAccessorType(XmlAccessType.FIELD)
public class Currencies {

    @XmlAttribute(name = "Date")
    public String date;

    @XmlAttribute(name = "name")
    public String name;

    @XmlElement(name = "Valute")
    private List<CurrencyDto> currencies;





}
