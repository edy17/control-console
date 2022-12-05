#!/bin/bash

kubectl create serviceaccount probe-agent -n default
kubectl create clusterrolebinding probe-role --clusterrole=cluster-admin --serviceaccount=default:probe-agent

kubectl create -f aggregator-service.yaml
kubectl create -f probe-service.yaml

kubectl create -f ingress.yaml

kubectl create -f configmap-envFrom.yaml

kubectl create -f aggregator-deployment.yaml
kubectl create -f probe-deployment.yaml
