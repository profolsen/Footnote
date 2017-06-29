#!/usr/bin/env bash
mkdir bin
javac -d bin $(find . -name "*.java")
cp -r samples bin/.
cp expected.txt bin/expected.txt
cd bin
java Test > actual.txt
diff actual.txt expected.txt
cd ..
rm -r bin