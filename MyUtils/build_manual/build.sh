#!/usr/bin/env bash

. "${0%/*}/utils.sh"

function finish {
    warning "SIGINT received. Exiting..."
    cleanup
    exit
}
trap finish SIGINT

function cleanup() {
    rm -v MANIFEST.MF
}

while [[ $# -gt 0 ]]; do
    key="$1"
    case $key in
        -c|--clean)
            cleanRequested=true
            shift
            ;;
        -t|--test)
            testsRequested=true
            shift
            ;;
        *)
            die "'$key' is an invalid parameter. Exiting..."
    esac
done

requireDeps "javac jar java javadoc"
dest="${0%/*}/../dist"
src="../sources/"
libraryName="StringSlicer.jar"
appName="stringslicer_test.jar"
mainClass="be.wget.inpres.java.restaurant.myutils.tests.StringSlicerTest1"
oldPath="${PWD}"

mkdir -p "$dest"
cd "$dest"

if [ "$cleanRequested" = "true" ]; then
    info "Cleaning '$dest' content..."
    rm -r ./*
fi

# Don't use for loop, they are vulnerable. Use a while loop with "here
# strings".
# src.: http://mywiki.wooledge.org/BashFAQ/001
while IFS= read -r file; do
    info "Compiling file \"$file\" to Java bytecode..."
    javac -classpath "$src" -d . "$file" -Xlint:unchecked
done <<< "$(find "$src" -type f -name '*.java')"

info "Compilation terminated."

info "Gathering library class files..."
classFiles=()
while IFS= read -r file; do
    classFiles+=("$file")
done <<< "$(find . -type f -name '*.class' ! -name '*test*')"

info "Creating library jar..."
# By default, even if a standard manifest file is created automatically, we
# need a custom one to specify a Main-Class and a Class-Path
cat <<EOF > MANIFEST.MF
Manifest-Version: 1.0
Created-By: Manual compilation by William Gathoye

EOF
# Do not specify the path as argument to the jar executable, otherwise, it will
# create an archive with the destination subfolder in it.
jar cvfm "$libraryName" MANIFEST.MF "${classFiles[@]}"

info "Gathering test class files..."
classFiles=()
while IFS= read -r file; do
    classFiles+=("$file")
done <<< "$(find . -type f -iname '*test*.class')"

cat <<EOF > MANIFEST.MF
Manifest-Version: 1.0
Created-By: Manual compilation by William Gathoye
Class-Path: StringSlicer.jar
Main-Class: $mainClass

EOF
jar cvfm "$appName" MANIFEST.MF "${classFiles[@]}"

info "Creating javadoc..."
javadoc -d "docs/" "$src/be/wget/inpres/java/restaurant/myutils/StringSlicer.java"

cleanup

if [ "$testsRequested" = "true" ]; then
    cd "$oldPath"
    info "Executing tests..."
    info "Testing \"java -classpath $dest $mainClass\""
    java -classpath "$dest" "$mainClass"
    info "Testing \"java -jar $dest/$appName\""
    java -jar "$dest/$appName"
    cd "$dest"
fi

info "Usage:"
info "Execute the main executable with \"java -classpath $dest $mainClass\""
info "OR"
info "Execute the jar file with \"java -jar $dest/$appName\""

