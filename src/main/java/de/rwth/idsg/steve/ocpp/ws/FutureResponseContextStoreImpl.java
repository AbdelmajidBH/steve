package de.rwth.idsg.steve.ocpp.ws;

import de.rwth.idsg.steve.SteveException;
import de.rwth.idsg.steve.ocpp.ws.data.FutureResponseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 21.03.2015
 */
@Slf4j
@Service
public class FutureResponseContextStoreImpl implements FutureResponseContextStore {

    // We store for each chargeBoxId, multiple pairs of (messageId, context)
    // (chargeBoxId, (messageId, context))
    private final Map<String, Map<String, FutureResponseContext>> lookupTable = new HashMap<>();

    @Override
    public void addChargeBox(String chargeBoxId) {
        log.debug("Creating new store for chargeBoxId '{}'", chargeBoxId);
        lookupTable.put(chargeBoxId, new ConcurrentHashMap<String, FutureResponseContext>());
    }

    @Override
    public void removeChargeBox(String chargeBoxId) {
        log.debug("Deleting the store for chargeBoxId '{}'", chargeBoxId);
        lookupTable.remove(chargeBoxId);
    }

    @Override
    public void add(String chargeBoxId, String messageId, FutureResponseContext context) {
        Map<String, FutureResponseContext> map = lookupTable.get(chargeBoxId);
        if (map == null) {
            throw new SteveException("chargeBoxId '" + chargeBoxId + "' is not in store");
        } else {
            map.put(messageId, context);
        }
    }

    @Override
    public FutureResponseContext get(String chargeBoxId, String messageId) {
        Map<String, FutureResponseContext> map = lookupTable.remove(chargeBoxId);
        if (map == null) {
            throw new SteveException("chargeBoxId '" + chargeBoxId + "' is not in store");
        } else {
            return map.get(messageId);
        }
    }
}