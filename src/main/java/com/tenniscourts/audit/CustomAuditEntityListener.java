package com.tenniscourts.audit;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.tenniscourts.config.persistence.BaseEntity;

public class CustomAuditEntityListener {

    public final static Long USER_SYSTEM_ID = 1L;

    @PreUpdate
    public void preUpdate(final BaseEntity baseEntity) throws UnknownHostException {

        baseEntity.setDateUpdate(now());

        if (isNull(baseEntity.getUserUpdate())) {
            baseEntity.setUserUpdate(USER_SYSTEM_ID);
        }

        if (isNull(baseEntity.getIpNumberUpdate())) {
            baseEntity.setIpNumberUpdate(InetAddress.getLocalHost().getHostAddress());
        }
    }

    @PrePersist
    public void prePersist(final BaseEntity baseEntity) throws UnknownHostException {

        baseEntity.setDateUpdate(now());

        if (isNull(baseEntity.getUserUpdate())) {
            baseEntity.setUserUpdate(USER_SYSTEM_ID);
        }

        baseEntity.setDateCreate(now());

        if (isNull(baseEntity.getUserCreate())) {
            baseEntity.setUserCreate(USER_SYSTEM_ID);
        }

        if (isNull(baseEntity.getIpNumberCreate())) {
            baseEntity.setIpNumberUpdate(InetAddress.getLocalHost().getHostAddress());
        }

        if (isNull(baseEntity.getIpNumberCreate())) {
            baseEntity.setIpNumberCreate(InetAddress.getLocalHost().getHostAddress());
        }
    }
}