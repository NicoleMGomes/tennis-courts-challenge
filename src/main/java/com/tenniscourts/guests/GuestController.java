package com.tenniscourts.guests;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tenniscourts.config.BaseRestController;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/guest")
public class GuestController extends BaseRestController implements GuestApi {

    private final GuestService guestService;

    @Override
    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<Void> addGuest(
        @RequestBody final CreateGuestRequestDTO createGuestRequestDTO) {

        return ResponseEntity.created(locationByEntity(guestService.addGuest(createGuestRequestDTO).getId())).build();
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<GuestDTO> findGuestById(
        @PathVariable("id") final Long guestId) {

        return ResponseEntity.ok(guestService.findGuestById(guestId));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<GuestDTO>> findGuests(
        @RequestParam(required = false) final String name) {

        return ResponseEntity.ok(guestService.findGuests(name));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<GuestDTO> removeGuest(
        @PathVariable("id") final Long guestId) {

        return ResponseEntity.ok(guestService.removeGuest(guestId));
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<GuestDTO> updateGuest(
        @PathVariable("id") final Long guestId,
        @RequestParam final String name) {

        return ResponseEntity.ok(guestService.updateGuest(guestId, name));
    }
}
