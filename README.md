# Weka-STPM
[![](https://jitpack.io/v/lucivanbatista/Weka-STPM.svg)](https://jitpack.io/#lucivanbatista/Weka-STPM)

### Branches
- Master: Interface gráfica (swing) + versão estável
- Jar: Sem Interface gráfica + Sem partes visuais + config.properties atualizado e funcionando
- Spring: Projeto Spring Boot + Interface gráfica (web) + Após o Jar (não funcionando com o CB)

### Application (branch Spring)
- Update the file ```config.properties```, it is necessary only the attributes of database connection
- Execute ```/src/main/java/weka/WekaStpmSpringApplication.java```. A log will be create with the name ```log_file.txt```.
- In ```Enriquecimento Semântico``` choose the method and change the parameters if you want to. Put the configs to find the datas.
- It will be created in the database, the tables ```complete_(method)_stops_(name_choosed)``` and ```(method)_stops_(name_choosed)```. The first one has all the points known as 'stops', the second one has all the stops areas, those points known as 'stops' are in these areas. You can use the attribute ```gid_stop``` in the fisrt one and ```gid``` in the second one to make a join.

### TCC (Trabalho de Conclusão de Curso)
- To more informations about the work: http://www.repositorio.ufc.br/bitstream/riufc/39471/1/2018_tcc_jlbfreires.pdf

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
