Cache Test
==========

To run this, edit ehcache.xml and change

```
<terracottaConfig url="localhost:9510"/>
to
<terracottaConfig url="terracotta_host_ip:9510"/>
```

If the DSO port is different, then change 9510 to the new DSO port.

do a mvn package and deploy as usual.  
