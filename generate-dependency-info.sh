
rm -f generated-runtime-deps-list.txt
rm -f generated-runtime-deps-tree.txt

mvn dependency:list -DincludeScope=runtime -DoutputFile=generated-runtime-deps-list.txt
mvn dependency:tree -Dscope=runtime -DoutputFile=generated-runtime-deps-tree.txt
 