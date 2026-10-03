#!/bin/bash
# Run CampusFind Java version (LAN-ready, no Tomcat install needed).
# Uses embedded Tomcat + system classpath (avoids exec:java classloader issues).
set -e
export JAVA_HOME=~/tools/jdk17/Contents/Home
export PATH=$JAVA_HOME/bin:~/tools/apache-maven-3.9.6/bin:$PATH
DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"
mvn -q package -DskipTests
CP="target/classes:$(cat target/classpath.txt)"
IP=$(ipconfig getifaddr en0 2>/dev/null || ipconfig getifaddr en1 2>/dev/null || echo "YOUR-IP")
echo ""
echo "Open locally : http://localhost:8080"
echo "Friend (same WiFi) opens: http://${IP}:8080"
echo "Stop with Ctrl+C"
echo ""
java -cp "$CP" com.campusfind.App
