@echo off
cd ..

echo Maven clean started.
powershell "mvn clean"
echo Maven clean finished.
echo Maven packaging started.
powershell "mvn package"
echo Maven packaging finished.
