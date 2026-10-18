package space.alven.andx.sock2vpn.Util


/**
 * @property CONNECTING SSH client constructing
 * @property AUTHENTICATING
 * @property CONNECTED SSH link
 */
enum class LnkState {
    STOPPED,
    CONNECTING,
    AUTHENTICATING,
    CONNECTED,
    RECONNECTING,
    ERROR
}