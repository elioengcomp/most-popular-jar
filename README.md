# most-popular-jar

This java application search for the most popular Jar files inside an Artifactory maven repository.

## Repository
[most-popular-jar](http://35.184.110.231:8083/artifactory/webapp/#/artifacts/browse/tree/Watch/release-local/marcolino/elio/most-popular-jar)

## Maven
```
<dependency>
  <groupId>marcolino.elio</groupId>
  <artifactId>most-popular-jar</artifactId>
  <version>${version}</version>
</dependency>
```

## Standalone Usage

- **MainClass**: [marcolino.elio.mpj.CommandLineHandler](https://github.com/elioengcomp/most-popular-jar/blob/master/src/main/java/marcolino/elio/mpj/CommandLineHandler.java)
- **Command line arguments**:
  - **-u,--url**: Artifactory url in format _http|s://host:port/artifactory_
  - **-a,--auth**: Artifactory authentication token
  - **-r,--repo**: Repository to search for the most popular jar files 
  - **-s,--size**: (Optional) Ranking size. Default: 2
  - **-w,--workers**: (Optional) Max number of concurrent workers. Default: 1
  - **-t,--threads**: (Optional) Max number of concurrent threads per worker. Default: 1  
  - **-p,--page**: Page size per worker. Optional if authentication token provided has admin privileges. In this case, will use Artifactory instance `artifactory.search.userQueryLimit` property value.
  
  
  
