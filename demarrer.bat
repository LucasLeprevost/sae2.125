@echo off

javac @compile.list -d class
if errorlevel 1 (
    echo La compilation a échoué.
    exit /b 1
)

java -cp class source.Controleur
if errorlevel 1 (
    echo L'exécution a échoué.
    exit /b 1
)

echo L'exécution s'est terminée avec succès.