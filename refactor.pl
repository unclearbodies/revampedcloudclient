use strict;
use warnings;
use File::Find;
use File::Copy;
use File::Path qw(make_path remove_tree);

my $src = "/mnt/c/Users/SUVI IRL MINI/Downloads/cloudclient-develop/cloudclient-develop/1.8.9/cloudclient/src/main/java";

my $hud_old = "$src/dev/cloudmc/gui/hudeditor/impl/impl";
my $hud_new = "$src/dev/cloudmc/gui/hudeditor/impl";

if (-d $hud_old) {
    opendir(my $dh, $hud_old) or die "Could not open $hud_old";
    while (my $f = readdir($dh)) {
        next if $f eq '.' or $f eq '..';
        rename("$hud_old/$f", "$hud_new/$f") or die "Failed to move $f";
    }
    closedir($dh);
    rmdir($hud_old);
}

my $mods_old = "$src/dev/cloudmc/gui/modmenu/impl/sidebar/mods/impl";
my $mods_new = "$src/dev/cloudmc/gui/modmenu/impl/sidebar/mods";

if (-d $mods_old) {
    opendir(my $dh, $mods_old) or die "Could not open $mods_old";
    while (my $f = readdir($dh)) {
        next if $f eq '.' or $f eq '..';
        rename("$mods_old/$f", "$mods_new/$f") or die "Failed to move $f";
    }
    closedir($dh);
    rmdir($mods_old);
}

find(sub {
    return unless -f && /\.java$/;
    
    my $file = $_;
    open(my $in, '<', $file) or return;
    my $content = do { local $/; <$in> };
    close($in);
    
    my $orig = $content;
    
    $content =~ s/package dev\.cloudmc\.gui\.hudeditor\.impl\.impl;/package dev.cloudmc.gui.hudeditor.impl;/g;
    $content =~ s/package dev\.cloudmc\.gui\.hudeditor\.impl\.impl\.keystrokes;/package dev.cloudmc.gui.hudeditor.impl.keystrokes;/g;
    $content =~ s/package dev\.cloudmc\.gui\.hudeditor\.impl\.impl\.keystrokes\.keys;/package dev.cloudmc.gui.hudeditor.impl.keystrokes.keys;/g;
    
    $content =~ s/package dev\.cloudmc\.gui\.modmenu\.impl\.sidebar\.mods\.impl;/package dev.cloudmc.gui.modmenu.impl.sidebar.mods;/g;
    $content =~ s/package dev\.cloudmc\.gui\.modmenu\.impl\.sidebar\.mods\.impl\.type;/package dev.cloudmc.gui.modmenu.impl.sidebar.mods.type;/g;
    
    $content =~ s/import dev\.cloudmc\.gui\.hudeditor\.impl\.impl/import dev.cloudmc.gui.hudeditor.impl/g;
    $content =~ s/import dev\.cloudmc\.gui\.modmenu\.impl\.sidebar\.mods\.impl/import dev.cloudmc.gui.modmenu.impl.sidebar.mods/g;
    
    if ($content ne $orig) {
        open(my $out, '>', $file) or die "Cannot write $file";
        print $out $content;
        close($out);
        print "Updated $File::Find::name\n";
    }
}, $src);
