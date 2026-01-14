import csv

# Open the CSV file
with open("merged.csv", newline="", encoding="utf-8") as f:
    reader = csv.reader(f)
    rows = list(reader)

# Print Confluence-friendly Markdown table header
print("| S.No | IF Condition Field | Operator | Code List |")
print("|------|-------------------|----------|-----------|")

# Enumerate rows starting from 1
for i, row in enumerate(rows, start=1):
    # Defensive indexing in case of odd rows
    if len(row) < 4:
        continue

    if_field = row[1].strip()
    operator = row[2].strip()
    code_list = row[3].strip().strip('"')

    print(f"| {i} | {if_field} | {operator} | {code_list} |")
