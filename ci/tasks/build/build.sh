#!/bin/bash

set -ex

export MAVEN_USER_HOME=$(cd maven-cache && pwd)
export MAVEN_ARGS='-Dmaven.repo.local=../maven-cache/repository'
VERSION=`cat version/number`

VERSION_FOLDER=$VERSION

if [ ! -z "$VERSION_SUFFIX" ]; then
    VERSION_FOLDER=$VERSION-$VERSION_SUFFIX
fi


if [ -z "$REPOSITORY_PATH" ]; then
    echo "Variable REPOSITORY_PATH is undefined."

    exit 1
fi

pushd src
    echo "${VERSION}" > ../build-output/version

    ./mvnw install -Drevision=$VERSION $MAVEN_ARGS

    cp ../maven-cache/repository/$REPOSITORY_PATH/${VERSION_FOLDER}/* ../build-output
popd
