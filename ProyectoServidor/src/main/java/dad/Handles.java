package dad;

import java.util.List;
import java.util.ArrayList;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttTopicSubscription;
import io.vertx.mqtt.messages.MqttPublishMessage;

public class Handles {

	static void handleSubscription(MqttEndpoint endpoint) {
		endpoint.subscribeHandler(subscribe -> {
			List<MqttQoS> grantedQosLevels = new ArrayList<>();
			for (MqttTopicSubscription s : subscribe.topicSubscriptions()) {
				System.out.println("Suscripción al topic " + s.topicName());
				grantedQosLevels.add(s.qualityOfService());
			}
			endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);
		});
	}

	static void handleUnsubscription(MqttEndpoint endpoint) {
		endpoint.unsubscribeHandler(unsubscribe -> {
			for (String t : unsubscribe.topics()) {
				System.out.println("Eliminada la suscripción del topic " + t);
			}
			endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
		});
	}

	static void publishHandler(MqttEndpoint endpoint) {
		endpoint.publishHandler(message -> {
			handleMessage(message, endpoint);
		}).publishReleaseHandler(messageId -> {
			endpoint.publishComplete(messageId);
		});
	}

	private static void handleMessage(MqttPublishMessage message, MqttEndpoint endpoint) {
		System.out.println("Mensaje publicado");
		if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
			String topicName = message.topicName();
			switch (topicName) {
				// Hacer algo con el mensaje si es necesario
			}
			endpoint.publishAcknowledge(message.messageId());
		} else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
			endpoint.publishRelease(message.messageId());
		}
		}

}
