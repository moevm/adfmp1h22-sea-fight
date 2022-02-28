#!/bin/bash

# Usage:
# - cd to your android app root
# - call script
# - enjoy the results!

seed=${1:-"seed"}
num_of_events=${2:-"1000"}
#Get package name

package=$(cat app/build.gradle | grep applicationId | grep -o '"[^"]\+"' | grep -o '[^"]\+')


cd ~/Android/Sdk/platform-tools


set -x
./adb shell monkey -p ${package} -v ${num_of_events} -s ${seed}
set +x
