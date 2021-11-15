package it.kdm.doctoolkit.utils;

import com.google.common.base.Optional;
import org.apache.commons.io.FileUtils;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.CacheElement;
import org.apache.jcs.engine.ElementAttributes;
import org.apache.jcs.engine.behavior.IElementAttributes;
import org.apache.jcs.engine.control.event.behavior.IElementEvent;
import org.apache.jcs.engine.control.event.behavior.IElementEventConstants;
import org.apache.jcs.engine.control.event.behavior.IElementEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.EventObject;
import java.util.HashMap;

/**
 * Created by Lorenzo Lucherini
 * Date: 11/21/13
 * Time: 12:51 PM
 */
public class CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    private static class CacheEventHandler implements IElementEventHandler {

        @Override
        public void handleElementEvent(IElementEvent event) {
            int eventType = event.getElementEvent();
            CacheElement element = (CacheElement)((EventObject)event).getSource();

            if (eventType != IElementEventConstants.ELEMENT_EVENT_SPOOLED_DISK_AVAILABLE) {
                // All the other events must result in eviction
                File tmpFile = new File(element.getVal().toString());
                if (tmpFile.exists()) {
                    try {
                        logger.info("Deleting temporary file {}", tmpFile.getAbsoluteFile());
                        FileUtils.forceDelete(tmpFile);

                    } catch (IOException e) {
                        try {
                            logger.error("Failed to delete temp file {}. Retrying on exit", tmpFile.getAbsoluteFile());
                            FileUtils.forceDeleteOnExit(tmpFile);

                        } catch (IOException e1) {
                            logger.error(String.format("Failed to delete temp file %s", tmpFile.getAbsoluteFile()),
                                    e1);
                        }
                    }
                }
            }
        }
    }

    private static HashMap<String, CacheManager> instances =
            new HashMap<>();

    public static boolean active = true;

    public static CacheManager cifsCache() throws CacheException {
        return getInstance("default");
    }

    public static CacheManager tipsCache() throws CacheException {
        return getInstance("tips");
    }

    private static CacheManager getInstance(String region) throws CacheException {
        if (!instances.containsKey(region)) {
            instances.put(region, new CacheManager(region));
        }

        return instances.get(region);
    }

    public static CacheManager descCache() throws CacheException {
        return getInstance("descriptions");
    }

    public static CacheManager confCache() throws CacheException {
//        return getInstance("configuration");
        String region = "configuration";
        if (!instances.containsKey(region)) {
            CacheManager confInstance = new CacheManager(region);

            IElementAttributes attributes = confInstance.cache.getDefaultElementAttributes();
            attributes.addElementEventHandler(new CacheManager.CacheEventHandler());
            confInstance.cache.setDefaultElementAttributes(attributes);

            instances.put(region, confInstance);
        }

        return instances.get(region);
    }
    public static CacheManager fileCache() throws CacheException {
        String region = "files";
        if (!instances.containsKey(region)) {
            CacheManager filesInstance = new CacheManager(region);

            IElementAttributes attributes = filesInstance.cache.getDefaultElementAttributes();
            attributes.addElementEventHandler(new CacheManager.CacheEventHandler());
            filesInstance.cache.setDefaultElementAttributes(attributes);

            instances.put(region, filesInstance);
        }

        return instances.get(region);
    }

    private JCS cache;
    private CacheManager(String group) throws CacheException {
        cache = JCS.getInstance(group);
    }

    public <T> Optional<T> get(String path, Class<T> klass) {
        logger.debug("requested item with path: {}", path);
        Optional obj = get(path);
        if (obj.isPresent() && klass.isInstance(obj.get())) {
            return Optional.of(klass.cast(obj.get()));
        }

        return Optional.absent();
    }

    public Optional get(String path) {
        logger.debug("requested item with path: {}", path);
        return Optional.fromNullable(cache.get(path));
    }

    public void put(String path, Object obj) throws CacheException {
        logger.debug("inserted item with path: {}", path);
        cache.put(path, obj);
    }

    public void put(String path, Object obj, IElementAttributes elementAttributes) throws CacheException {
        logger.debug("inserted item with path: {}", path);
        cache.put(path, obj, elementAttributes);
    }

    public void remove(String path) throws CacheException {
        logger.debug("removed item with path: {}", path);
        cache.remove(path);
    }

    public IElementAttributes getDefaultElementAttributes() throws CacheException {
        return cache.getDefaultElementAttributes();
    }

}
