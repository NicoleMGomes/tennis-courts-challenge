package com.tenniscourts.config;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.net.URI;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
public class BaseRestController {

    protected URI locationByEntity(Long entityId) {

        return fromCurrentRequest().path("/{id}").buildAndExpand(entityId).toUri();
    }
}
