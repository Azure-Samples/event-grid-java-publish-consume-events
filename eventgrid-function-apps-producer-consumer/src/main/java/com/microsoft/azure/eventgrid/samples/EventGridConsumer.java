/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.eventgrid.samples;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.microsoft.azure.eventgrid.models.EventGridEvent;
import com.microsoft.azure.eventgrid.models.StorageBlobCreatedEventData;
import com.microsoft.azure.serverless.functions.ExecutionContext;
import com.microsoft.azure.serverless.functions.annotation.EventGridTrigger;
import com.microsoft.azure.serverless.functions.annotation.FunctionName;

/**
 * Azure Functions with EventGrid Trigger.
 */
public class EventGridConsumer {
    /**
     */
    @FunctionName("EventGrid-Consumer")
    public void Run(@EventGridTrigger(name = "data") String data, final ExecutionContext executionContext) {
        executionContext.getLogger().info("Java EventGrid trigger function begun\n");
        executionContext.getLogger().info("\tFOUND: " + data);

        try {
            final String SubscriptionValidationEvent = "Microsoft.EventGrid.SubscriptionValidationEvent";
            final String StorageBlobCreatedEvent = "Microsoft.Storage.BlobCreated";
            final String CustomTopicEvent = "Contoso.Items.ItemReceived";
            final Gson gson = new GsonBuilder().create();

            EventGridEvent eventGridEvent = gson.fromJson(data, EventGridEvent.class);

            // Deserialize the event data into the appropriate type based on event type
            if (eventGridEvent.eventType().toLowerCase().equals(StorageBlobCreatedEvent.toLowerCase())) {
                // Deserialize the data portion of the event into StorageBlobCreatedEventData
                StorageBlobCreatedEventData eventData = (StorageBlobCreatedEventData) eventGridEvent.data();
                executionContext.getLogger().info("Got BlobCreated event data, blob URI " + eventData.url());
            }
            else if (eventGridEvent.eventType().toLowerCase().equals(CustomTopicEvent.toLowerCase())) {
                // Deserialize the data portion of the event into ContosoItemReceivedEventData
                ContosoItemReceivedEventData eventData = (ContosoItemReceivedEventData) eventGridEvent.data();
                executionContext.getLogger().info("Got ContosoItemReceived event data, item SKU " + eventData.itemSku);
            }
        } catch (Exception e) {
            executionContext.getLogger().info("UNEXPECTED Exception caught: " + e.toString());
        }
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
}
