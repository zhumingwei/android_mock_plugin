#!/usr/bin/env bash

pre=${pwd}

echo "start build"

rm -r .repo

./gradlew uploadArchives

if [ -n "$1" ] && [ "$1" == "test" ] ;then
  echo "auto test"
  cd example
  ./gradlew assembleDebug --rerun-tasks
fi

cd ${pre}
