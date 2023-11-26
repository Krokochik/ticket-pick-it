package krokochik.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
class AddressGenerator {
    Queue<String> randomAddresses(int amount) {
        RestTemplate restTemplate = new RestTemplate();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
                        "https://www.doogal.co.uk/CreateRandomAddress/?startWith=");

        log.info("Addresses' downloading started from https://doogal.co.uk");

        Queue<String> addresses = new LinkedList<>();
        for (int i = 0; i < amount; i++) {
            HttpEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    null,
                    String.class);
            String[] responseBody = response.getBody().split(",");
            responseBody = Arrays.copyOfRange(responseBody,
                    0, responseBody.length - 2);
            addresses.add(String.join(",", responseBody));
        }
        log.info("Got addresses in amount of " + addresses.size());
        return addresses;
    }

}
