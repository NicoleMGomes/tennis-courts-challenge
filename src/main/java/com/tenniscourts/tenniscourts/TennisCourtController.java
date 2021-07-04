package com.tenniscourts.tenniscourts;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tenniscourts.config.BaseRestController;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/court")
public class TennisCourtController extends BaseRestController implements TennisCourtApi {

    private final TennisCourtService tennisCourtService;

    @Override
    @PostMapping
    public ResponseEntity<Void> addTennisCourt(
        @RequestBody final CreateTennisCourtRequestDTO createTennisCourtRequestDTO) {

        return ResponseEntity
            .created(locationByEntity(tennisCourtService.addTennisCourt(createTennisCourtRequestDTO).getId())).build();
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<TennisCourtDTO> findTennisCourtById(
        @PathVariable("id") final Long tennisCourtId) {

        return ResponseEntity.ok(tennisCourtService.findTennisCourtById(tennisCourtId));
    }

    @Override
    @GetMapping("/schedules/{id}")
    public ResponseEntity<TennisCourtDTO> findTennisCourtWithSchedulesById(
        @PathVariable("id") final Long tennisCourtId) {

        return ResponseEntity.ok(tennisCourtService.findTennisCourtWithSchedulesById(tennisCourtId));
    }
}
