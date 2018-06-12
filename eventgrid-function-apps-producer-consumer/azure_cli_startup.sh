#!/bin/bash
#

export EVENTGRID_RESOURCE_GROUP_NAME="rgeventgrid11"
export EVENTGRID_REGION_NAME="westus"
export EVENTGRID_STORAGE_ACCOUNT_NAME=$(cat /dev/urandom | env LC_CTYPE=C tr -dc 'a-z0-9' | fold -w 20 | head -n 1)
export EVENTGRID_STORAGE_CONTAINER_NAME=eventgridsample
export EVENTGRID_FUNCTION_APP_NAME=egfunc$(cat /dev/urandom | env LC_CTYPE=C tr -dc 'a-z0-9' | fold -w 10 | head -n 1)
export EVENTGRID_TOPIC_NAME=egtopic$(cat /dev/urandom | env LC_CTYPE=C tr -dc 'a-z0-9' | fold -w 10 | head -n 1)

# Create an Azure Resource Group
az group create --name $EVENTGRID_RESOURCE_GROUP_NAME --location $EVENTGRID_REGION_NAME

# Create an Azure Storage Account
az storage account create --location $EVENTGRID_REGION_NAME --resource-group $EVENTGRID_RESOURCE_GROUP_NAME --name $EVENTGRID_STORAGE_ACCOUNT_NAME --sku Standard_LRS --kind BlobStorage --access-tier Hot

# Retrieve the Azure Storage Account connection string
export EVENTGRID_STORAGE_CONNECTION_STRING=$(az storage account show-connection-string --resource-group $EVENTGRID_RESOURCE_GROUP_NAME --name $EVENTGRID_STORAGE_ACCOUNT_NAME --output table | grep DefaultEndpointsProtocol)

# Create an Azure Storage Container
az storage container create --connection-string $EVENTGRID_STORAGE_CONNECTION_STRING --name $EVENTGRID_STORAGE_CONTAINER_NAME

# Create an Azure EventGrid Topic
az eventgrid topic create --resource-group $EVENTGRID_RESOURCE_GROUP_NAME --location $EVENTGRID_REGION_NAME --name $EVENTGRID_TOPIC_NAME

# Retrieve the EventGrid Topic Endpoint and Key
export EVENTGRID_TOPIC_ENDPOINT=$(az eventgrid topic show --resource-group $EVENTGRID_RESOURCE_GROUP_NAME --name $EVENTGRID_TOPIC_NAME --output tsv | awk '{print $1;}')
export EVENTGRID_TOPIC_KEY=$(az eventgrid topic key list --resource-group $EVENTGRID_RESOURCE_GROUP_NAME --name $EVENTGRID_TOPIC_NAME --output tsv | awk '{print $1;}')

