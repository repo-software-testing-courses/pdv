#!/bin/bash

if [ $# -ne 1 ]; then
    echo "Illegal number of parameters"
    exit
fi

path_docker=$(pwd)
path_project_angular=$(dirname $(pwd))/Angular
path_project=$(dirname $(pwd))/TrackOrJargh
path_jar=$path_project/target
name_image=$1

echo "Compiling angular application"
cd $path_project_angular
npx ng build --prod --base-href /new/

if [ -d "$path_project/src/main/resources/static/new" ]; then
    echo "Remove old angular application"
    rm -R $path_project/src/main/resources/static/new
fi

echo "Move angular application to java application"

mkdir $path_project/src/main/resources/static/new
cp -rf dist/* $path_project/src/main/resources/static/new

echo "Compiling java application"
cd $path_project
mvn package -DskipTests

echo "Copying java application"
cd $path_docker
cp -rf $path_project/files .
mv $path_jar/TrackOrJargh-0.0.1-SNAPSHOT.jar .
mv TrackOrJargh-0.0.1-SNAPSHOT.jar webapp-1.java

echo "Creating docker image"
docker build -t $name_image .