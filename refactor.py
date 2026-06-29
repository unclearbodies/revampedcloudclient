import os
import shutil

src_dir = "/mnt/c/Users/SUVI IRL MINI/Downloads/cloudclient-develop/cloudclient-develop/1.8.9/cloudclient/src/main/java"

# 1. Move hudeditor/impl/impl -> hudeditor/impl
hud_impl_impl = os.path.join(src_dir, "dev/cloudmc/gui/hudeditor/impl/impl")
hud_impl = os.path.join(src_dir, "dev/cloudmc/gui/hudeditor/impl")

if os.path.exists(hud_impl_impl):
    for item in os.listdir(hud_impl_impl):
        shutil.move(os.path.join(hud_impl_impl, item), os.path.join(hud_impl, item))
    os.rmdir(hud_impl_impl)

# 2. Move modmenu/impl/sidebar/mods/impl -> modmenu/impl/sidebar/mods
mods_impl = os.path.join(src_dir, "dev/cloudmc/gui/modmenu/impl/sidebar/mods/impl")
mods_dir = os.path.join(src_dir, "dev/cloudmc/gui/modmenu/impl/sidebar/mods")

if os.path.exists(mods_impl):
    for item in os.listdir(mods_impl):
        shutil.move(os.path.join(mods_impl, item), os.path.join(mods_dir, item))
    os.rmdir(mods_impl)

# 3. Process all java files to fix package and imports
for root, dirs, files in os.walk(src_dir):
    for file in files:
        if file.endswith(".java"):
            path = os.path.join(root, file)
            with open(path, "r", encoding="utf-8") as f:
                content = f.read()

            new_content = content
            # Fix package declarations
            new_content = new_content.replace("package dev.cloudmc.gui.hudeditor.impl.impl;", "package dev.cloudmc.gui.hudeditor.impl;")
            new_content = new_content.replace("package dev.cloudmc.gui.hudeditor.impl.impl.keystrokes;", "package dev.cloudmc.gui.hudeditor.impl.keystrokes;")
            new_content = new_content.replace("package dev.cloudmc.gui.hudeditor.impl.impl.keystrokes.keys;", "package dev.cloudmc.gui.hudeditor.impl.keystrokes.keys;")
            new_content = new_content.replace("package dev.cloudmc.gui.modmenu.impl.sidebar.mods.impl;", "package dev.cloudmc.gui.modmenu.impl.sidebar.mods;")
            new_content = new_content.replace("package dev.cloudmc.gui.modmenu.impl.sidebar.mods.impl.type;", "package dev.cloudmc.gui.modmenu.impl.sidebar.mods.type;")

            # Fix imports
            new_content = new_content.replace("import dev.cloudmc.gui.hudeditor.impl.impl", "import dev.cloudmc.gui.hudeditor.impl")
            new_content = new_content.replace("import dev.cloudmc.gui.modmenu.impl.sidebar.mods.impl", "import dev.cloudmc.gui.modmenu.impl.sidebar.mods")

            if new_content != content:
                with open(path, "w", encoding="utf-8") as f:
                    f.write(new_content)
                print(f"Updated {path}")
