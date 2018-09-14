#!/bin/bash

set -ex

export MAVEN_USER_HOME=$(cd maven-cache && pwd)
export MAVEN_ARGS='-Dmaven.repo.local=../maven-cache/repository'

if [ -z "$REPOSITORY_PATH" ]; then
    echo "Variable REPOSITORY_PATH is undefined."

    exit 1
fi

pushd src
    echo "${VERSION}" > ../build-output/version

    ./mvnw install $MAVEN_ARGS

    cp ../maven-cache/repository/$REPOSITORY_PATH/${VERSION}/* ../build-output
popd
