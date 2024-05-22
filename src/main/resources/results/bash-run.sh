#!/bin/bash

# Define the sentences
sentences=(
    "The leaves turned golden in the autumn breeze."
    "A gentle breeze flowed through the open window."
    "She painted a beautiful landscape on the canvas."
    "The coffee shop was bustling with activity."
    "He solved the complex equation effortlessly."
    "The orchestra played a symphony flawlessly."
    "A rainbow appeared after the heavy rain."
    "The children laughed as they flew their kites."
    "She wrote her thoughts in a leather-bound journal."
    "The garden was full of vibrant flowers."
    "He jogged along the river every morning."
    "The library was quiet, filled with readers."
    "She prepared a delicious meal for her family."
    "The fisherman cast his line into the tranquil lake."
    "They danced under the stars at the festival."
    "The bakery displayed a variety of pastries."
    "He read a captivating novel by the fireplace."
    "The sunset painted the sky in shades of pink."
    "The cat curled up in the warm sunlight."
    "She listened to music while working on her art."
    "The train journeyed through the scenic countryside."
    "They enjoyed a picnic by the serene lake."
    "He crafted a wooden table in his workshop."
    "The museum showcased ancient artifacts."
    "She practiced yoga to relax and unwind."
)

url="http://localhost:11434/api/embeddings"
model="nomic-embed-text"

# Start timing
start_time=$(date +%s)

# Loop through each sentence and make a request
for sentence in "${sentences[@]}"; do
    response=$(curl -s -d "{\"model\": \"$model\", \"prompt\": \"$sentence\"}" "$url")

echo "Response: $response"
#    if [ "$http_code" -eq 200 ]; then
#        echo "Successfully got embedding for: \"$sentence\""
#        echo "Response: $response"
#    else
#        echo "Failed to get embedding for: \"$sentence\". Status code: $http_code"
#    fi
done

# End timing
end_time=$(date +%s)
total_time=$((end_time - start_time))

# Print total time taken
echo "Total time taken: $total_time seconds"
