# Aplicativo Android Nativo

Aplicativo Android nativo em Kotlin para autenticação e gerenciamento de registros. Possui telas de cadastro, dashboard, listagem, detalhes, edição, pesquisa, filtros, processamento e gráficos.

## Requisitos

- Android Studio
- JDK 11
- Android SDK com compileSdk 36
- Emulador Android ou dispositivo físico
- API Spring Boot executando na porta 8080

## Execução

Abra o projeto no Android Studio e execute o módulo `app`. Pela linha de comando no Windows:

```powershell
.\gradlew.bat assembleDebug
```

O APK de debug é gerado em `app/build/outputs/apk/debug/`.

## Configuração da API

As classes `ApiClient` e `UsuarioApiClient` definem a URL base. No emulador Android, use `http://10.0.2.2:8080/`. Em dispositivo físico, use o IP local do computador e confirme que ambos estão na mesma rede.

## Arquitetura e recursos

- Kotlin e AndroidX
- Room para persistência local
- Retrofit e Gson para comunicação HTTP
- Repository para sincronização entre API e banco local
- Activities e layouts XML para as telas do aplicativo
- CRUD de registros, autenticação, filtros, processamento e gráficos

## Integração

O aplicativo consome os endpoints `/auth/login`, `/usuarios` e `/registros` da API deste conjunto de projetos.

## Projeto relacionado

- API Spring Boot: `C:/Users/PICHAU/Desktop/Inteliji/androidnativeapi`
