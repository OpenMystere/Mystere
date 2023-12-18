# 消息事件

## 单聊消息

对应 QQ 开放平台：[单聊消息](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/event.html#单聊消息)

### 事件数据

| 字段名            | 数据类型           | 可能的值      | 说明            |
|----------------|----------------|-----------|---------------|
| `time`         | number (int64) | -         | 事件发生的时间戳      |
| `self_id`      | string         | -         | 收到事件的机器人 QQ 号 |
| `post_type`    | string         | `message` | 上报类型          |
| `message_type` | string         | `private` | 消息类型          |
| `sub_type`     | string         | `friend`  | 消息子类型         |
| `message_id`   | string         | -         | 消息 ID         |
| `user_id`      | string         | -         | 发送者 QQ 号      |
| `message`      | message        | -         | 消息内容          |
| `raw_message`  | string         | -         | 原始消息内容        |
| `font`         | number (int32) | -         | 字体            |
| `sender`       | object         | -         | 发送人信息         |

其中 `sender` 字段的内容如下：

| 字段名       | 数据类型   | 说明     |
|-----------|--------|--------|
| `user_id` | string | 发送者 ID |


## 群消息

对应 QQ 开放平台：[群聊@机器人](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/event.html#群聊-机器人)

### 事件数据

| 字段名            | 数据类型           | 可能的值      | 说明          |
|----------------|----------------|-----------|-------------|
| `time`         | number (int64) | -         | 事件发生的时间戳    |
| `self_id`      | string         | -         | 收到事件的机器人 ID |
| `post_type`    | string         | `message` | 上报类型        |
| `message_type` | string         | `group`   | 消息类型        |
| `sub_type`     | string         | `group`   | 消息子类型       |
| `message_id`   | string         | -         | 消息 ID       |
| `group_id`     | string         | -         | 群号          |
| `user_id`      | string         | -         | 发送者 ID      |
| `message`      | message        | -         | 消息内容        |
| `raw_message`  | string         | -         | 原始消息内容      |
| `sender`       | object         | -         | 发送人信息       |

`sender` 字段的内容如下：

| 字段名       | 数据类型   | 说明     |
|-----------|--------|--------|
| `user_id` | string | 发送者 ID |

## 频道消息

对应 QQ 开放平台：[文字子频道全量消息（私域）](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/event.html#文字子频道全量消息-私域) 和 [文字子频道@机器人](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/event.html#文字子频道-机器人)

### 事件数据

| 字段名            | 数据类型           | 可能的值      | 说明          |
|----------------|----------------|-----------|-------------|
| `time`         | number (int64) | -         | 事件发生的时间戳    |
| `self_id`      | string         | -         | 收到事件的机器人 ID |
| `post_type`    | string         | `message` | 上报类型        |
| `message_type` | string         | `guild`   | 消息类型        |
| `sub_type`     | string         | `channel` | 消息子类型       |
| `message_id`   | string         | -         | 消息 ID       |
| `guild_id`     | string         | -         | 频道 ID       |
| `channel_id`   | string         | -         | 子频道 ID      |
| `user_id`      | string         | -         | 发送者 ID      |
| `message`      | message        | -         | 消息内容        |
| `raw_message`  | string         | -         | 原始消息内容      |
| `sender`       | object         | -         | 发送人信息       |

`sender` 字段的内容如下：

| 字段名       | 数据类型   | 说明     |
|-----------|--------|--------|
| `user_id` | string | 发送者 ID |

## 频道私聊消息

对应 QQ 开放平台：[文字子频道全量消息（私域）](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/event.html#文字子频道全量消息-私域) 和 [文字子频道@机器人](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/event.html#文字子频道-机器人)

### 事件数据

| 字段名            | 数据类型           | 可能的值      | 说明          |
|----------------|----------------|-----------|-------------|
| `time`         | number (int64) | -         | 事件发生的时间戳    |
| `self_id`      | string         | -         | 收到事件的机器人 ID |
| `post_type`    | string         | `message` | 上报类型        |
| `message_type` | string         | `private` | 消息类型        |
| `sub_type`     | string         | `channel` | 消息子类型       |
| `message_id`   | string         | -         | 消息 ID       |
| `guild_id`     | string         | -         | 频道 ID       |
| `channel_id`   | string         | -         | 子频道 ID      |
| `user_id`      | string         | -         | 发送者 ID      |
| `message`      | message        | -         | 消息内容        |
| `raw_message`  | string         | -         | 原始消息内容      |
| `sender`       | object         | -         | 发送人信息       |

`sender` 字段的内容如下：

| 字段名       | 数据类型   | 说明     |
|-----------|--------|--------|
| `user_id` | string | 发送者 ID |

