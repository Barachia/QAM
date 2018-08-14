package hmi.qam.util;

import nl.utwente.hmi.middleware.Middleware;
import nl.utwente.hmi.middleware.MiddlewareListener;
import nl.utwente.hmi.middleware.helpers.JsonNodeBuilders;
import static nl.utwente.hmi.middleware.helpers.JsonNodeBuilders.object;
import nl.utwente.hmi.middleware.loader.GenericMiddlewareLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QAMMiddleware implements MiddlewareListener {

    protected BlockingQueue<JsonNode> queue = null;
    private static Logger logger = LoggerFactory.getLogger(QAMMiddleware.class.getName());
    Middleware middleware;

    public QAMMiddleware(String middlewareProps) {
        this.queue = new LinkedBlockingQueue<JsonNode>();
        Properties ps = new Properties();
        InputStream mwProps = QAMMiddleware.class.getClassLoader().getResourceAsStream(middlewareProps);

        try {
            ps.load(mwProps);
        } catch (IOException ex) {
            logger.warn("Could not load flipper middleware props file {}", mwProps);
            ex.printStackTrace();
        }

        GenericMiddlewareLoader.setGlobalPropertiesFile("qam.properties");

        GenericMiddlewareLoader gml = new GenericMiddlewareLoader("nl.utwente.hmi.middleware.activemq.ActiveMQMiddlewareLoader",ps);
        middleware = gml.load();
        middleware.addListener(this);
    }

    // { "content": "$data" }
    public void send(String data) {
        JsonNodeBuilders.ObjectNodeBuilder on = object();
        try {
            on.with("content", URLEncoder.encode(data, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        middleware.sendData(on.end());
    }

    public boolean hasMessage() {
        return isConnected() && !queue.isEmpty();
    }

    /**
     * only call this when hasMessage() returned true,
     * as it is otherwise blocking until it receives a message.
     */
    public String getMessage() {
        try {
            JsonNode msg = queue.take();
            logger.debug("Processing message from middleware: {}", msg);
            return msg.toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    @Override
    public void receiveData(JsonNode jn) {
        queue.clear();
        queue.add(jn);
    }

    public boolean isConnected() {
        return middleware != null;
    }

    public static void Log(String s) {
        logger.debug("\n===\n{}\n===", s);
    }
}
