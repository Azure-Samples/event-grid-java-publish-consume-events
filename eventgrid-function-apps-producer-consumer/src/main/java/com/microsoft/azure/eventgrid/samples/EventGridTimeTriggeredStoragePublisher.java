/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.eventgrid.samples;

import com.microsoft.azure.serverless.functions.ExecutionContext;
import com.microsoft.azure.serverless.functions.annotation.FunctionName;
import com.microsoft.azure.serverless.functions.annotation.TimerTrigger;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.util.UUID;

/**
 * Azure Functions with Time Trigger.
 *  - Create and delete storage blobs which will be captured as EventGrid events
 */
public class EventGridTimeTriggeredStoragePublisher {
    @FunctionName("EventGrid-TimeTriggered-Storage-Publisher")
    public void EventGridWithStoragePublisher(@TimerTrigger(name = "timerInfo", schedule = "*/20 * * * * *")
                         String timerInfo,
                     final ExecutionContext executionContext) {

        try {
            // Parse the connection string and create a blob client to interact with Blob storage
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(System.getenv("EVENTGRID_STORAGE_CONNECTION_STRING"));
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = blobClient.getContainerReference(System.getenv("EVENTGRID_STORAGE_CONTAINER_NAME"));


            // Create the container if it does not exist with public access.
            container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());

            // Getting a blob reference
            String blobName = "eg_" + UUID.randomUUID().toString().replaceAll("-", "") + ".txt";
            CloudBlockBlob blob = container.getBlockBlobReference(blobName);

            // Creating blob and uploading file to it
            executionContext.getLogger().info("Uploading the sample text ");
            blob.uploadText(blobName + " content\n");

            // Sleep 10 seconds then delete the blob
            Thread.sleep(10000);

            // Delete the blob
            blob.delete();

        } catch (Exception e) {
            executionContext.getLogger().info("UNEXPECTED Exception caught: " + e.toString());
        }
    }
}
