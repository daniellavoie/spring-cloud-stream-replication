#!/bin/bash

set -ex

export MAVEN_USER_HOME=$(cd maven-cache && pwd)
export MAVEN_ARGS='-Dmaven.repo.local=../maven-cache/repository'

pushd src
    echo "${VERSION}" > ../parent-build-output/version
    echo "${VERSION}" > ../starter-build-output/version

    ./mvnw install $MAVEN_ARGS

    cp ../maven-cache/repository/io/daniellavoie/spring/replication/spring-cloud-stream-replication-parent/${VERSION}/* ../parent-build-output
    cp ../maven-cache/repository/io/daniellavoie/spring/replication/spring-cloud-stream-replication-starter/${VERSION}/* ../starter-build-output
popd
