import csv
import re

glove_file = 'glove.6B.50d.txt'
csv_file = 'songs.csv'
filtered_file = 'filtered_model.txt'

# Create a set to store the words from the songs.csv file
csv_words = set()

def split_string_to_words(string):
    # Split the string at whitespace and punctuation marks
    words = re.findall(r'\w+|[^\w\s]', string)
    return words

# Read the songs.csv file and extract the words from all columns
with open(csv_file, 'r', encoding='utf-8') as csvfile:
    reader = csv.reader(csvfile)
    for row in reader:
        for word in row:
            words = split_string_to_words(word)
            for final_word in words:
                csv_words.add(final_word)


# Filter the glove file and save the filtered lines to a new file
with open(glove_file, 'r', encoding='utf-8') as glovefile, open(filtered_file, 'w', encoding='utf-8') as filteredfile:
    for line in glovefile:
        word = line.split()[0]  # Extract the first word from each line
        if word in csv_words:
            filteredfile.write(line)
