import re

with open("petreminder-app/src/main/java/com/mehdi/petreminder/ConsoleApp.java", "r", encoding="utf-8") as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    # match method signatures but not if/while/for etc.
    # e.g.: "    private void methodName("
    # or "    public String methodName("
    # or "    public ConsoleApp("
    match = re.match(r"^(\s+)(public|private|protected)\s+(?:[\w<>\[\]]+\s+)?(\w+)\s*\(.*\)\s*(?:throws\s+[\w,\s]+)?\s*\{?\s*$", line)
    
    if match:
        # Check if the previous line is a comment or annotation
        prev = lines[i-1].strip() if i > 0 else ""
        if not prev.endswith("*/") and not prev.startswith("@"):
            indent = match.group(1)
            method_name = match.group(3)
            doc = f"{indent}/**\n{indent} * @brief {method_name} method.\n{indent} */\n"
            new_lines.append(doc)
            
    new_lines.append(line)

with open("petreminder-app/src/main/java/com/mehdi/petreminder/ConsoleApp.java", "w", encoding="utf-8") as f:
    f.writelines(new_lines)

print("Doxygen comments added successfully!")
