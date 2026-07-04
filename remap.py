import os
import re
import csv
import urllib.request
import zipfile

# Download mappings
url = "https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_stable/22-1.8.9/mcp_stable-22-1.8.9.zip"
zip_path = "mcp_stable.zip"
urllib.request.urlretrieve(url, zip_path)

with zipfile.ZipFile(zip_path, 'r') as z:
    z.extractall("mcp_mappings")

methods = {}
fields = {}

with open("mcp_mappings/methods.csv", "r", encoding="utf-8") as f:
    reader = csv.reader(f)
    next(reader) # skip header
    for row in reader:
        methods[row[0]] = row[1]

with open("mcp_mappings/fields.csv", "r", encoding="utf-8") as f:
    reader = csv.reader(f)
    next(reader)
    for row in reader:
        fields[row[0]] = row[1]

src_dir = "/mnt/c/Users/SUVI IRL MINI/Downloads/revampedcloudclient-master/revampedcloudclient-master/src/main/java/first/rain/anticheat"

def replacer(match):
    name = match.group(0)
    if name.startswith("func_"):
        # it might have an underscore at the end, like func_73866_w_
        base = name.split('_')[0] + "_" + name.split('_')[1] + "_" + name.split('_')[2]
        return methods.get(base, name)
    elif name.startswith("field_"):
        base = name.split('_')[0] + "_" + name.split('_')[1] + "_" + name.split('_')[2]
        return fields.get(base, name)
    return name

pattern = re.compile(r'\b(func_[0-9]+_[a-zA-Z_]+|field_[0-9]+_[a-zA-Z_]+)\b')

count = 0
for root, dirs, files_in_dir in os.walk(src_dir):
    for file in files_in_dir:
        if file.endswith(".java"):
            path = os.path.join(root, file)
            with open(path, "r", encoding="utf-8") as f:
                content = f.read()
            
            new_content = pattern.sub(replacer, content)
            
            if new_content != content:
                with open(path, "w", encoding="utf-8") as f:
                    f.write(new_content)
                count += 1
                print(f"Updated {file}")

print(f"Done remapping {count} files.")
