function ChatService() {
    this.receiver = new Receiver();
    this.emitter = new Emitter('ChatService');
}

var chatService = new ChatService();

chatService.sendMessage = function(message) {
    this.emitter.emit('sendMessage', {message: message});
}

chatService.onMessage = function(callback) {
   this.receiver.receive('onMessage', callback);
}


