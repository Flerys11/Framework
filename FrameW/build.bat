@REM For the framework compilation

set "FRAMEWORK_FOLDER=D:\ETU002044\Framew\FrameW\"
set FRAMEWORK="D:\ETU002044\Framew\FrameW\Framework"
set TOMCAT="C:\apache-tomcat-8.5.90\webapps"
set TESTFRAMEWORK="D:\ETU002044\Framew\FrameW\TestFramework"

javac -d %FRAMEWORK%\bin %FRAMEWORK%\\*.java
cd %FRAMEWORK%\\bin\\
jar -cvf %FRAMEWORK_FOLDER%\framework.jar .
cd %FRAMEWORK_FOLDER%

copy framework.jar %TESTFRAMEWORK%\lib

@REM For the test compilation

mkdir temp
mkdir temp\\WEB-INF
mkdir temp\\WEB-INF\\lib
mkdir temp\\WEB-INF\\classes

xcopy %TESTFRAMEWORK%\\*.jsp temp
xcopy %TESTFRAMEWORK%\\lib temp\\WEB-INF\\lib
javac -cp temp\\WEB-INF\\lib\\framework.jar -d temp\\WEB-INF\\classes %TESTFRAMEWORK%\\Test\\*.java
xcopy %TESTFRAMEWORK%\\*.xml temp\\WEB-INF
@REM rmdir -r temp

cd temp
jar -cvf %FRAMEWORK_FOLDER%webtest.war .
cd ../
rmdir temp

xcopy webtest.war %TOMCAT%
