#!/bin/bash

# Nombre de tâches à générer
NUM_TASKS=$2
# Fichier de sortie
OUTPUT_FILE=$1

# Nettoyer le fichier de sortie
> "$OUTPUT_FILE"

# Liste pour stocker les noms de tâches déjà créées
declare -a TASK_NAMES

# Fonction pour générer un nom de tâche (A, B, C, ...)
get_task_name() {
  local index=$1
  # Convertit en lettres majuscules (A-Z, AA, AB, etc.)
  local name=""
  while (( index >= 0 )); do
    name="$(printf "\\$(printf '%03o' $((65 + index % 26)))")$name"
    index=$(( index / 26 - 1 ))
  done
  echo "$name"
}

# Générer les tâches
for ((i=0; i<NUM_TASKS; i++)); do
  task_name=$(get_task_name $i)
  TASK_NAMES+=("$task_name")

  # Durée aléatoire entre 1 et 5
  duration=$(( RANDOM % 5 + 1 ))

  # Dépendances : on peut dépendre de tâches précédentes uniquement
  deps=""
  if (( i > 0 )); then
    num_deps=$(( RANDOM % i ))  # nombre de dépendances possibles
    selected_deps=($(shuf -e "${TASK_NAMES[@]:0:$i}" -n "$num_deps"))
    deps=$(IFS=, ; echo "${selected_deps[*]}")
  fi

  echo "$task_name|$duration|$deps" >> "$OUTPUT_FILE"
done

cp -r "$OUTPUT_FILE" ./data/$(basename "$OUTPUT_FILE")
rm -f "$OUTPUT_FILE"

echo "Fichier '$OUTPUT_FILE' généré avec $NUM_TASKS tâches."
