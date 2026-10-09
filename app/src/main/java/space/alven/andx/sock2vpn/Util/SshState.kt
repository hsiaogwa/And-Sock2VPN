package space.alven.andx.sock2vpn.Util

enum class SshState {
    STOPPED,
    CONNECTING,
    AUTHENTICATING,
    CONNECTED,
    RECONNECTING,
    ERROR
}