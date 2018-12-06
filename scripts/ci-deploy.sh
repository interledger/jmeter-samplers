#!/bin/bash -ex

pushDocker() {
  # Push Docker image tagged latest and tagged with commit descriptor
  local REGISTRY=""
  local NAMESPACE="interledger/"
  local REPO="jmeter-samplers"
  # rm is false because on Circle the process doesn't have permissions to delete the intermediate container
  docker build -t $NAMESPACE$REPO --rm=false .
  docker login -u $DOCKER_USER -p $DOCKER_PASS -e $DOCKER_EMAIL $REGISTRY
  docker tag $NAMESPACE$REPO":latest" $NAMESPACE$REPO":$(git describe)"
  docker push $NAMESPACE$REPO":latest"
  docker push $NAMESPACE$REPO":$(git describe)"
}

pushDocker