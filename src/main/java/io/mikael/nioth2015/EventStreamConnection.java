package io.mikael.nioth2015;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import net.sf.xenqtt.client.AsyncClientListener;
import net.sf.xenqtt.client.AsyncMqttClient;
import net.sf.xenqtt.client.MqttClient;
import net.sf.xenqtt.client.PublishMessage;
import net.sf.xenqtt.client.Subscription;
import net.sf.xenqtt.message.ConnectReturnCode;
import net.sf.xenqtt.message.QoS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EventStreamConnection implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(EventStreamConnection.class);

    final AtomicReference<ConnectReturnCode> connectReturnCode = new AtomicReference<>();
    final CountDownLatch connectLatch = new CountDownLatch(1);

    @Value("${xenqtt.connection.url}")
    private String url;

    @Value("${xenqtt.connection.port}")
    private int port;

    @Value("${xenqtt.connection.clientId}")
    private String clientId;

    @Value("${xenqtt.connection.cleanSession}")
    private boolean cleanSession;

    @Value("${xenqtt.subscriptions.list}")
    private String subscriptions;

    @Value("${xenqtt.subscriptions.QoS}")
    private String qos;

    @Value("${xenqtt.connection.username}")
    private String username;

    @Value("${xenqtt.connection.password}")
    private String password;

    @Autowired
    private EventProcessor processor;

    @Override
    public void run(String... arg0) throws Exception {
        LOG.info("connecting to " + url);
        final AsyncMqttClient client = new AsyncMqttClient(url + ":" + port, new Subscriber(connectLatch, connectReturnCode, processor), 5);
        try {
            LOG.info("clientId: " + clientId);
            LOG.info("cleanSession: " + cleanSession);
            LOG.info("username: " + username);
            LOG.info("password: " + password);

            client.connect(clientId, cleanSession, username, password);

            connectLatch.await();

            ConnectReturnCode returnCode = connectReturnCode.get();
            if (returnCode == null || returnCode != ConnectReturnCode.ACCEPTED) {
                LOG.error("Unable to connect to the MQTT broker. Reason: " + returnCode);
                return;
            }
            LOG.info("connected");

            //set up subscriptions
            final List<Subscription> subscriptions = getSubscriptions();
            LOG.info("subscriptions: " + subscriptions);
            if (subscriptions.size() > 0) {
                client.subscribe(subscriptions);
            }

        } catch (Exception ex) {
            LOG.error("An unexpected exception has occurred.", ex);
        }
    }

    private List<Subscription> getSubscriptions() {
        final List<Subscription> subscriptionsToCreate = new ArrayList<>();
        final String[] subscriptionArray = subscriptions.split(",");
        final String[] qosArray = qos.split(",");
        final int[] qos = new int[qosArray.length];
        for (int i = 0; i < qosArray.length; i++) {
            try {
                qos[i] = Integer.parseInt(qosArray[i]);
            } catch (NumberFormatException ignored) {}
        }
        if (subscriptionArray.length != qos.length) {
            LOG.error("could not create subscriptions, qos array length: " + qos.length + " != topic array length: " + subscriptionArray.length);
        } else {
            for (int i = 0; i < subscriptionArray.length; i++) {
                System.out.println();
                subscriptionsToCreate.add(new Subscription(subscriptionArray[i], QoS.lookup(qos[i])));
            }
        }
        return subscriptionsToCreate;
    }

    private static class Subscriber implements AsyncClientListener {
        private static final Logger LOG = LoggerFactory.getLogger(Subscriber.class);
        private CountDownLatch latch;
        private AtomicReference<ConnectReturnCode> connectReturnCode;
        private EventProcessor processor;

        public Subscriber(CountDownLatch latch, AtomicReference<ConnectReturnCode> connectReturnCode, final EventProcessor processor) {
            this.latch = latch;
            this.connectReturnCode = connectReturnCode;
            this.processor = processor;
        }

        @Override
        public void disconnected(MqttClient client, Throwable cause, boolean reconnecting) {
            if (cause != null) {
                LOG.error("Disconnected from the broker due to an exception.", cause);
            } else {
                LOG.info("Disconnecting from the broker.");
            }

            if (reconnecting) {
                LOG.info("Attempting to reconnect to the broker.");
            }
        }

        @Override
        public void publishReceived(MqttClient client, PublishMessage message) {
            LOG.info("received");
            try {
                processor.submitMessage(message.getPayloadString());
            } catch (final Exception e) {
                LOG.error("failed to submit message", e);
            }
        }

        @Override
        public void connected(MqttClient client, ConnectReturnCode returnCode) {
            LOG.info("connected");
            connectReturnCode.set(returnCode);
            latch.countDown();
        }

        @Override
        public void published(MqttClient client, PublishMessage message) {
            LOG.info("published");
        }

        @Override
        public void subscribed(MqttClient client, Subscription[] requestedSubscriptions, Subscription[] grantedSubscriptions, boolean requestsGranted) {
            LOG.info("subscribed");

        }

        @Override
        public void unsubscribed(MqttClient client, String[] topics) {
            LOG.info("unsubscribed");
        }

    }

}

