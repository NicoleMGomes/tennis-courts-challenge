package com.tenniscourts.audit;

import static com.tenniscourts.audit.CustomAuditEntityListener.USER_SYSTEM_ID;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import com.tenniscourts.config.persistence.BaseEntity;

public class CustomAuditEntityListenerTest {

    private CustomAuditEntityListener listener = new CustomAuditEntityListener();

    @Test
    public void preUpdate_withoutValues() throws UnknownHostException {

        final BaseEntity baseEntity = new BaseEntity();

        listener.preUpdate(baseEntity);

        assertNotNull(baseEntity.getDateUpdate());
        assertEquals(USER_SYSTEM_ID, baseEntity.getUserUpdate());
        assertEquals(InetAddress.getLocalHost().getHostAddress(), baseEntity.getIpNumberUpdate());
    }

    @Test
    public void preUpdate_withValues() throws UnknownHostException {

        final BaseEntity baseEntity = new BaseEntity();
        baseEntity.setIpNumberUpdate(randomNumeric(11));
        baseEntity.setUserUpdate(nextLong());

        listener.preUpdate(baseEntity);

        assertNotNull(baseEntity.getDateUpdate());
        assertNotEquals(USER_SYSTEM_ID, baseEntity.getUserUpdate());
        assertNotEquals(InetAddress.getLocalHost().getHostAddress(), baseEntity.getIpNumberUpdate());
    }

    @Test
    public void prePersist_withoutValues() throws UnknownHostException {

        final BaseEntity baseEntity = new BaseEntity();

        listener.prePersist(baseEntity);

        assertNotNull(baseEntity.getDateUpdate());
        assertNotNull(baseEntity.getDateCreate());
        assertEquals(USER_SYSTEM_ID, baseEntity.getUserUpdate());
        assertEquals(USER_SYSTEM_ID, baseEntity.getUserCreate());
        assertEquals(InetAddress.getLocalHost().getHostAddress(), baseEntity.getIpNumberUpdate());
        assertEquals(InetAddress.getLocalHost().getHostAddress(), baseEntity.getIpNumberCreate());
    }

    @Test
    public void prePersist_withValues() throws UnknownHostException {

        final BaseEntity baseEntity = new BaseEntity();
        baseEntity.setUserUpdate(nextLong());
        baseEntity.setUserCreate(nextLong());
        baseEntity.setIpNumberUpdate(randomNumeric(11));
        baseEntity.setIpNumberCreate(randomNumeric(11));

        listener.prePersist(baseEntity);

        assertNotNull(baseEntity.getDateUpdate());
        assertNotNull(baseEntity.getDateCreate());
        assertNotEquals(USER_SYSTEM_ID, baseEntity.getUserUpdate());
        assertNotEquals(USER_SYSTEM_ID, baseEntity.getUserCreate());
        assertNotEquals(InetAddress.getLocalHost().getHostAddress(), baseEntity.getIpNumberUpdate());
        assertNotEquals(InetAddress.getLocalHost().getHostAddress(), baseEntity.getIpNumberCreate());
    }
}