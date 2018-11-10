# Weka-STPM
[![](https://jitpack.io/v/lucivanbatista/Weka-STPM.svg)](https://jitpack.io/#lucivanbatista/Weka-STPM)

### Branches
- Master: Interface gráfica + versão estável
- Jar: Sem Interface gráfica + Sem partes visuais + config.properties atualizado e funcionando
- Spring: Projeto Spring Boot + Sem Interface gráfica + Após o Jar (não funcionando com o CB)

### Application
- Update the file ```config.properties```
- Execute the ```main``` in ```src/main/java/App.java```

### Library
- Add the repository in ```pom.xml```
```xml
<repositories>
  <repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
  </repository>
</repositories>
```
- Add dependency in ```pom.xml```
```xml
<dependency>
  <groupId>com.github.lucivanbatista</groupId>
  <artifactId>Weka-STPM</artifactId>
  <version>v2.0.1</version>
</dependency>
```
