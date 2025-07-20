@echo off
echo ===========================================
echo    TASK MANAGER API - TCC
echo    Script de Inicio Rapido
echo ===========================================
echo.

echo [1/5] Verificando Java...
java -version
if %errorlevel% neq 0 (
    echo ERROR: Java no esta instalado o no esta en el PATH
    echo Por favor instala Java 21 o superior
    pause
    exit /b 1
)
echo ✓ Java verificado
echo.

echo [2/5] Verificando Gradle Wrapper...
if not exist "gradlew.bat" (
    echo ERROR: Gradle Wrapper no encontrado
    echo Asegurate de estar en el directorio correcto del proyecto
    pause
    exit /b 1
)
echo ✓ Gradle Wrapper encontrado
echo.

echo [3/5] Limpiando proyecto anterior...
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo ERROR: Error al limpiar el proyecto
    pause
    exit /b 1
)
echo ✓ Proyecto limpiado
echo.

echo [4/5] Compilando proyecto...
call gradlew.bat build -x test
if %errorlevel% neq 0 (
    echo ERROR: Error al compilar el proyecto
    echo Revisa los errores de compilacion
    pause
    exit /b 1
)
echo ✓ Proyecto compilado exitosamente
echo.

echo [5/5] Iniciando aplicacion...
echo.
echo ===========================================
echo    INFORMACION IMPORTANTE
echo ===========================================
echo.
echo 1. Asegurate de que SQL Server este ejecutandose
echo 2. Verifica que la base de datos TaskManagerDB exista
echo 3. Revisa la configuracion en application.properties
echo.
echo URLs importantes:
echo - Aplicacion: http://localhost:8080
echo - Swagger UI: http://localhost:8080/swagger-ui/index.html
echo - Health Check: http://localhost:8080/actuator/health
echo.
echo Presiona Ctrl+C para detener la aplicacion
echo ===========================================
echo.

call gradlew.bat bootRun --args='--spring.profiles.active=local'

echo.
echo Aplicacion detenida
pause 