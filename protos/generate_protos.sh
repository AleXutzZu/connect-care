#!/usr/bin/bash

for file in $(find . -type f -name "*.proto")
do
  /home/alexutzzu/IdeaProjects/teledon-management/protoc/bin/protoc -I=. "$file" --java_out="../backend-server/src/main/java/" --csharp_out="../desktop-client/Protos"
done

