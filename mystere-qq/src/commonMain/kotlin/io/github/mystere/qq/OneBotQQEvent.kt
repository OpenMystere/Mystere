package io.github.mystere.qq

enum class QQNoticeType {
    guild, channel, guild_member, audio_live_channel, group_robot, user_robot
}

enum class GuildEventType {
    create, update, delete,
}

enum class ChannelEventType {
    create, update, delete,
}

enum class GuildMemberEventType {
    add, update, remove,
}

enum class AudioLiveChannelEventType {
    enter, exit,
}

enum class GroupRobotEventType {
    add, del, reject, receive,
}

enum class UserRobotEventType {
    add, del, reject, receive,
}