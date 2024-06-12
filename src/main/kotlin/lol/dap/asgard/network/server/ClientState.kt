package network.server

enum class ClientState {

    NONE,
    STATUS,
    HANDSHAKE,
    LOGIN,
    PLAY

}