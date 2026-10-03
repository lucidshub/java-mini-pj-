#!/bin/bash
# Run CampusFind Java version (LAN-ready, no Tomcat install needed).
# Needs: Java 17+ and Maven 3.8+ installed.
# Your friend runs the SAME script on their laptop after cloning.
set -e

# --- Find Java 17+ ---
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  : # use existing JAVA_HOME
elif [ -x "$HOME/tools/jdk17/Contents/Home/bin/java" ]; then
  export JAVA_HOME="$HOME/tools/jdk17/Contents/Home"
elif command -v java >/dev/null 2>&1; then
  : # use java from PATH
else
  echo "ERROR: Java 17+ not found."
  echo "Install: https://adoptium.net/temurin/releases (JDK 17, your OS)"
  exit 1
fi
if command -v java >/dev/null 2>&1; then
  java -version 2>&1 | head -n 1
fi

# --- Find Maven ---
if ! command -v mvn >/dev/null 2>&1; then
  if [ -x "$HOME/tools/apache-maven-3.9.6/bin/mvn" ]; then
    export PATH="$HOME/tools/apache-maven-3.9.6/bin:$PATH"
  else
    echo "ERROR: Maven not found."
    echo "Install: https://maven.apache.org/download.cgi (or: brew install maven)"
    exit 1
  fi
fi

DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"
mvn -q package -DskipTests
CP="target/classes:$(cat target/classpath.txt)"
IP=$(ipconfig getifaddr en0 2>/dev/null || ipconfig getifaddr en1 2>/dev/null || hostname -I 2>/dev/null | awk '{print $1}' || echo "YOUR-IP")
echo ""
echo "Open locally : http://localhost:8080"
echo "Same-WiFi friend opens: http://${IP}:8080"
echo "Stop with Ctrl+C"
echo ""
java -cp "$CP" com.campusfind.App
