---
services: EventGrid
platforms: java
author: milismsft
---

# Microsoft Azure Event Grid Publish/Consume Samples for Java using Serverless

This contains Java samples for publishing events to Azure Event Grid and consuming events from Azure Event Grid using Azure Function Apps

## Features

These samples demonstrates the following features:

* How to publish custom topic events to Azure Event Grid.
* How to enable Azure Event Grid events when working with Azure Storage blobs.
* How to consume events delivered by Azure Event Grid.

## Getting Started

### Prerequisites

- Azure CLI for provisioning the Azure resources such as resource group, storage account, EventGrid topic, EventSubscription
- Azure Functions Core Tools (https://docs.microsoft.com/en-us/azure/azure-functions/functions-run-local)

## Running this Sample ##

To run this sample:

    git clone https://github.com/Azure-Samples/event-grid-java-publish-consume-events.git

    cd eventgrid-function-apps-producer-consumer
    
    az login

    source ./azure_cli_startup.sh
    
    mvn clean package
    
    mvn azure-functions:deploy


 The following are the steps to run the sample and see events flowing through Event Grid:


 1. Clone the sample Git repro.
  

    git clone https://github.com/Azure-Samples/event-grid-java-publish-consume-events.git

    cd eventgrid-function-apps-producer-consumer


 2. Log in to Azure CLI and create the Azure resources required by this sample.  

    
    az login

    source ./azure_cli_startup.sh

 3. Build and publish the Azure function sample. 

     
    mvn clean package azure-functions:deploy

 4. Once the Azure Function App has been published, navigate to the newly published function in the Azure Portal, find the EventGridConsumer entry and click on "Get Function URL" to copy the function URL. Save this URL as it will be used to create the event subscriptions.

     
    export EVENTGRID_ENDPOINT="https://egfuncw2olyf3isr.azurewebsites.net/runtime/webhooks/EventGridExtensionConfig?functionName=EventGrid-Consumer&code=code"

 5. Create a new event subscription for a storage account, using default filters.
 
     
    export EVENTGRID_STORAGE_ACCOUNT_ID=$(az storage account show --resource-group $EVENTGRID_RESOURCE_GROUP_NAME --name $EVENTGRID_STORAGE_ACCOUNT_NAME --query id | sed 's/"//g')
    export EVENTGRID_STORAGE_CONTAINER_ID=$(az storage account show --resource-group $EVENTGRID_RESOURCE_GROUP_NAME --name $EVENTGRID_STORAGE_ACCOUNT_NAME --query id | sed 's/"//g')/$EVENTGRID_STORAGE_CONTAINER_NAME
    
   
 6. Create a new event subscription for a custom topic event, using default filters.
  
      
     az eventgrid event-subscription create --name egtopic1 --resource-group $EVENTGRID_RESOURCE_GROUP_NAME --topic-name $EVENTGRID_TOPIC_NAME --endpoint $EVENTGRID_ENDPOINT
    
 7. Verify the events are received; in this step, we will be verifying that the events are delivered to your event subscription. Here are the steps:

    a. Log in to the Azure Portal, navigate to the newly created function and find the EventGridConsumer entry.

    b. In the Logs view of the EventGridConsumer entry for the Azure Function, verify that you can see the logs that show the receipt of the EventGridEvent.
 
 
## Resources

(Any additional resources or related projects)

- https://docs.microsoft.com/en-us/azure/event-grid/overview
- https://docs.microsoft.com/en-us/azure/azure-functions/functions-develop-vs

## More information ##

[http://azure.com/java](http://azure.com/java)

If you don't have a Microsoft Azure subscription you can get a FREE trial account [here](http://go.microsoft.com/fwlink/?LinkId=330212)

---

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
