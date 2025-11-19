#!/bin/bash

javac @compile.list -d class
if [ $? -ne 0 ]; then
    echo "La compilation a échoué."
    exit 1
fi

java -cp class source.Controleur
if [ $? -ne 0 ]; then
    echo "L'exécution a échoué."
    exit 1
fi
echo "L'exécution s'est terminée avec succès."