---
services: EventGrid
platforms: java
author: milismsft
---

## Getting Started with Event Grid - Publish and Consume - in Java ##


Azure Event Grid sample for publishing and consuming custom events
 * Create a resource group.
 * Create an Azure EventHub resource which will be used for receiving and pulling the events.
 * Create an Azure EventGrid topic;
 * Create an EventGrid Subscription with EventHub destination.
 * Create an EventGrid client object and use it to publish custom events to the EventGrid
 * Create an EventHub client and use it to pull/receive the custom events from the EventGrid via a PartitionReceiver.
 

## Running this Sample ##

To run this sample:

Set the environment variable `AZURE_AUTH_LOCATION` with the full path for an auth file. See [how to create an auth file](https://github.com/Azure/azure-sdk-for-java/blob/master/AUTH.md).

    git clone https://github.com/Azure-Samples/event-grid-java-publish-consume-events.git

    cd eventgrid-manage-producer-consumer

    mvn clean compile exec:java

## More information ##

[http://azure.com/java](http://azure.com/java)

If you don't have a Microsoft Azure subscription you can get a FREE trial account [here](http://go.microsoft.com/fwlink/?LinkId=330212)

---

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
