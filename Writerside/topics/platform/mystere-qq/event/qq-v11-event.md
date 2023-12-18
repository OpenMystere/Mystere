# Event

每个事件都有 `time`、`self_id` 和 `post_type` 字段，如下：

| 字段名         | 数据类型           | 说明          |
|-------------|----------------|-------------|
| `id`        | string         | 事件 ID，全局唯一  |
| `time`      | number (int64) | 事件发生的时间戳    |
| `self_id`   | string         | 收到事件的机器人 ID |
| `post_type` | string         | 事件类型        |

其中 `post_type` 不同字段值表示的事件类型对应如下：

- `message`：[消息事件](qq-v11-event-message.md)
- `notice`：通知事件
- `request`：请求事件
- `meta_event`：元事件