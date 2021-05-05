## local pc (macos) 에 elastic + kibana 구축

- docker 이용

1. elasticsearch image pull
    - docker pull docker.elastic.co/elasticsearch/elasticsearch:7.12.1

2. docker container 시작 (single node)
    - docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.12.1

3. kibana image pull 
    - docker pull docker.elastic.co/kibana/kibana:7.12.1

4. kibana container 시작
    - docker run --link YOUR_ELASTICSEARCH_CONTAINER_NAME_OR_ID:elasticsearch -p 5601:5601 docker.elastic.co/kibana/kibana:7.12.1
