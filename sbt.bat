@echo off
set SCRIPT_DIR=%~dp0
java -Dhttp.proxyHost=proxy.intellilink.co.jp -Dhttp.proxyPort=8080 -Xmx512M -jar "%SCRIPT_DIR%sbt-launch.jar" %*
