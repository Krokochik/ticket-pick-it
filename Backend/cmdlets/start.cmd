@echo off
cd ..
for %%f in (./target/server.jar) do set jarfile=%%f
if defined jarfile (
  echo found jar: %jarfile%.
  powershell "java -jar ./target/%jarfile%"
) else (
  echo jar not found.
)