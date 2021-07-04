package com.tenniscourts.guests;

import static com.tenniscourts.guests.GuestStatus.ACTIVE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.tenniscourts.config.persistence.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Guest extends BaseEntity<Long> {

    @Column
    @NotNull
    private String name;

    @NotNull
    private GuestStatus status;

    @PrePersist
    public void prePersist() {
        setStatus(ACTIVE);
    }
}
