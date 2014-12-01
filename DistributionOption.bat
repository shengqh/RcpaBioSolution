set cp=
for %%i in (..\RcpaBioJava\lib\*.jar) do call cp.bat %%i
set cp=%cp%;.

java -classpath %CP%; org.exolab.castor.builder.SourceGenerator -types j2 -i DistributionOption.xsd -package cn.ac.rcpa.bio.tools.distribution.option -dest .\src