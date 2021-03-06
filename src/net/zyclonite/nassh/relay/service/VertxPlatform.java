/*
 * nassh-relay - Relay Server for tunneling ssh through a http endpoint
 * 
 * Website: http://relay.wsn.at
 *
 * Copyright 2014   zyclonite    networx
 *                  http://zyclonite.net
 * Developer: Lukas Prettenthaler
 */
package net.zyclonite.nassh.relay.service;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.file.FileSystem;
import org.vertx.java.core.net.NetClient;
import org.vertx.java.core.shareddata.SharedData;
import org.vertx.java.platform.PlatformLocator;
import org.vertx.java.platform.PlatformManager;

/**
 *
 * @author zyclonite
 */
public class VertxPlatform implements Handler<AsyncResult<String>> {

    private static final Log LOG = LogFactory.getLog(VertxPlatform.class);
    private final PlatformManager mgr;
    private static final VertxPlatform instance;

    static {
        instance = new VertxPlatform();
    }

    private VertxPlatform() {
        mgr = PlatformLocator.factory.createPlatformManager();
    }

    public void deployVerticle(final String verticle) {
        mgr.deployVerticle(verticle, null, getClassPathAsURLArray(), 1, null, this);
    }

    public SharedData getSharedData() {
        return mgr.vertx().sharedData();
    }

    public EventBus getEventBus() {
        return mgr.vertx().eventBus();
    }

    public FileSystem getFileSystem() {
        return mgr.vertx().fileSystem();
    }

    public NetClient createNetClient() {
        return mgr.vertx().createNetClient();
    }

    public long setPeriodic(long time, Handler<Long> handler) {
        return mgr.vertx().setPeriodic(time, handler);
    }
    
    public boolean cancelTimer(long id) {
        return mgr.vertx().cancelTimer(id);
    }

    public void stop() {
        mgr.stop();
    }

    private static URL[] getClassPathAsURLArray() {
        String classPath = System.getProperty("java.class.path");
        String[] splitClassPath = classPath.split(";");
        URL[] classPathAsURLArray = new URL[splitClassPath.length];
        for (int i = 0; i < splitClassPath.length; i++) {
            try {
                classPathAsURLArray[i] = new URL("file:///" + splitClassPath[i].replace('\\', '/'));
            } catch (MalformedURLException ex) {
                LOG.warn(ex, ex.fillInStackTrace());
                classPathAsURLArray = null;
            }
        }
        return classPathAsURLArray;
    }

    public static VertxPlatform getInstance() {
        return instance;
    }

    @Override
    public void handle(AsyncResult<String> done) {
        if(done.succeeded()){
            LOG.debug("Verticle deployed " + done.result());
        }else{
            LOG.error("Verticle NOT deployed " + done.cause().getMessage());
        }
    }
}
