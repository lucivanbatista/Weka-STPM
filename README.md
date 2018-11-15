# Weka-STPM
[![](https://jitpack.io/v/lucivanbatista/Weka-STPM.svg)](https://jitpack.io/#lucivanbatista/Weka-STPM)

### Branches
- Master: Interface gráfica (swing) + versão estável
- Jar: Sem Interface gráfica + Sem partes visuais + config.properties atualizado e funcionando
- Spring: Projeto Spring Boot + Interface gráfica (web) + Após o Jar (não funcionando com o CB)

### Application (branch Spring)
- Update the file ```config.properties```, it is necessary only the attributes of database connection
- Execute ```/src/main/java/weka/WekaStpmSpringApplication.java```
- In ```Enriquecimento Semântico``` choose the method and change the parameters if you want to. Put the configs to find the datas.

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
