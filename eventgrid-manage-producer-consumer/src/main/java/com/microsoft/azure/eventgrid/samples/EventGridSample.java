/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.eventgrid.samples;

import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.eventgrid.EventGridClient;
import com.microsoft.azure.eventgrid.TopicCredentials;
import com.microsoft.azure.eventgrid.implementation.EventGridClientImpl;
import com.microsoft.azure.eventgrid.models.EventGridEvent;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubRuntimeInformation;
import com.microsoft.azure.eventhubs.EventPosition;
import com.microsoft.azure.eventhubs.PartitionReceiver;
import com.microsoft.azure.management.eventgrid.v2018_01_01.EventHubEventSubscriptionDestination;
import com.microsoft.azure.management.eventgrid.v2018_01_01.EventSubscription;
import com.microsoft.azure.management.eventgrid.v2018_01_01.EventSubscriptionFilter;
import com.microsoft.azure.management.eventgrid.v2018_01_01.Topic;
import com.microsoft.azure.management.eventgrid.v2018_01_01.implementation.EventGridManager;
import com.microsoft.azure.management.eventhub.EventHub;
import com.microsoft.azure.management.eventhub.EventHubAuthorizationRule;
import com.microsoft.azure.management.eventhub.EventHubNamespace;
import com.microsoft.azure.management.eventhub.EventHubNamespaceSkuType;
import com.microsoft.azure.management.eventhub.implementation.EventHubManager;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;
import com.microsoft.azure.management.resources.implementation.ResourceManager;
import com.microsoft.rest.LogLevel;
import org.joda.time.DateTime;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Azure Event Grid sample for publishing and consuming custom events
 *   - Create a resource group.
 *   - Create an Azure EventHub resource which will be used for receiving and pulling the events.
 *   - Create an Azure EventGrid topic;
 *   - Create an EventGrid Subscription with EventHub destination.
 *   - Create an EventGrid client object and use it to publish custom events to the EventGrid
 *   - Create an EventHub client and use it to pull/receive the custom events from the EventGrid via a PartitionReceiver.
 */
public class EventGridSample {
    private static ResourceManager resourceManager;
    private static EventHubManager eventHubManager;
    private static EventGridManager eventGridManager;
    private static EventGridClient eventGridClient;

    /**
     * Main function which runs the actual sample.
     * @return true if sample runs successfully
     */
    public static boolean runSample() {
        final String rgName = SdkContext.randomResourceName("rgeventgrid", 24);
        final String eventHubNamespaceName = SdkContext.randomResourceName("ehns", 24);
        final String eventHubRuleName = "ehRule1";
        final String topicName = SdkContext.randomResourceName("topicsample", 24);
        final String eventSubscriptionName = "EventSubscription1";
        final String defaultRegion = Region.US_WEST.label();

        try {

            //============================================================
            // Create a resource group.
            //
            System.out.println("Creating a resource group");
            resourceManager.resourceGroups().define(rgName)
                .withRegion(defaultRegion)
                .create();

            System.out.println("Resource group created with name " + rgName);

            //============================================================
            // Create an event hub.
            //
            System.out.println("Creating an Azure EventHub");

            EventHubNamespace eventHubNamespace = eventHubManager.namespaces().define(eventHubNamespaceName)
                .withRegion(defaultRegion)
                .withExistingResourceGroup(rgName)
                .withAutoScaling()
                .withSku(EventHubNamespaceSkuType.STANDARD)
                .withNewEventHub("eh1", 2, 1)
                .withNewManageRule("rule1")
                .withTag("key1", "value1")
                .create();

            System.out.println("EventHub namespace created with name " + eventHubNamespace.name());

            EventHub eventHub = eventHubNamespace.listEventHubs().get(0);
            System.out.println("EventHub created with name " + eventHub.name());

            System.out.println("EventHub update with new managed rule");
            eventHub.update()
                .withNewManageRule(eventHubRuleName)
                .apply();

            //============================================================
            // Create an event grid topic.
            //
            System.out.println("Creating an Azure EventGrid topic");

            Topic eventGridTopic = eventGridManager.topics().define(topicName)
                .withRegion(defaultRegion)
                .withExistingResourceGroup(rgName)
                .withTag("key1", "value1")
                .withTag("key2", "value2")
                .create();

            System.out.println("EventGrid topic created with name " + eventGridTopic.name());

            //============================================================
            // Create an event grid subscription.
            //
            System.out.println("Creating an Azure EventGrid Subscription");

            EventSubscription eventSubscription = eventGridManager.eventSubscriptions().define(eventSubscriptionName)
                .withScope(eventGridTopic.id())
                .withDestination(new EventHubEventSubscriptionDestination()
                    .withResourceId(eventHub.id()))
                .withFilter(new EventSubscriptionFilter()
                    .withIsSubjectCaseSensitive(false)
                    .withSubjectBeginsWith("")
                    .withSubjectEndsWith(""))
                .create();

            System.out.println("EventGrid event subscription created with name " + eventSubscription.name());

            //============================================================
            // Retrieve the event grid client connection key.
            //
            System.out.println("Retrieve the event grid client connection key");

            String eventGridClientKey = eventGridManager.topics().listSharedAccessKeysAsync(rgName, topicName).toBlocking().last().key1();

            System.out.format("Found EventGrid client connection key \"%s\" for endpoint \"%s\"\n", eventGridClientKey, eventGridTopic.endpoint());

            //============================================================
            // Create an event grid client.
            //
            System.out.println("Creating an Azure EventGrid Client");

            TopicCredentials topicCredentials = new TopicCredentials(eventGridClientKey);
            EventGridClient client = new EventGridClientImpl(topicCredentials);

            System.out.println("Done creating an Azure EventGrid Client...");

            //============================================================
            // Publish custom events to the EventGrid.
            //

            System.out.println("Publish custom events to the EventGrid");
            List<EventGridEvent> eventsList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                eventsList.add(new EventGridEvent(
                    SdkContext.randomUuid(),
                    String.format("Door%d", i),
                    new ContosoItemReceivedEventData("Contoso Item SKU #1"),
                    "Contoso.Items.ItemReceived",
                    DateTime.now(),
                    "2.0"
                ));
            }

            String eventGridEndpoint = String.format("https://%s/", new URI(eventGridTopic.endpoint()).getHost());

            client.publishEvents(eventGridEndpoint, eventsList);

            System.out.println("Done publishing custom events to the EventGrid");

            //============================================================
            // Create an EventHub client.
            //

            System.out.println("Creating an Azure EventHub Client");
            EventHubAuthorizationRule eventHubRule = eventHub.listAuthorizationRules().get(0);
            if (!eventHubRule.name().equals(eventHubRuleName)) {
                return false;
            }
            final String eventHubConnectionString = eventHubRule.getKeys().primaryConnectionString();
            final ExecutorService executorService = Executors.newSingleThreadExecutor();
            final EventHubClient ehClient = EventHubClient.createSync(eventHubConnectionString, executorService);
            final EventHubRuntimeInformation eventHubInfo = ehClient.getRuntimeInformation().get();
            System.out.format("EventHub Runtime information\n\tpath: %s\n\tpartition count: %d\n\tcreated at: %s\n", eventHubInfo.getPath(), eventHubInfo.getPartitionCount(), eventHubInfo.getCreatedAt().toString());

            System.out.println("Done creating an Azure EventHub Client...");

            //============================================================
            // Receive custom events from the EventGrid.
            //

            System.out.println("Receive custom events from the EventGrid");

            try {
                for (int idx = 0; idx < eventHubInfo.getPartitionCount(); idx++) {
                    final String partitionId = eventHubInfo.getPartitionIds()[idx]; // get first partition's id

                    final PartitionReceiver receiver = ehClient.createEpochReceiverSync(
                        EventHubClient.DEFAULT_CONSUMER_GROUP_NAME,
                        partitionId,
                        EventPosition.fromStartOfStream(),
                        2345);

                    System.out.println("receiver created from sequenceNumber...");

                    int receivedCount = 0;
                    while (receivedCount++ < 1) {
                        receiver.receive(10)
                            .thenAcceptAsync(receivedEvents -> {
                                int batchSize = 0;
                                if (receivedEvents != null) {
                                    for (EventData receivedEvent : receivedEvents) {
                                        System.out.print(String.format("Offset: %s, SeqNo: %s, EnqueueTime: %s",
                                            receivedEvent.getSystemProperties().getOffset(),
                                            receivedEvent.getSystemProperties().getSequenceNumber(),
                                            receivedEvent.getSystemProperties().getEnqueuedTime()));

                                        if (receivedEvent.getBytes() != null)
                                            System.out.println(String.format("| Message Payload: %s", new String(receivedEvent.getBytes(), Charset.defaultCharset())));
                                        batchSize++;
                                    }
                                }

                                System.out.println(String.format("ReceivedBatch Size: %s", batchSize));
                            }, executorService).get();
                    }
                    // cleaning up receivers is paramount;
                    // Quota limitation on maximum number of concurrent receivers per consumergroup per partition is 5
                    receiver.closeSync();
//                        .thenComposeAsync(aVoid -> ehClient.close(), executorService)
//                        .whenCompleteAsync((t, u) -> {
//                            if (u != null) {
//                                // wire-up this error to diagnostics infrastructure
//                                System.out.println(String.format("closing failed with error: %s", u.toString()));
//                            }
//                        }, executorService).get();
                }
            } finally {
                ehClient.closeSync();
                executorService.shutdown();
            }
            System.out.println("Done receive custom events from the EventGrid");

            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Deleting Resource Group: " + rgName);
                resourceManager.resourceGroups().beginDeleteByName(rgName);
                System.out.println("Deleted Resource Group: " + rgName);
            } catch (NullPointerException npe) {
                System.out.println("Did not create any resources in Azure. No clean up is necessary");
            } catch (Exception g) {
                g.printStackTrace();
            }
        }
        return false;
    }

    /**
     * This captures the "Data" portion of an EventGridEvent on a custom topic
     */
    static class ContosoItemReceivedEventData
    {
        public String itemSku;

        public ContosoItemReceivedEventData(String itemSku) {
            this.itemSku = itemSku;
        }
    }


    /**
     * Main entry point.
     * @param args the parameters
     */
    public static void main(String[] args) {
        try {
            //=============================================================
            // Authenticate

            final File credFile = new File(System.getenv("AZURE_AUTH_LOCATION"));

            ApplicationTokenCredentials credentials = ApplicationTokenCredentials.fromFile(credFile);

            resourceManager = ResourceManager.configure()
                .withLogLevel(LogLevel.BASIC)
                .authenticate(credentials)
                .withSubscription(credentials.defaultSubscriptionId());

            eventHubManager = EventHubManager.configure()
                .withLogLevel(LogLevel.BASIC)
                .authenticate(credentials, credentials.defaultSubscriptionId());

            eventGridManager = EventGridManager
                .configure()
                .withLogLevel(LogLevel.BASIC)
                .authenticate(credentials, credentials.defaultSubscriptionId());

            // Print selected subscription
            System.out.println("Selected subscription: " + credentials.defaultSubscriptionId());

            runSample();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
