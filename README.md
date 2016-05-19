### Message Format ###
#### General Format ####
```
<CR><message_type>[<size>]<LF>[<MESSAGE>]
<message_type> : 1byte
<sender_id> : 32 bytes
<received_id> : 32 bytes
<size> : 8 bytes
```

#### Add friend ####
```
<CR><message_type><LF>[<MESSAGE>]
<message_type> = 0
<MESSAGE> = invite message
```