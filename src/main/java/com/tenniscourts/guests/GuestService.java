package com.tenniscourts.guests;

import static com.tenniscourts.guests.GuestStatus.ACTIVE;
import static com.tenniscourts.guests.GuestStatus.INACTIVE;
import static org.hibernate.annotations.common.util.StringHelper.isEmpty;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tenniscourts.exceptions.EntityNotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;

    private final GuestMapper guestMapper;

    public GuestDTO addGuest(final CreateGuestRequestDTO createGuestRequestDTO) {

        return guestMapper.map(guestRepository.saveAndFlush(guestMapper.map(createGuestRequestDTO)));
    }

    public GuestDTO findGuestById(final Long guestId) {

        return guestRepository.findById(guestId).map(guestMapper::map).orElseThrow(() ->
            new EntityNotFoundException("Guest not found.")
        );
    }

    public List<GuestDTO> findGuests(final String name) {

        if (!isEmpty(name)) {
            return guestMapper.map(guestRepository.findByName(name));
        }

        return guestMapper.map(guestRepository.findAll());
    }

    public GuestDTO removeGuest(final Long guestId) {

        final GuestDTO guestDTO = findGuestById(guestId);

        validateUpdate(guestDTO);

        guestDTO.setStatus(INACTIVE);
        return guestMapper.map(guestRepository.save(guestMapper.map(guestDTO)));
    }

    public GuestDTO updateGuest(final Long guestId, final String name) {

        final GuestDTO guestDTO = findGuestById(guestId);

        validateUpdate(guestDTO);

        guestDTO.setName(name);
        return guestMapper.map(guestRepository.save(guestMapper.map(guestDTO)));
    }

    private void validateUpdate(final GuestDTO guestDTO) {

        if (!ACTIVE.equals(guestDTO.getStatus())) {
            throw new IllegalArgumentException("Cannot remove/update because it's not active.");
        }
    }

    public boolean existsActiveGuestById(final Long guestId) {

        return guestRepository.existsByIdAndStatusEquals(guestId, ACTIVE);
    }
}
