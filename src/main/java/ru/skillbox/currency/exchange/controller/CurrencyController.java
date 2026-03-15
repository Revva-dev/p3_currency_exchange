package ru.skillbox.currency.exchange.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.service.CurrencyService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/currency")
public class CurrencyController {

    private final CurrencyService service;

    @GetMapping(value = "/{id}")
    ResponseEntity<CurrencyDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getById(id));
    }


    @PostMapping(value = "/convert")
    ResponseEntity<Double> convertValue(@Valid @RequestBody CurrencyDto currencyDto) {
        Long count = currencyDto.getCount();
        Long numCode = currencyDto.getIsoNumCode();
        return ResponseEntity.ok(service.convertValue(count, numCode));
    }

    @JsonView(CurrencyDto.GetAllCurrencies.class)
    @GetMapping(value = "/all")
    ResponseEntity<Map<String, Collection<CurrencyDto>>> getAll() {
        return ResponseEntity.ok(service.getALL());
    }
}



