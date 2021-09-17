---
page_type: sample
languages:
- java
products:
- azure
description: "Java samples for publishing and consuming events from Azure Event Grid."
urlFragment: microsoft-azure-event-grid-java-sample
---

***DISCLAIMER: The data-plane samples in this repo are for azure-eventgrid v1 (1.x). For the samples for v4 (4.x and above) please visit [here](https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/eventgrid/azure-messaging-eventgrid/src/samples/java). This repo is archived since v4 has become stable. The management-plane samples in this repo uses a beta version of the client library. A stable version of [Event Grid management library](https://mvnrepository.com/artifact/com.azure.resourcemanager/azure-resourcemanager-eventgrid/1.0.0) is now available and to view the samples, please visit [here](https://github.com/azure/azure-sdk-for-java/tree/main/sdk/eventgrid/azure-resourcemanager-eventgrid/src/samples).***

# Microsoft Azure Event Grid Publish/Consume Samples for Java

This contains Java samples for publishing events to Azure Event Grid and consuming events from Azure Event Grid. It also contains a set of management samples that demonstrates how to manage topics and event subscriptions using Java code.

## Features

These samples demonstrates the following features:

* How to create a topic and an event subscription to a topic using Java and Azure CLI.
* How to create an event subscription to a blob storage using Azure CLI.
* How to create an event hub using Java.
* How to publish events to Azure Event Grid using Java and Azure Functions.
* How to consume events delivered by Azure Event Grid using an Azure Function and through an Azure Event Hub event processor.


## Getting Started

### Prerequisites

- Azure CLI for provisioning the Azure resources such as resource group, storage account, EventGrid topic, EventSubscription
- Azure Functions Core Tools (https://docs.microsoft.com/en-us/azure/azure-functions/functions-run-local)


## Running this Sample ##

To run these samples clone the repo, go to the respective sample directory and follow the README.md steps for each particular sample.

## Resources

(Any additional resources or related projects)

- https://docs.microsoft.com/en-us/azure/event-grid/overview
- https://docs.microsoft.com/en-us/azure/azure-functions/functions-develop-vs

## More information ##

[http://azure.com/java](http://azure.com/java)

If you don't have a Microsoft Azure subscription you can get a FREE trial account [here](http://go.microsoft.com/fwlink/?LinkId=330212)

---

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
