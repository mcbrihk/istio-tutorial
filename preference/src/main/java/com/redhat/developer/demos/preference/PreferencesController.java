package com.redhat.developer.demos.preference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PreferencesController {

    private static final String RESPONSE_STRING_FORMAT = "preference => %s\n";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RestTemplate restTemplate;

    @Value("${recommendations.api.url:http://recommendations:8080}")
    private String remoteURL;

    public PreferencesController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping("/")
    public ResponseEntity<?> getPreferences(HttpServletRequest request) {
        try {
            String response = restTemplate.getForObject(remoteURL, String.class);
            return ResponseEntity.ok(String.format(RESPONSE_STRING_FORMAT, response.trim()));
        } catch (HttpServerErrorException ex) {
            logger.warn("Exception trying to get the response from recommendation service.", ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(String.format(RESPONSE_STRING_FORMAT, ex.getResponseBodyAsString().trim()));
        } catch (RestClientException ex) {
            logger.warn("Exception trying to get the response from recommendation service.", ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(String.format(RESPONSE_STRING_FORMAT, ex.getCause()));
        }
    }

}