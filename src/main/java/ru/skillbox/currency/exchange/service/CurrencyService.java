package ru.skillbox.currency.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.skillbox.currency.exchange.dto.Currencies;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.mapper.CurrencyMapper;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyMapper mapper;
    private final CurrencyRepository repository;

    @Value("${app.cbr-uri}")
    private String cbrUri;

    public CurrencyDto getById(Long id) {
        log.info("CurrencyService method getById executed");
        Currency currency = repository.findById(id).orElseThrow(() ->
                new RuntimeException("Currency not found with id: " + id));
        return mapper.mapToDto(currency);
    }

    public CurrencyDto getByCharCode(String charCode) {
        log.info("CurrencyService method getByCharCode executed");
        Currency currency = repository.findByCharCode(charCode);
        return mapper.mapToDto(currency);
    }

    public Map<String, Collection<CurrencyDto>> getALL() {
        log.info("CurrencyService method getALL executed");

        Map<String, Collection<CurrencyDto>> map = new HashMap<>();
        map.put("currencies", repository.findAll()
                .stream()
                .map(mapper::mapToDto)
                .toList());

        return map;
    }

    public Double convertValue(Long count, Long numCode) {
        log.info("CurrencyService method convertValue executed");
        Currency currency = repository.findByIsoNumCode(numCode);
        return count * currency.getValue();
    }

    @PostConstruct
    @Scheduled(fixedDelayString = "${app.task.schedule.delay}")
    public void cbrSearch() throws JAXBException {

        RestTemplate restTemplate = new RestTemplate();
        String xmlData = restTemplate.getForObject(cbrUri, String.class);

        Path outputPath = Path.of("./src/main/resources/" + "cbrCurrency.xml");
        try (InputStream in = new URL(cbrUri).openStream()) {
            Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JAXBContext context = JAXBContext.newInstance(Currencies.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Currencies currencies = (Currencies) unmarshaller.unmarshal(new StringReader(xmlData));

        if (currencies != null) {
            for (CurrencyDto currencyDto : currencies.getCurrencies()) {

                Currency currency;
                if (repository.findByCharCode(currencyDto.getCharCode()) != null) {
                    currency = repository.findByCharCode(currencyDto.getCharCode());
                    currency.setNominal(currencyDto.getNominal());
                    currency.setValue(Double.valueOf(currencyDto.getValue().replace(',', '.')));
                } else {
                    currency = new Currency();
                    currency.setIsoNumCode(currencyDto.getIsoNumCode());
                    currency.setCharCode(currencyDto.getCharCode());
                    currency.setNominal(currencyDto.getNominal());
                    currency.setName(currencyDto.getName());
                    currency.setValue(Double.valueOf(currencyDto.getValue().replace(',', '.')));
                }
                repository.save(currency);
            }
        }
    }

}